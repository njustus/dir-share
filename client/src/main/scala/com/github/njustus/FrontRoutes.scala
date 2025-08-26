package com.github.njustus

import com.raquo.laminar.api.L.*
import frontroute.*
import sttp.client4.WebSocketBackend
import sttp.client4.fetch.FetchBackend
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.{ExecutionContext, Future}
import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.nodes.ReactiveHtmlElement

class FrontRoutes()(using ExecutionContext) {
  import com.softwaremill.macwire.*

  private lazy val adapter = {
    val inter                             = SttpClientInterpreter()
    val backend: WebSocketBackend[Future] = FetchBackend()
    new SttpClientAdapter(inter, backend)
  }
//TODO redirect to /listing -- return PWD from backend if no path provided
//TODO restrict backend to PWD

  def routes: ReactiveHtmlElement[HTMLDivElement] =
    div(pathPrefix("listing") {
      firstMatch(
        extractUnmatchedPath { paths =>
          wireRec[ListingComponent].render(paths)
        },
        pathEnd {
          println("render path end")
          div("path end")
        }
      )
    })
}
