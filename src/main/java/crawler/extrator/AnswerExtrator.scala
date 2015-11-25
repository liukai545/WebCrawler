package crawler.extrator

import crawler.http.HttpClient

import scala.Predef
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator

/**
  * Created by liukai on 2015/11/23.
  */
class AnswerExtrator extends Extrator{
  private val rule: ExtratRule = ExtratRule( "<div>(.*?)</div>")

  private def getDiv(content: String) {
    extrat(rule,content).foreach(println)
  }
}

object AnswerExtrator{
  def main(args: Array[String]) {
    val _2: Array[Byte] = HttpClient.get("http://www.zhihu.com/question/30901993/answer/71350474")._2

    //new AnswerExtrator().getDiv(new String(_2))


     val str: String = "afegwae<div>liu</div>asdfawe"
     val reg: Regex = "<div>(.*?)</div>".r

    println(new Predef.String(_2).replace("\n", ""))
    println("xxxxxxxx")

     val in: MatchIterator = reg.findAllIn(new String(_2).replace("\n|\r\n","").replaceAll(" ",""))
    in.foreach(println)
  }


}
