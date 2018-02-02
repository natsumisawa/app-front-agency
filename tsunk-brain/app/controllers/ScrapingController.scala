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
    val doc = JsoupBrowser().get("https://weather.yahoo.co.jp/weather/")
    for {
      area <- doc >> elements("#map > ul > li > a > dl")
      (name, weather) = area >> (text("dt"), texts("dd"))
    } println(s"$name - ${weather.mkString(" ")}")
    println("--------------")
    val songsDoc = JsoupBrowser().get("https://www.uta-net.com/search/?Aselect=3&Keyword=%E3%81%A4%E3%82%93%E3%81%8F&Bselect=3&x=0&y=0")
    val list = for {
      area <- songsDoc >> elements("table > tbody > tr > td.side td1")
      words = area >> (texts("a"))
    } yield words

    println(list)

    Ok(views.html.index())
  }
}
