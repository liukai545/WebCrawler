package crawler.http

import java.io.{ByteArrayOutputStream, InputStream}
import java.net.{HttpURLConnection, URLConnection, URL}

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClients

/**
  * Created by kai on 2015/11/22.
  */
class HttpClient {
}

object HttpClient {
  val httpClient = HttpClients.createDefault()

  def get(url: String) = {
    Thread.sleep(500)

    /*    val httpGet = new HttpGet(url);
        val response: CloseableHttpResponse = httpClient.execute(httpGet)

        val entity: HttpEntity = response.getEntity
        val responseCode: Int = response.getStatusLine.getStatusCode
        if (entity != null) {
          val in: InputStream = entity.getContent
          val out = new ByteArrayOutputStream()
          var buffer = new Array[Byte](1024)
          while (in.read(buffer, 0, 1024) != -1) {
            out.write(buffer)
          } */

    val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    val responseCode = connection.getResponseCode
    if (true) {
      val in = connection.getInputStream
      (responseCode, IOUtils.toByteArray(in))
    } else {
      (responseCode, null)
    }
  }
}
