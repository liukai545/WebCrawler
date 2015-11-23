package crawler

import crawler.extrator.LinksExtrator
import crawler.http.HttpClient
import crawler.utils.Logs
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by kai on 2015/11/22.
  */
class Crawler extends Logs {

  def getAnswerLinks(topicURL: String, pageNum: Int = 1): scala.collection.mutable.Set[String] = {
    println("当前 pageNo" + pageNum)

    val (reponseCode, content) = HttpClient.get(topicURL + "?page=" + pageNum)

    if (pageNum == 1) {
      //第一页时计算总页数
      val maxPageNum = LinksExtrator.getMaxPageNum(new String(content))
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
}
