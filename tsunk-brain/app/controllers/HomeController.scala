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

  case class MorphemeData(
    morpheme: String
  )

  implicit val MorphemeRowWritesFormat = new Writes[MorphemeRow] {
    def writes(morpheme: MorphemeRow): JsValue = {
      Json.obj(
        "surface" -> morpheme.surface,
        "pos" -> morpheme.pos
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
  * 形態素解析データをランダム取得
  **/
  // TODO(sawa): Repository層に移植
  def getRandomWord = Action.async { implicit req =>

    def _sortMorpheme(result: Seq[Morpheme]): Seq[Morpheme] = {
      println(result.map(_.pos))
      result
    }

    // 乱数のListを作成
    val random = new Random
    val ids = random.shuffle((0 to 10000).toList).take(100)
    db.run {
      Morpheme.filter(_.id inSetBind ids)
      .result
    }.map(morpheme =>
      // _sortMorpheme(morpheme)
      Ok(Json.obj("morphemes" -> morpheme)).withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
        ACCESS_CONTROL_ALLOW_HEADERS -> "*",
        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true",
        ACCESS_CONTROL_ALLOW_METHODS -> "GET"
      )
    )
  }
}
