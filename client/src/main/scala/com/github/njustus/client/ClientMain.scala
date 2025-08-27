package com.github.njustus.client

import com.github.njustus.localshare.shared.FilesEndpoints
import com.raquo.laminar.api.L.*
import frontroute.LinkHandler
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global

object ClientMain extends FilesEndpoints {

  def main(args: Array[String]): Unit = {
    val routes = new FrontRoutes()
    val app2   = frontroute.routes(routes.routes).amend(LinkHandler.bind)
    renderOnDomContentLoaded(dom.document.getElementById("appContainer"), app2)
  }
}
