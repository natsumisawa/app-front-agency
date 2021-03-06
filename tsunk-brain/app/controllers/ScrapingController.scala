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

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

import model.Tables._

@Singleton
class  ScrapingController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

  /**
  * すべての歌詞をスクレイピングし、DBに保存する
  **/
  def catchWords = Action { implicit request =>
    val pageNum = List("1", "2", "3", "4", "5", "6", "7")
    for {
      n <- pageNum
      parentPageDoc = JsoupBrowser().get("https://www.uta-net.com/search/?Aselect=3&Bselect=3&Keyword=%E3%81%A4%E3%82%93%E3%81%8F&sort=&pnum=" + n)
      // 親ページから200曲分の歌詞ページURLを取得
      area <- parentPageDoc >> elements("table > tbody > tr > td")
      url = area >> (attrs("href")("a"))
      songPageUrl <- url.filter(url => url != Nil).filter(url => url.startsWith("/song"))
      // 1ページずつ見に行き歌詞を取得
      doc = JsoupBrowser().get("https://www.uta-net.com" + songPageUrl)
      area <- doc >> elements("#flash_area")
      words = area >> text("#kashi_area")
      title = doc >> elements("div.title") >> text("h2")
      artist = doc >> elements("div.kashi_artist") >> text("span")
    } yield {
      val kashiData = KashiRow(0, words, title, artist)
      db.run(Kashi += kashiData).map(_ =>
        Ok
      )
    }
    Ok
  }
}
