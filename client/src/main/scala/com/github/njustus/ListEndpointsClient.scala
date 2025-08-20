package com.github.njustus

import com.softwaremill.FilesEndpoints

import scala.concurrent.{ExecutionContext, Future}

class ListEndpointsClient(clientAdapter: SttpClientAdapter)(using ExecutionContext) extends FilesEndpoints {

  def list(path: List[String]): Future[List[FileEntry]] =
    clientAdapter.toClientThrowErrors(listEndpoint)(path)
}
