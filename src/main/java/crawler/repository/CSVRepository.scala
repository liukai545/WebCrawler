package crawler.repository

import java.io.PrintWriter

import crawler.domain.Answer

/**
  * Created by liukai on 2015/11/28.
  */
class CSVRepository[T](path: String) extends Repository[T] {
  var writer = new PrintWriter(path)

  override def load(): Array[T] = ???

  override def save(obj: T): Unit = {
    if (obj.isInstanceOf[Answer]) {
      val answer: Answer = obj.asInstanceOf[Answer]
      val sb = new StringBuilder()
      sb.append(answer.url).append("*#*#")
      sb.append(answer.auther).append("*#*#")
      sb.append(answer.title).append("*#*#")
      val labels: Array[String] = answer.labels
      if (labels != null)
        sb.append(labels.mkString("(", ",", ")")).append("*#*#")
      sb.append(answer.question).append("*#*#")
      sb.append(answer.content).append("*#*#")
      val links: Array[String] = answer.imgLinks
      if (links != null)
        sb.append(links.mkString("(", ",", ")")).append("@@@@")
      writer.write(sb.toString())
      writer.flush()
    }
  }

  override def close(): Unit = {
    writer.flush()
    writer.close()
  }
}
