package urwerk.source

import urwerk.source.internal.ContextAdapter

object Context:
  def apply(elems: (Any, Any)*): Context =
    ContextAdapter(elems*)

  def empty: Context = apply()

  def from(map: Map[Any, Any]): Context =
    ContextAdapter.from(map)

  def from(it: IterableOnce[(Any, Any)]): Context =
    ContextAdapter.from(it)

trait Context:
  def apply(key: Any): Any

  def applyOrElse(key: Any, default: Any => Any): Any

  def get(key: Any): Option[Any]

  def contains(key: Any): Boolean
 
  def isEmpty: Boolean

  def iterator: Iterator[(Any, Any)]

  def removed(key: Any): Context

  def size: Int

  def toMap: Map[Any, Any]

  def toSeq: Seq[(Any, Any)]

  def toSet: Set[(Any, Any)]

  def updated(key: Any, value: Any): Context
