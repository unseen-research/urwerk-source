package urwerk.source.internal

import org.reactivestreams.Subscription as UnderlyingSubscription

import java.util.concurrent.Flow

object SubscriptionConverter:
  def fromUnderlying(underlyingSubscription: UnderlyingSubscription): Flow.Subscription =
    if underlyingSubscription.isInstanceOf[AsUnderlyingSubscription] then
      underlyingSubscription.asInstanceOf[AsUnderlyingSubscription].subscription
    else
      AsSubscription(underlyingSubscription)

  def toUnderlying(subscription: Flow.Subscription): UnderlyingSubscription =
    if subscription.isInstanceOf[AsSubscription] then
      subscription.asInstanceOf[AsSubscription].subscription
    else
      AsUnderlyingSubscription(subscription)

  class AsUnderlyingSubscription(val subscription: Flow.Subscription) extends UnderlyingSubscription:
    def request(n: Long): Unit = subscription.request(n)
    def cancel(): Unit = subscription.cancel()

  class AsSubscription(val subscription: UnderlyingSubscription) extends Flow.Subscription:
    def request(n: Long): Unit = subscription.request(n)
    def cancel(): Unit = subscription.cancel()

