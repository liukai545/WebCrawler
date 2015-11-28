package crawler.dao

/**
  * Created by liukai on 2015/11/27.
  */
trait Repository[T] {
  def load(): Array[T]

  def save(obj: T):Unit

  def close()
}
