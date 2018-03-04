package controllers

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

  case class MorphemeData(
    surface: String,
    pos: String
  )

  /**
  * 形態素解析APIにリクエストを送り、解析結果をMorphemeテーブルに保存
  **/
  def analysisByGooApi = Action { implicit request =>

    db.run(Kashi.filter(e => e.id < 100).result).map { result =>
      result.map { e =>
        val word = e.kashi
        val appId = "dj00aiZpPWVJeTJVOGZpN3BhayZzPWNvbnN1bWVyc2VjcmV0Jng9YTU-"
        val yahooApiUrl = s"https://jlp.yahooapis.jp/MAService/V1/parse?appid=${appId}&results=ma,uniq&uniq_filter=9%7C10&sentence=${word}"
        // 歌詞データをAPIに送る
        WS.url(yahooApiUrl).get().map {
          response =>
            val res = response.xml.toString
            // res = ...<word><surface>sur</surface><reading>yomi</reading><pos>pos</pos></word>..
            // resのsurとposだけ取得する
            val index = res.indexOf("<word>")
            val word = _getContent(res.drop(index), "word")
            val result = word.map(e => {
              val index = e.indexOf("<pos>")
              val (sur, pos) = e.splitAt(index)
              MorphemeData(_getContent(sur, "surface").head, _getContent(pos, "pos").head)
            })
            _insertMorphemeData(result, e.id)
        }
      }
    }

    // 取得したいデータに対応するタグ(symbol)の中身だけ取得する
    def _getContent(str: String, symbol: String, result: List[String] = Nil):List[String] = {
      str match {
        case x if x.startsWith(s"<${symbol}>") => _getContent(x.drop(symbol.length + 2), symbol, result)
        case x if x.length == 0 => result.reverse
        case x => {
          val index = x.indexOf(s"</${symbol}>")
          // 不要なデータに対応するタグがきたらループを抜ける
          if (x.startsWith("<reading>") || x.startsWith("</word_list>")) result.reverse
          else _getContent(x.drop(index + (symbol.length + 3)), symbol, x.take(index) :: result)
        }
      }
    }

    // TODO(sawa): Repository層に移植する
    def _insertMorphemeData(morphemeList: List[MorphemeData], songId: Int): Unit = {
      morphemeList.filter(morpheme => morpheme.pos != "特殊").map { morpheme =>
        val morphemeRow = MorphemeRow(0, morpheme.surface, morpheme.pos, songId)
        db.run(Morpheme += morphemeRow).map(_ => Ok)
      }
    }

    // GooAPIからアクセス拒否されたのでコメントアウト
    // db.run(Kashi.filter(data => data.id < 100).result).map { kashi =>
    //   kashi.map {ele =>
    //     val data = Json.obj(
    //       "app_id" -> "3746eabd0f585048486a5c5ffae307204bcb1302e0b5bbd83a6259569a5e8ee5",
    //       "sentence" -> "なんてバイトを 変わったくらいで 差をつけられた感じ"
    //     )
    //     val gooUrl = "https://labs.goo.ne.jp/api/morph"
    //     WS.url(gooUrl).withHeaders("Accept" -> "application/json").post(data).map{
    //       response =>
    //         val result = response.json.validate[AnalysisData]
    //         insertWord(result, ele.id)
    //     }
    //   }
    // }
    Ok
  }
}
