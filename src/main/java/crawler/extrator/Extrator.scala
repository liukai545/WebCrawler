package crawler.extrator

import scala.util.matching.Regex

/**
  * Created by kai on 2015/11/22.
  */
trait Extrator {
  def extrat(extratRule: ExtratRule, content: String) = {
    val reg: Regex = extratRule.pattern.r
    for (reg(item) <- reg.findAllIn(content) if extratRule.filter(item)) yield {
      item
    }
  }
}
