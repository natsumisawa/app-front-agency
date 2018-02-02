import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

class ScrapingRepository {
  // val doc: Document  = JsoupBrowser().get("http://weather.yahoo.co.jp/weather/")
  val browser = JsoupBrowser()
  val doc = browser.parseFile("core/src/test/resources/example.html")
  val doc2 = browser.get("http://example.com")
  for {
    area <- doc >> elements("#map > ul > li > a > dl")
    (name, weather) = area >> (text("dt"), texts("dd"))
  } println(s"$name - ${weather.mkString(" ")}")
}
