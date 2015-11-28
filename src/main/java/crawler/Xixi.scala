package crawler

import java.io.FileInputStream

import crawler.dao.{RepositoryCategory, RespositoryFactory}
import crawler.extrator.XixiExtrator
import crawler.http.HttpClient
import org.apache.commons.io.IOUtils

/**
  * Created by kai on 2015/11/28.
  */
object Xixi extends App {
  val url = "http://cl.youcl.biz/htm_data/7/1511/1704683.html"

  //private val (responseCode, content) = HttpClient.get(url)

  val in = new FileInputStream(new java.io.File("d://zhi/web.txt"))

  private val str: String = IOUtils.toString(in)

  new XixiExtrator().extractImgLinks(str).foreach(str => {
    println(str)
    val (responseCode, content) = HttpClient.get(str)

    val respository = RespositoryFactory.createRespository[Array[Byte]](RepositoryCategory.BINARY,"d://zhi/pic/" +str.substring(str.lastIndexOf("/")))
    respository.save(content)
    respository.close()
  })

  new XixiExtrator().extratMagnet(str).foreach(println)
}
