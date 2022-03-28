package urwerk.source.internal

import reactor.util.context.ContextView
import reactor.util.context.{Context => ReactorContext}

import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

import urwerk.source.Context

object FluxContext:
  def apply(elems: (Any, Any)*): Context =
    from(elems.toMap)

  def empty: Context = wrap(ReactorContext.empty())

  def from(map: Map[Any, Any]): Context =
    wrap(
      ReactorContext.of(map.asJava))

  def from(it: IterableOnce[(Any, Any)]): Context =
    from(it.iterator.toMap)

  def wrap(context: ContextView): Context = new Context:
    def apply(key: Any): Any = context.get(key)

    def applyOrElse(key: Any, default: Any => Any): Any =
      context.getOrDefault(key, default(key))

    def get(key: Any): Option[Any] = context.getOrEmpty(key).toScala

    def contains(key: Any): Boolean = context.hasKey(key)

    def isEmpty: Boolean = context.isEmpty

    def iterator: Iterator[(Any, Any)] = context.stream.iterator.asScala.map({mapEntry => (mapEntry.getKey, mapEntry.getValue)})

    def removed(key: Any): Context =
      wrap(ReactorContext.of(context).delete(key))

    def size: Int = context.size()

    def toMap: Map[Any, Any] =
      if isEmpty then
        Map()
      else
        iterator.toMap

    def toSeq: Seq[(Any, Any)] =
      if isEmpty then
        Seq()
      else iterator.toSeq

    def toSet: Set[(Any, Any)] =
      if isEmpty then
        Set()
      else iterator.toSet
    
    def updated(key: Any, value: Any): Context =
      wrap(ReactorContext.of(context).put(key, value))
