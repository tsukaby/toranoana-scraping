package com.tsukaby.toranoana.scraping

import java.io.{PrintWriter, StringReader}

import com.github.nscala_time.time.Imports._
import com.ibm.icu.text.Transliterator
import com.ning.http.client.cookie.Cookie
import com.tsukaby.toranoana.scraping.AgeType.{AdultOnly, AllAge}
import com.tsukaby.toranoana.scraping.Gender.{Female, Male}
import dispatch.Defaults._
import dispatch._
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.xml.parsing.NoBindingFactoryAdapter
import scala.xml.{Node, Text}

object Main {

  def main(args: Array[String]) {

    if (args.length != 2) {
      println("引数の数が間違っています。[開始日] [終了日]を指定してください。")
      return
    }

    val startDateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(args(0))
    val endDateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(args(1))

    if (startDateTime > endDateTime) {
      println("開始日と終了日が逆転しています。")
      return
    }

    println(s"集計を開始します。開始日=${startDateTime.toString("yyyy/MM/dd")} 終了日=${endDateTime.toString("yyyy/MM/dd")}")

    val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth")

    val files = Map(
      AgeType.AllAge -> new PrintWriter("toranoana-result-male-normal.csv"),
      AgeType.AdultOnly -> new PrintWriter("toranoana-result-male-18only.csv"))

    for (dateTime <- getDateList(startDateTime, endDateTime); ageType <- Seq(AgeType.AllAge, AgeType.AdultOnly)) {

      println(s"${dateTime.toString("yyyy/MM/dd")} ${ageType.getClass.getSimpleName} 集計中")

      Thread.sleep(5000)

      var req = url(getToranoanaUrl(dateTime, Gender.Male, ageType))
      req = req <:< Map("User-Agent" -> "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)")
      req = req.addCookie(new Cookie("afg", "0", "0", ".toranoana.jp", "/", 0, 0, false, false))

      val res: Future[Array[Byte]] = Http(req OK as.Bytes)

      val content = Await.result[Array[Byte]](res, Duration.Inf)

      val body = new String(content, "SHIFT_JIS")

      val node = toNode(body)

      val entryBody = node \\ "td" filter (_ \ "@class" contains Text("RankTblStructure_Td"))

      entryBody foreach { x =>

        val rankTmp = x \\ "span" filter (_ \ "@class" contains Text("RankNum")) text
        val rank = transliterator.transliterate(rankTmp)

        val category = x.text.split("\n") find (_.contains("ジャンル：")) match {
          case Some(x) =>

            val categories = x.replace("\t", "").replace("ジャンル：", "").split("-", 2)
            if (categories.length == 1) {
              categories(0)
            } else if (categories.length == 2) {
              categories(1)
            } else {
              ""
            }
          case None =>
        }
        val circle = x.text.split("\n") find (_.contains("サークル：")) match {
          case Some(x) =>
            x.replace("\t", "").replace("サークル：", "")
          case None =>
            ""
        }

        val file = files.get(ageType).get
        file.println(s""""${dateTime.toString("yyyy/MM/dd")}","$rank","$category","$circle"""")
      }
    }

    files.foreach { x =>
      x._2.close()
    }

    println("終了しました。")

    System.exit(0)
  }

  def toNode(str: String): Node = {
    val hp = new HtmlParser
    hp.setNamePolicy(XmlViolationPolicy.ALLOW)
    hp.setCommentPolicy(XmlViolationPolicy.ALLOW)

    val saxer = new NoBindingFactoryAdapter
    hp.setContentHandler(saxer)
    hp.parse(new InputSource(new StringReader(str)))

    saxer.rootElem
  }

  def getDateList(startDateTime: DateTime, endDateTime: DateTime): List[DateTime] = {

    val dateList = ListBuffer[DateTime]()

    var tmp = startDateTime

    while (true) {
      if (tmp <= endDateTime) {
        dateList += tmp

        tmp = tmp + 1.days
      } else {
        return dateList.toList
      }
    }

    Nil
  }

  def getToranoanaUrl(dateTime: DateTime, gender: Gender, ageType: AgeType): String = {

    val genderString = gender match {
      case Male => "male"
      case Female => "female"
    }

    val ageTypeString = ageType match {
      case AllAge => "0"
      case AdultOnly => "1"
    }

    s"http://www.toranoana.jp/mailorder/cot/ranking/${dateTime.getYear}/${dateTime.toString("yyyyMMdd")}-$genderString$ageTypeString.html"
  }

}


