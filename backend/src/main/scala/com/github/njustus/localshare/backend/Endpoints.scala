package com.github.njustus.localshare.backend

import cats.effect.IO
import com.github.njustus.localshare.*
import com.github.njustus.localshare.shared.FilesEndpoints
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint

import java.io.File

class FilesServerEndpoints(fileService: FilesService) extends FilesEndpoints with Endpoints {

  private val listServerEndpoint: ServerEndpoint[Any, IO] {
    type SECURITY_INPUT = Unit; type PRINCIPAL = Unit; type INPUT = List[String]; type ERROR_OUTPUT = Unit;
    type OUTPUT         = List[FileEntry]
  } = listEndpoint.serverLogicSuccess { paths =>
    fileService.list(paths)
  }

  private val uploadServerEndpoint: ServerEndpoint[Any, IO] {
    type SECURITY_INPUT = Unit; type PRINCIPAL = Unit; type INPUT = (List[String], MultipartUpload);
    type ERROR_OUTPUT   = Unit; type OUTPUT    = String
  } = uploadEndpoint.serverLogicSuccess { case (path, FilesEndpoints.MultipartUpload(file)) =>
    fileService.upload(path, file)
  }

  private val downloadServerEndpoint = downloadEndpoint.serverLogicSuccess { paths =>
    fileService.download(paths)
  }

  override val endpoints: List[ServerEndpoint[Any, IO]] = List(listServerEndpoint, uploadServerEndpoint, downloadServerEndpoint)
}

trait Endpoints {
  def endpoints: List[ServerEndpoint[Any, IO]]
}
