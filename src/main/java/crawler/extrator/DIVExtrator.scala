package crawler.extrator

import org.htmlparser.filters.{HasAttributeFilter, TagNameFilter}
import org.htmlparser.util.NodeList
import org.htmlparser.{Node, NodeFilter, Parser}
import java.nio.charset.Charset

/**
  * Created by kai on 2015/11/24.
  */
class DIVExtrator private(parser: Parser) {

  def extractDiv(filter: NodeFilter) = {
    val matched: NodeList = parser.extractAllNodesThatMatch(filter)

    if (matched.size() == 1) {
      val node: Node = matched.elementAt(0)
    }
  }
}

object DIVExtrator {
  def apply(content: Array[Byte], charset: String = "UTF-8") = {
    new DIVExtrator(Parser.createParser(new String(content, Charset.forName(charset)), charset))
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
