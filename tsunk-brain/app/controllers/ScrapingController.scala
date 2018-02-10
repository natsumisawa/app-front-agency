package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.Future
import play.api.Play.current
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

@Singleton
class  ScrapingController @Inject() extends Controller {

  case class AnalysisData(request_id: String, word_list: Seq[Seq[Seq[String]]])
  implicit val analysisDataReads  = Json.reads[AnalysisData]

  def catchWords = Action { implicit request =>
    // TODO(sawa): ↓ださめなので検討（(1 to 10).toList.map(_.toString)だとdocument取得できない）
    val pageNum = List("1", "2")
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

    analysisByGooApi(result)
    Ok(views.html.index())
  }

  def analysisByGooApi(wordsList: List[String]): Unit = {
    wordsList.map(word => {
      val url = "https://labs.goo.ne.jp/api/morph"
      val data = Json.obj(
        "app_id" -> "f1864845432875486a4061f40566d4254283779e639c146e38d8c8057df9485e",
        "sentence" -> word
      )
      WS.url(url).withHeaders("Accept" -> "application/json").post(data).map{
        response =>
          println(response.json.validate[AnalysisData])
      }
    })
  }
}
