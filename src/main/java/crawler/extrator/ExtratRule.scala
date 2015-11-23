package crawler.extrator

/**
  * Created by kai on 2015/11/22.
  */
case class ExtratRule(pattern: String, filter: (String => Boolean) = (str: String) => true) {}

