package crawler.http

import java.io.{ByteArrayOutputStream, InputStream}
import java.net.URL

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.http.HttpEntity
import org.apache.http.impl.client.{HttpClients, HttpClientBuilder}
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}

/**
  * Created by kai on 2015/11/22.
  */
class HttpClient {
}

object HttpClient {

  def get(url: String) = {
    val httpClient =  HttpClients.custom().build()

    val httpGet = new HttpGet(url);
    val response: CloseableHttpResponse = httpClient.execute(httpGet)

    val entity: HttpEntity = response.getEntity
    val responseCode: Int = response.getStatusLine.getStatusCode
    if (entity != null) {
      val in: InputStream = entity.getContent
      val out = new ByteArrayOutputStream()
      var buffer = new Array[Byte](1024)
      while (in.read(buffer, 0, 1024) != -1) {
        out.write(buffer)
      }
      response.close()
      httpClient.close()
      (responseCode, out.toByteArray)
    } else {
      response.close()
      httpClient.close()
      (responseCode, null)
    }
  }

  implicit def str2URL(url: String) = {
    new URL(url)
  }
}
