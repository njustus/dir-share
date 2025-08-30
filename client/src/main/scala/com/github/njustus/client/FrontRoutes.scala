package com.github.njustus.client

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import frontroute.*
import org.scalajs.dom.HTMLDivElement
import sttp.client4.WebSocketBackend
import sttp.client4.fetch.FetchBackend
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.{ExecutionContext, Future}

class FrontRoutes()(using ExecutionContext) {
  import com.softwaremill.macwire.*

  private lazy val adapter = {
    val inter                             = SttpClientInterpreter()
    val backend: WebSocketBackend[Future] = FetchBackend()
    new SttpClientAdapter(inter, backend)
  }
//TODO restrict backend to PWD

  def routes: ReactiveHtmlElement[HTMLDivElement] = {
    div(pathPrefix("listing") {
      firstMatch(
        extractUnmatchedPath { paths =>
          wireRec[ListingComponent].render(paths)
        },
        pathEnd {
          wireRec[ListingComponent].render()
        }
      )
    }, noneMatched {
      navigate("/listing")
    })
  }
}
