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
    val parentPageDoc = JsoupBrowser().get("https://www.uta-net.com/search/?Aselect=3&Bselect=3&Keyword=%E3%81%A4%E3%82%93%E3%81%8F&sort=&pnum=1")
    val urlList = for {
      area <- parentPageDoc >> elements("table > tbody > tr > td")
      url = area >> (attrs("href")("a"))
    } yield {
      url.filter(url => url != Nil).filter(url => url.startsWith("/song"))
    }

    val wordsResult = for {
      url <- urlList
      songPageUrl <- url
      doc = JsoupBrowser().get("https://www.uta-net.com" + songPageUrl)
      area <- doc >> elements("#flash_area")
      words = area >> text("#kashi_area")
    } yield words

    println(wordsResult)

    Ok(views.html.index())
  }
}
