package crawler

import crawler.domain.Answer
import crawler.extrator.{ExtratRule, FilterEnum, DIVExtrator, LinksExtrator}
import crawler.http.HttpClient
import crawler.utils.Logs

import scala.Predef
import scala.collection.mutable
import scala.xml.{NodeSeq, Elem, XML}

/**
  * Created by kai on 2015/11/22.
  */
class Crawler extends Logs {
  def getAnswerLinks(topicURL: String, pageNum: Int = 1): scala.collection.mutable.Set[String] = {
    println("正在提取第 " + pageNum + "页的链接")

    val (reponseCode, content) = HttpClient.get(topicURL + "?page=" + pageNum)

    if (pageNum == 1) {
      //第一页时计算总页数
      val maxPageNum = LinksExtrator.getMaxPageNum(new String(content))
      println("总页数 " + maxPageNum)
      if (maxPageNum == 1) {
        //只有一页，返回
        LinksExtrator.extratLinks(new String(content))
      } else {
        //开始递归
        LinksExtrator.extratLinks(new String(content)) ++ getAnswerLinks(topicURL, maxPageNum)
      }
    } else if (pageNum == 2) {
      //从最后一页递归到第二页，返回
      if (reponseCode == 200) {
        LinksExtrator.extratLinks(new String(content))
      } else {
        warn("PageNum : " + pageNum + " Download Error")
        mutable.Set()
      }
    } else {
      LinksExtrator.extratLinks(new String(content)) ++ getAnswerLinks(topicURL, pageNum - 1)
    }
  }

  def getAnswer(answerURL: String): Option[Answer] = {
    val (reponseCode, content) = HttpClient.get(answerURL)
    if (reponseCode == 200) {
      val extrator: DIVExtrator = DIVExtrator(answerURL)
      val answer = new Answer(answerURL)

      for (div <- extrator.extractDiv(FilterEnum.title)) {
        val str = div.toHtml()
        val root: Elem = XML.loadString(str)
        val seq: NodeSeq = root \ "h2" \ "a"
        answer.title = seq.text.trim()
      }
      for (div <- extrator.extractDiv(FilterEnum.question)) {
        val str = div.toHtml().replaceAll("<br>", System.lineSeparator()).replaceAll("<.*?>", " ")
        answer.question = str.trim()
      }
      for (div <- extrator.extractDiv(FilterEnum.label)) {
        val seq: NodeSeq = XML.loadString(div.toHtml()) \ "a"
        answer.labels = seq.map(_.text.trim).toArray
      }
      for (div <- extrator.extractDiv(FilterEnum.author)) {
        answer.auther = div.toHtml().replaceAll("<.*?>", "").replaceAll("\n", "")
      }
      for (div <- extrator.extractDiv(FilterEnum.content)) {
        val str = div.toHtml().replaceAll("<br>", System.lineSeparator())

        val imgSrcRule = ExtratRule( """src="([^"]*)"""", (str => str.startsWith("http")))
        answer.imgLinks = LinksExtrator.extratLinks(str, imgSrcRule).toArray
        answer.content = str.replaceAll("<.*?>", " ")
      }
      Some(answer)
    } else {
      warn(answerURL + " Connected Failed " + reponseCode)
      None
    }
  }
}
