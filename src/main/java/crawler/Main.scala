package crawler

import java.io.PrintWriter

import crawler.extrator.LinksExtrator
import crawler.http.HttpClient

import scala.collection.mutable
import scala.tools.nsc.io.File


/**
  * Created by kai on 2015/11/22.
  */
object Main extends App {
  val topicURL = "http://www.zhihu.com/collection/44415717"

  private val links: mutable.Set[String] = new Crawler().getAnswerLinks(topicURL)
  println("count: " + links.count(str => true))
  links foreach (println)

}
