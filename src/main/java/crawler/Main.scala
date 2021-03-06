package crawler

import java.util.concurrent.{ExecutorService, Executors}

import crawler.domain.Answer
import crawler.repository.{RepositoryCategory, RespositoryFactory}
import crawler.utils.Constant

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable


/**
  * Created by kai on 2015/11/22.
  */
object Main extends App {
  val topicURL = "http://www.zhihu.com/collection/20205640"

  private val links: mutable.Set[String] = new Crawler().getAnswerLinks(topicURL)

  private val save: Actor = actor {

    val respository = RespositoryFactory.createRespository[Answer](RepositoryCategory.CSV, "d://zhi/answer")
    var end = false
    while (!end) {
      receive {
        case (answer: Answer, finished: Finished) => println(answer.title); respository.save(answer); finished ! "msg"
        case boo: Boolean => end = true; respository.close(); executor.shutdown();
        case _ => println("end")
      }
    }

  }
  val executor: ExecutorService = Executors.newFixedThreadPool(10)
  private val finished: Finished = new Finished(links.size, save)
  finished.start()
  links.foreach(link => {
    println(link)

    executor.execute(new Runnable {
      override def run(): Unit = {
        val answer: Option[Answer] = new Crawler().getAnswer(Constant.ZHIHU_URL + link)
        if (answer.isDefined) {
          val answer1: Answer = answer.get
          save !(answer1, finished)
        }
      }
    })
  }
  )
}

class Finished(total: Int, save: Actor) extends Actor {
  var count = 0;

  override def act(): Unit = {
    while (count != total) {
      receive {
        case msg: String => count += 1; println(count)
        case _ => println("xxx")
      }
    }
    save ! true
    println("sended")
  }
}
