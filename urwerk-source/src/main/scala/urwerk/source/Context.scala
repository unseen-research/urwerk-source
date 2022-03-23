package urwerk.source


trait Context:
  def apply(key: Any): Any

  def applyOrElse(key: Any, default: Any => Any): Any

  def get(key: Any): Option[Any]

  def contains(key: Any): Boolean
 
  def isEmpty: Boolean

  def iterator: Iterator[(Any, Any)]

  def size: Int

  def toSeq: Seq[(Any, Any)]

  def toMap: Map[Any, Any]

  //default <T> T 	get(Class<T> key)
  //Resolve a value given a type key within the Context.
