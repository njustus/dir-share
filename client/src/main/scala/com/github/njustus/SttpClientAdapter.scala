package com.github.njustus

import com.softwaremill.FilesEndpoints
import org.scalajs.dom
import sttp.client4.fetch.FetchBackend
import sttp.client4.{Backend, Request, Response, UriContext, WebSocketBackend}
import sttp.tapir
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.{ExecutionContext, Future}

class SttpClientAdapter(clientInterpreter: SttpClientInterpreter,
                        backend: Backend[Future]) {

  private val basePath = uri"${dom.window.location.origin}"

  def toClientThrowErrors[I, E, O](e: tapir.PublicEndpoint[I, E, O, Any]) =
    clientInterpreter
      .toClientThrowErrors(e, Some(basePath), backend)
}
