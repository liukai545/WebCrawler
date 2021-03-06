package crawler.repository

import crawler.repository.RepositoryCategory.RepositoryCategory

/**
  * Created by liukai on 2015/11/27.
  */
object RespositoryFactory {
  def createRespository[T](category: RepositoryCategory, destination: String) = {
    category match {
      case RepositoryCategory.JSON => new JsonRepository[T](destination)
      case RepositoryCategory.CSV => new CSVRepository[T](destination)
      case RepositoryCategory.BINARY =>new BinaryRepository[T](destination)
    }
  }
}

object RepositoryCategory extends Enumeration {
  type RepositoryCategory = Value
  val JSON,CSV ,BINARY= Value
}
