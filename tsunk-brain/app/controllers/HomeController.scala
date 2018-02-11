package controllers

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick._
import play.api.libs.json._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import models.Tables._

@Singleton
class HomeController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

  /**
  * DB接続確認
  **/
  def index = Action.async { implicit req =>
    db.run(Kashi.result).map(kashi =>
      Ok(views.html.index(kashi))
    )
  }
}
