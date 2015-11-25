package crawler

import crawler.extrator.{FilterEnum, DIVExtrator, LinksExtrator}
import crawler.http.HttpClient
import crawler.utils.Logs
import org.htmlparser.Node

import scala.collection.mutable
import scala.xml
import scala.xml.{NodeSeq, Elem, XML}

/**
  * Created by kai on 2015/11/22.
  */
class Crawler extends Logs {
  def getAnswerLinks(topicURL: String, pageNo: Int): scala.collection.mutable.Set[String] = {
    val (reponseCode, content) = HttpClient.get(topicURL + "?page=" + pageNo)

    println(pageNo)

    if (pageNo == 1) {
      if (reponseCode == 200) {
        LinksExtrator.extratLinks(new String(content))
      } else {
        mutable.Set()
      }
    } else {
      LinksExtrator.extratLinks(new String(content)) ++ getAnswerLinks(topicURL, pageNo - 1)
    }
  }

  def getAnswler(answerURL: String) = {
    val (reponseCode, content) = HttpClient.get(answerURL)
    if (reponseCode == 200) {
      val extrator: DIVExtrator = DIVExtrator(answerURL)

      for (div <- extrator.extractDiv(FilterEnum.title)) {
        val str = div.toHtml()
        val root: Elem = XML.loadString(str)
        val seq: NodeSeq = root \ "h2" \ "a"
        println("title:" + seq.text)
      }
      for (div <- extrator.extractDiv(FilterEnum.question)) {
        val str = div.toHtml().replaceAll("<br>", System.lineSeparator()).replaceAll("<.*?>"," ")
        println("question:" + str)
      }
      for (div <- extrator.extractDiv(FilterEnum.label)) {
        val seq: NodeSeq = XML.loadString(div.toHtml()) \ "a"
        println("label:")
        for (elem <- seq) {
          val elem1: xml.Node = elem
          println(elem1.text)
        }
      }
      for (div <- extrator.extractDiv(FilterEnum.author)) {
        println("author:" +div.toHtml().replaceAll("<.*?>", ""))
        println(div.toHtml())
      }
      for (div <- extrator.extractDiv(FilterEnum.content)) {
        val str = div.toHtml().replaceAll("<br>", System.lineSeparator()).replaceAll("<.*?>"," ")
        println("content:" +str)
      }
    } else {
      warn(answerURL + " Connected Failed " + reponseCode)
      println("http error")
    }
  }
}
