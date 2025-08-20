package com.github.njustus

import com.raquo.laminar.api.L.*
import frontroute.LinkHandler
import org.scalajs.dom
import sttp.client4.*
import sttp.client4.fetch.FetchBackend
import sttp.model.Uri
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.ExecutionContext.Implicits.global

object ClientMain extends com.softwaremill.FilesEndpoints {

  def main(args: Array[String]): Unit = {
    val routes = new FrontRoutes()
    val app2 = frontroute.routes(routes.routes).amend(LinkHandler.bind)
    renderOnDomContentLoaded(dom.document.getElementById("appContainer"), app2)
  }
}
