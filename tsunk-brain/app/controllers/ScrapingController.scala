package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

@Singleton
class  ScrapingController @Inject() extends Controller {

  def catchWords = Action { implicit request =>
    // TODO(sawa): ↓ださめなので検討（(1 to 10).toList.map(_.toString)だとdocument取得できない）
    val pageNum = List("1", "2", "3", "4", "5", "6", "7")
    val result = for {
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
    } yield words

    println(result)

    Ok(views.html.index())
  }
}
