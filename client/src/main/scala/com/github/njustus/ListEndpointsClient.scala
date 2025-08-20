package com.github.njustus

import com.softwaremill.FilesEndpoints
import org.scalajs.dom
import sttp.client4.fetch.FetchBackend
import sttp.client4.{Backend, Response, UriContext, WebSocketBackend}
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.{ExecutionContext, Future}

class ListEndpointsClient(clientAdapter: SttpClientAdapter)(using ExecutionContext) extends FilesEndpoints {

  def list(path: List[String]): Future[List[FileEntry]] = 
    clientAdapter.toClientThrowErrors(listEndpoint)(path)
}
