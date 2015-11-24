package crawler.extrator

import scala.collection.mutable

/**
  * Created by kai on 2015/11/22.
  */
object LinksExtrator extends Extrator {
  val linkRule: ExtratRule = ExtratRule( """href="([^"]*)"""", (str => str.contains("answer") && str.startsWith("/")))
  val pageNumRule: ExtratRule = ExtratRule( """href="([^"]*)"""", (str => str.startsWith("?page=")))

  def extratLinks(content: String) = {
    val set = scala.collection.mutable.Set[String]()
    extrat(linkRule, content).foreach(str => {
      (set += str)
    })
    set
  }

  def getMaxPageNum(content: String) = {

    val extrat1: Iterator[String] = extrat(pageNumRule, content)


    val pages: Iterator[Int] = extrat1
      .map(str => str.substring(str.indexOf("=") + 1).toInt)



    pages.foldLeft(0)((max,page) =>{ if(max < page) page else max})
  }
}
