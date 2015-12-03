package crawler.repository

import java.io.PrintWriter

import com.fasterxml.jackson.databind.ObjectMapper



/**
  * Created by liukai on 2015/11/27.
  */
class JsonRepository[T](path: String) extends crawler.repository.Repository[T] {

  private val writer: PrintWriter = new PrintWriter(path)


  override def load(): Array[T] = ???

  override def save(obj: T): Unit = {
   // writer.write(Json.toJson(obj).toString() + System.lineSeparator())
  }

  override def close(): Unit = {
    writer.flush();
    writer.close()
  }
}
