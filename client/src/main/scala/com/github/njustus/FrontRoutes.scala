package com.github.njustus

import com.raquo.laminar.api.L.*
import frontroute.*
import sttp.client4.WebSocketBackend
import sttp.client4.fetch.FetchBackend
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.{ExecutionContext, Future}

class FrontRoutes()(using ExecutionContext) {
  import com.softwaremill.macwire.*

  private lazy val adapter = {
    val inter = SttpClientInterpreter()
    val backend: WebSocketBackend[Future] = FetchBackend()
    new SttpClientAdapter(inter, backend)
  }

  def routes = {
    div(
      pathPrefix("listing") {
        firstMatch(
          extractUnmatchedPath { paths =>
            wireRec[ListingComponent].render(paths)
          },
          pathEnd {
            println(s"render path end")
            div("path end")
          })
      }
    )
  }
}
