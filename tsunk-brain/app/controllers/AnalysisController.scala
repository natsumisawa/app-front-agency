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

import models.Tables._

@Singleton
class AnalysisController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

  case class AnalysisData(request_id: String, word_list: Seq[Seq[Seq[String]]])
  implicit val analysisDataReads  = Json.reads[AnalysisData]

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
        morpheme <- words
      } yield {
        val wordResult = new StringBuilder
        words.map(e => wordResult.append(e(0)))
        List(List(wordResult.toString), morpheme)
      }
      val moldedWordData = filteredWords.groupBy(_.head.head).mapValues(value => value.map(e => e(1)))

      for (kv <- moldedWordData) {
        val (k,v) = kv
        val wordData = WordRow(0, k, kashiId)
        db.run(Word returning Word.map(_.id)  += wordData).map {wordId =>
          v.map {mor =>
            val morphemeData = MorphemeRow(0, mor.head, wordId)
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
