package crawler

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.{ActorRef, ActorSystem, Props}
import crawler.domain.Answer
import crawler.repository.{RepositoryCategory, RespositoryFactory}
import crawler.utils.Constant

import scala.collection.mutable


/**
  * Created by kai on 2015/11/22.
  * 使用Akka框架传递消息
  */
object Main extends App {
  val topicURL = "https://www.zhihu.com/collection/20205640"

  private val links: mutable.Set[String] = new Crawler().getAnswerLinks(topicURL)

  val system = ActorSystem("actor-answer")
  private val of: ActorRef = system.actorOf(Props(classOf[Save], links.size))
  val save = of
  println(save.toString())

  class Save(total: Int) extends akka.actor.Actor {
    val respository = RespositoryFactory.createRespository[Answer](RepositoryCategory.CSV, "d://zhi/answer")

    var count = 0

    override def receive = {
      case answer: Answer => {
        println(answer.title);
        respository.save(answer);
        count += 1;
        println(count)
        if (count == total) {
          respository.close()
          system.shutdown()
          executor.shutdown()
          println("shutdown")
        }
      }
    }
  }

  val executor: ExecutorService = Executors.newFixedThreadPool(10)
  links.foreach(link => {
    println(link)

    executor.execute(new Runnable {
      override def run(): Unit = {
        val answer: Option[Answer] = new Crawler().getAnswer(Constant.ZHIHU_URL + link)
        if (answer.isDefined) {
          val answer1: Answer = answer.get
          save ! answer1
        }
      }
    })
  }
  )
}

