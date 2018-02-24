package controllers

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick._
import play.api.libs.json._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import model.Tables._

object HomeController {
  case class KashiData(kashi: String, title: String, artist: String)
  implicit val kashiRowWritesFormat = new Writes[KashiRow] {
    def writes(kashi: KashiRow): JsValue = {
      Json.obj(
        "kashi" -> kashi.kashi,
        "title" -> kashi.title,
        "artist" -> kashi.artist
      )
    }
  }

  case class WordData(word: String)
  implicit val WordRowWritesFormat = new Writes[WordRow] {
    def writes(word: WordRow): JsValue = {
      Json.obj(
        "word" -> word.word
      )
    }
  }
}

@Singleton
class HomeController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

  import HomeController._

  /**
  * DB接続確認
  **/
  def index = Action.async { implicit req =>
    db.run(Kashi.result).map(kashi =>
      Ok(views.html.index())
    )
  }

  def getKashi = Action.async { implicit req =>
    db.run(Kashi.filter(_.id < 10).result).map(kashi =>
      Ok(Json.obj("kashi" -> kashi)).withHeaders(
                        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
                        ACCESS_CONTROL_ALLOW_HEADERS -> "*",
                        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true",
                        ACCESS_CONTROL_ALLOW_METHODS -> "GET"
                    )
    )
  }

  /**
  * ランダムに歌詞（文章単位）を取得
  **/
  def getRandomWord = Action.async { implicit req =>
    // 乱数のListを作成
    val random = new Random
    val ids = random.shuffle((0 to 10000).toList).take(50)
    db.run {
      Word.filter(_.id inSetBind ids)
      .result
    }.map(word =>
      Ok(Json.obj("word" -> word)).withHeaders(
                        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
                        ACCESS_CONTROL_ALLOW_HEADERS -> "*",
                        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true",
                        ACCESS_CONTROL_ALLOW_METHODS -> "GET"
                    )
    )
  }
}
