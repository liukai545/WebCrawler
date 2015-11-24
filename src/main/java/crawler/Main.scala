package crawler

import crawler.extrator.LinksExtrator
import crawler.http.HttpClient

import scala.collection.mutable


/**
  * Created by kai on 2015/11/22.
  */
object Main extends App {
/*  val topicURL = "http://www.zhihu.com/collection/20382249"
  private val num: Int = LinksExtrator.getMaxPageNum(new String(HttpClient.get(topicURL)._2))

  println(num + "pageNo")

  private val links: mutable.Set[String] = new Crawler().getAnswerLinks(topicURL, num)
  links.foreach(println)*/

  new Crawler().getAnswler("http://www.zhihu.com/question/21391305/answer/44542857")

}
