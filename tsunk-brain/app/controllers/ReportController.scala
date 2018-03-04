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
import play.filters.csrf._
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import model.Tables._

@Singleton
class ReportController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

  case class ReportForm(word: String)
  implicit val reportFormReads  = Json.reads[ReportForm]

  val reportForm = Form(
    mapping(
        "word"  -> nonEmptyText
    )(ReportForm.apply)(ReportForm.unapply)
  )

  /**
  * 頻出順に名詞を取得
  **/
  def getFrequentWords = Action.async { implicit req =>
    db.run {
      Morpheme
        .filter(m => m.pos === "名詞" || m.pos === "Alphabet")
        .groupBy(m => m.surface)
        .map{ case (surface, group) => (surface, group.length)}
        .sortBy{ case (surface, count) => count.desc}
        .filter{ case (surface, count) => count =!= 1}
        .result
    }.map {result =>
      Ok(Json.obj(
        "surface" -> result.map(_._1),
        "count" -> result.map(_._2)
      )).withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
        ACCESS_CONTROL_ALLOW_HEADERS -> "*",
        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true",
        ACCESS_CONTROL_ALLOW_METHODS -> "GET"
      )
    }
  }
}
