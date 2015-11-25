package crawler

import crawler.extrator.{FilterEnum, DIVExtrator, LinksExtrator}
import crawler.http.HttpClient
import crawler.utils.Logs
import org.htmlparser.Node

import scala.collection.mutable

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
        println(div.toHtml() + System.lineSeparator() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      }
      for (div <- extrator.extractDiv(FilterEnum.question)) {
        println(div.toHtml() + System.lineSeparator() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      }
      for (div <- extrator.extractDiv(FilterEnum.label)) {
        println(div.toHtml() + System.lineSeparator() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      }
      for (div <- extrator.extractDiv(FilterEnum.author)) {
        println(div.toHtml() + System.lineSeparator() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      }
      for (div <- extrator.extractDiv(FilterEnum.content)) {
        println(div.toHtml() + System.lineSeparator() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++")
      }
    } else {
      warn(answerURL + " Connected Failed " + reponseCode)
      println("http error")
    }
  }
}
