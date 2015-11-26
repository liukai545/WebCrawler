package crawler.domain

class Answer(val url:String){
  var title:String = null
  var question:String = null
  var labels:Array[String] = null
  var auther:String = null
  var content:String = null
  var imgLinks:Array[String] = null
}