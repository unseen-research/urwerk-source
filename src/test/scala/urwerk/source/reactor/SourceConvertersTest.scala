package urwerk.source.reactor

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import reactor.core.publisher.Flux
import urwerk.source.Source
import FluxConverters.*
import scala.jdk.CollectionConverters.*
import urwerk.test.TestBase

class SourceConvertersTest extends TestBase:
  "source as flux" in {
    val flux: Flux[Int] = Source(1, 2, 3).toFlux
    
    flux.collectList().block.asScala should be(Seq(1, 2, 3))
  }
