package crawler.extrator

/**
  * Created by kai on 2015/11/28.
  */
class XixiExtrator extends Extrator {
  private val imgRule: ExtratRule = ExtratRule( """<img src='([^"]*)'""", (str => true))
  private val magnetRule: ExtratRule = ExtratRule( """<blockquote>([^"]*)</blockquote>""", (str => true))

  def extratMagnet(content: String) = {
    val set = scala.collection.mutable.Set[String]()
    extrat(magnetRule, content).foreach(str => {
      (set += str)
    })
    set
  }

  def extractImgLinks(content: String) = {
    val set = scala.collection.mutable.Set[String]()
    extrat(imgRule, content).foreach(str => {
      (set += str)
    })
    set
  }
}
