package crawler.extrator

import crawler.utils.Constant

/**
  * Created by kai on 2015/11/22.
  */
object LinksExtrator extends Extrator {
  val linkRule: ExtratRule = ExtratRule( """href="([^"]*)"""", (str => str.length == 34 && str.startsWith("/question")))
  val pageNumRule: ExtratRule = ExtratRule( """href="([^"]*)"""", (str => str.startsWith("?page=")))

  def extratLinks(content: String) = {
    val set = scala.collection.mutable.Set[String]()
    extrat(linkRule, content).foreach(str => {
      (set += Constant.ZHIHU_URL + str)
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
