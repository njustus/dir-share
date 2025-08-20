package com.github.njustus

import org.scalajs.dom
import sttp.client4.{Backend, UriContext}
import sttp.tapir
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.Future

class SttpClientAdapter(clientInterpreter: SttpClientInterpreter, backend: Backend[Future]) {

  private val basePath = uri"${dom.window.location.origin}"

  def toClientThrowErrors[I, E, O](e: tapir.PublicEndpoint[I, E, O, Any]): I => Future[O] =
    clientInterpreter
      .toClientThrowErrors(e, Some(basePath), backend)
}
