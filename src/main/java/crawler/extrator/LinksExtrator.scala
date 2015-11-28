package crawler.extrator

import crawler.utils.Constant

import scala.Predef
import scala.collection.mutable

/**
  * http://www.zhihu.com/collection/20054781
  * Created by kai on 2015/11/22.
  */
object LinksExtrator extends Extrator {
  private val linkRule: ExtratRule = ExtratRule( """href="([^"]*)"""", (str => str.contains("answer") && str.startsWith("/")))
  private val pageNumRule: ExtratRule = ExtratRule( """href="([^"]*)"""", (str => str.startsWith("?page=")))

  def extratLinks(content: String, linkRule: ExtratRule = linkRule) = {
    val set = scala.collection.mutable.Set[String]()
    extrat(linkRule, content).foreach(str => {
      (set += str)
    })
    set
  }

  def getMaxPageNum(content: String) = {
    val pages: Iterator[Int] = extrat(pageNumRule, content).map(str => str.substring(str.indexOf("=") + 1).toInt)
    pages.foldLeft(1)((max, page) => {
      if (max < page) page else max
    })
  }
}
