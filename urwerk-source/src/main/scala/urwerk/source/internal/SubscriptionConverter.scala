package urwerk.source.internal

import org.reactivestreams.Subscription as UnderlyingSubscription

import java.util.concurrent.Flow

object SubscriptionConverter:
  def fromUnderlying(underlyingSubscription: UnderlyingSubscription): Flow.Subscription =
    if underlyingSubscription.isInstanceOf[UnderlyingSubscriptionImpl] then
      underlyingSubscription.asInstanceOf[UnderlyingSubscriptionImpl].subscription
    else
      FlowSubscriptionImpl(underlyingSubscription)

  def toUnderlying(subscription: Flow.Subscription): UnderlyingSubscription =
    if subscription.isInstanceOf[FlowSubscriptionImpl] then
      subscription.asInstanceOf[FlowSubscriptionImpl].subscription
    else
      UnderlyingSubscriptionImpl(subscription)

  class UnderlyingSubscriptionImpl(val subscription: Flow.Subscription) extends UnderlyingSubscription:
    def request(n: Long): Unit = subscription.request(n)
    def cancel(): Unit = subscription.cancel()

  class FlowSubscriptionImpl(val subscription: UnderlyingSubscription) extends Flow.Subscription:
    def request(n: Long): Unit = subscription.request(n)
    def cancel(): Unit = subscription.cancel()

