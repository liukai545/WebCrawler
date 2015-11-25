package crawler.extrator

import java.net.{URLConnection, HttpURLConnection, URL}

import org.htmlparser.filters.{TagNameFilter, HasAttributeFilter}
import org.htmlparser.util.NodeList
import org.htmlparser.{Node, NodeFilter, Parser}

/**
  * Created by kai on 2015/11/24.
  */
class DIVExtrator private(parser: Parser) {
  val nodeList: NodeList = parser.extractAllNodesThatMatch(new TagNameFilter("div"))

  def extractDiv(filter: NodeFilter): Option[Node] = {
    var node: Option[Node] = None
    val matched: NodeList = nodeList.extractAllNodesThatMatch(filter, true)
    if (matched.size() >= 1) {
      println(matched.elementAt(0).toHtml())
      node = Some(matched.elementAt(0))
    } else {
      println(matched.size())
      println("mei pi pei dao")
    }
    node
  }
}

object DIVExtrator {
  def apply(url: String) = {
    val connection: URLConnection = new URL(url).openConnection()
    new DIVExtrator(new Parser(connection.asInstanceOf[HttpURLConnection]))
  }
}

object FilterEnum {
  def title = new HasAttributeFilter("id", "zh-question-title")

  def question = new HasAttributeFilter("class", "zm-editable-content")

  def label = new HasAttributeFilter("class", "zm-tag-editor-labels zg-clear")

  // 注意匿名用户
  def author = new HasAttributeFilter("class", "zm-item-answer-author-info")

  def content = new HasAttributeFilter("class", "zm-editable-content clearfix")
}
