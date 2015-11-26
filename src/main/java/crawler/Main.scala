package crawler

import java.util.concurrent.{ExecutorService, Executors, Executor}

import crawler.domain.Answer
import crawler.extrator.LinksExtrator
import crawler.http.HttpClient

import scala.actors.Actor
import scala.collection.mutable
import scala.actors.Actor._


/**
  * Created by kai on 2015/11/22.
  */
object Main extends App {
  val topicURL = "http://www.zhihu.com/collection/43011531"

  private val links: mutable.Set[String] = new Crawler().getAnswerLinks(topicURL)

  private val save: Actor = actor {
    var end = false
    while (!end) {
      receive {
        case answer: Answer => println(answer.title)
        case boo: Boolean => end = true
        case _ => println("end")
      }
    }
  }
  val executor: ExecutorService = Executors.newFixedThreadPool(10)
  links.foreach(link => {
    println(link)

    executor.execute(new Runnable {
      override def run(): Unit = {
        val answer: Option[Answer] = new Crawler().getAnswer(link)
        if (answer.isDefined) {
          val answer1: Answer = answer.get
          save ! answer1
        }
      }
    })
  }
  )

  while(Thread.activeCount() != 1){
    println(Thread.activeCount())
    Thread.sleep(100)
  }

  save ! true
  println("sended")

}


