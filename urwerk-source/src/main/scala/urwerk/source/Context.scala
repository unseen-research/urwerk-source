package urwerk.source


trait Context:
  def apply(key: Any): Any

  def applyOrElse(key: Any, default: Any => Any): Any

  def get(key: Any): Option[Any]

  def contains(key: Any): Boolean
 
  def isEmpty: Boolean

  def iterator: Iterator[(Any, Any)]

  def removed(key: Any): Context

  def size: Int

  def toSeq: Seq[(Any, Any)]

  def toMap: Map[Any, Any]

  def updated(key: Any, value: Any): Context

trait ContextFactory:
  def apply(elems: (Any, Any)*): Context

  def empty: Context

  def from[K, V](it: IterableOnce[(Any, Any)]): Context