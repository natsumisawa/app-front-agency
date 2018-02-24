package controllers

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.db.slick._
import play.api.Play.current
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import model.Tables._

@Singleton
class AnalysisController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

  case class AnalysisData(request_id: String, word_list: Seq[Seq[Seq[String]]])
  implicit val analysisDataReads  = Json.reads[AnalysisData]

  case class WordDataOfApiResponse(
    word: String,
    morpheme: MorphemeDataOfApiResponse
  )

  case class MorphemeDataOfApiResponse(
    morpheme: String,
    morphemeClass: String
  )

  /**
  * 形態素解析GooAPIにPOSTリクエストを送り、レスポンスをパースする
  * HowTo
  * 1. 一行目のfilterでdata.idの範囲を調整する（apiたたきすぎるとリクエスト拒否されるので気をつける）
  * 2. apiに送るsentenceをele.kashiにする
  **/
  def analysisByGooApi = Action { implicit request =>
    db.run(Kashi.filter(data => data.id === 1).result).map { kashi =>
      kashi.map {ele =>
        val data = Json.obj(
          "app_id" -> "3746eabd0f585048486a5c5ffae307204bcb1302e0b5bbd83a6259569a5e8ee5",
          "sentence" -> "ここをele.kashiにする"
        )
        val gooUrl = "https://labs.goo.ne.jp/api/morph"
        WS.url(gooUrl).withHeaders("Accept" -> "application/json").post(data).map{
          response =>
            val result = response.json.validate[AnalysisData]
            insertWord(result, ele.id)
        }
      }
    }
    Ok
  }

  /**
  * 形態素解析されたワードをDBに保存する
  **/
  def insertWord(result: JsResult[AnalysisData], kashiId: Int): Unit = {
    result.map(analysisData => {
      val wordsList = analysisData.word_list
      val filteredWords = for {
        words <- wordsList
        morpheme <- words.filter(w => w(1) != "空白")
      } yield {
        WordDataOfApiResponse(
          word = words.map(e => e(0)).mkString,
          morpheme = MorphemeDataOfApiResponse(morpheme(0), morpheme(1))
        )
      }
      val moldedWordData = filteredWords.groupBy(_.word).mapValues {v =>
        v.map(e => e.morpheme)
      }
      // @return Map(ここをele.kashiにする。 -> List(Morpheme(ここ,名詞), Morpheme(を,格助詞), Morpheme(ele,Alphabet), Morpheme(.,句点), Morpheme(kashi,Roman), Morpheme(に,格助詞), Morpheme(する,動詞語幹), Morpheme(。,句点)), こんにちは。 -> List(Morpheme(こんにちは,独立詞), Morpheme(。,句点)))

      for (kv <- moldedWordData) {
        val (k,v) = kv
        val wordData = WordRow(0, k, kashiId)
        db.run(Word returning Word.map(_.id)  += wordData).map {wordId =>
          v.map {morpheme =>
            val morphemeData = MorphemeRow(0, morpheme.morpheme, Option(morpheme.morphemeClass), wordId)
            db.run(Morpheme += morphemeData).map {_ =>
              Ok
            }
          }
        }
      }
    })
    println("success!!")
  }
}
