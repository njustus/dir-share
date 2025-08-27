package com.github.njustus.client

import com.github.njustus.localshare.shared.FilesEndpoints

import scala.concurrent.{ExecutionContext, Future}

class ListEndpointsClient(clientAdapter: SttpClientAdapter)(using ExecutionContext) extends FilesEndpoints {
  val upload: ((List[String], MultipartUpload)) => Future[String] = clientAdapter.toClientThrowErrors(uploadEndpoint)
  val list: List[String] => Future[List[FileEntry]] =
    clientAdapter.toClientThrowErrors(listEndpoint)
}
