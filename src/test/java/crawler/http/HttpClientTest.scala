package crawler.http

import org.junit.Test


/**
  * Created by kai on 2015/11/22.
  */
class HttpClientTest {
  @Test def testGet() = {
    val (responseCode, content) = HttpClient.get("http://www.zhihu.com/collection/72475918")

    println(responseCode)
    println(new String(content))

  }
}
