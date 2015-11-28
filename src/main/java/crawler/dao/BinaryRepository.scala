package crawler.dao

import java.io.{FileOutputStream, ByteArrayOutputStream, PrintWriter}

/**
  * Created by kai on 2015/11/28.
  */
class BinaryRepository[T](destination: String) extends Repository[T] {
  val writer = new FileOutputStream(new java.io.File(destination))

  override def load(): Array[T] = ???

  override def save(obj: T): Unit = {
    if (obj.isInstanceOf[Array[Byte]]) {
      val bytes: Array[Byte] = obj.asInstanceOf[Array[Byte]]
      writer.write(bytes)
      writer.flush()
    }
  }

  override def close(): Unit = {
    writer.flush()
    writer.close()
  }
}
