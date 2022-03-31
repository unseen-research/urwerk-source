package urwerk.source.internal

import reactor.util.context.{ContextView as UnderlyingContextView, Context as UnderlyingContext}
import urwerk.source.Context
import urwerk.source.internal.ContextAdapter.*

import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

object ContextConverter:
  def fromUnderlying(context: UnderlyingContextView): Context =
    ContextAdapter.wrap(context)  

  def toUnderlying(context: Context): UnderlyingContextView =
    if context.isInstanceOf[ContextAdapter] then
      context.asInstanceOf[ContextAdapter].underlyingContext
    else
      UnderlyingContext.of(context.toMap.asJava)

object ContextAdapter:
  def apply(elems: (Any, Any)*): Context =
    from(elems.toMap)

  def empty: Context = wrap(UnderlyingContext.empty())

  def from(map: Map[Any, Any]): Context =
    wrap(
      UnderlyingContext.of(map.asJava))

  def from(it: IterableOnce[(Any, Any)]): Context =
    from(it.iterator.toMap)

  private[internal] def wrap(context: UnderlyingContextView): Context = new ContextAdapter(context)

private [internal] class ContextAdapter private (context: UnderlyingContextView) extends Context:
  def apply(key: Any): Any = context.get(key)

  def applyOrElse(key: Any, default: Any => Any): Any =
    context.getOrDefault(key, default(key))

  def get(key: Any): Option[Any] = context.getOrEmpty(key).toScala

  def contains(key: Any): Boolean = context.hasKey(key)

  def isEmpty: Boolean = context.isEmpty

  def iterator: Iterator[(Any, Any)] = context.stream.iterator.asScala.map({mapEntry => (mapEntry.getKey, mapEntry.getValue)})

  def removed(key: Any): Context =
    wrap(UnderlyingContext.of(context).delete(key))

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
    wrap(UnderlyingContext.of(context).put(key, value))

  def underlyingContext: UnderlyingContextView = context

  def canEqual(a: Any) = a.isInstanceOf[Context]

  override def equals(that: Any): Boolean =
    that match
      case that: Context =>
        this.toMap == that.toMap
      case _ => false

  override def hashCode: Int =
    this.toMap.hashCode