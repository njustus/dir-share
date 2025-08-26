package com.softwaremill

import sttp.tapir.*
import cats.effect.IO
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import java.io.File

trait FilesServerEndpoints extends FilesEndpoints {
  val fileService: FilesService = FilesService()

  val listServerEndpoint: ServerEndpoint[Any, IO] {
    type SECURITY_INPUT = Unit; type PRINCIPAL = Unit; type INPUT = List[String]; type ERROR_OUTPUT = Unit;
    type OUTPUT         = List[FileEntry]
  } = listEndpoint.serverLogicSuccess { paths =>
    fileService.list(paths)
  }

  val uploadServerEndpoint: ServerEndpoint[Any, IO] {
    type SECURITY_INPUT = Unit; type PRINCIPAL = Unit; type INPUT = (List[String], MultipartUpload);
    type ERROR_OUTPUT   = Unit; type OUTPUT    = String
  } = uploadEndpoint.serverLogicSuccess { case (path, FilesEndpoints.MultipartUpload(file)) =>
    fileService.upload(path, file)
  }

  lazy val downloadServerEndpoint: ServerEndpoint[Any, IO] {
    type SECURITY_INPUT = Unit; type PRINCIPAL = Unit; type INPUT = List[String]; type ERROR_OUTPUT = Unit;
    type OUTPUT         = File
  } = ???
//  downloadEndpoint.serverLogicSuccess { paths =>
//    fileService.download(paths)
//  }

  val endpoints: List[ServerEndpoint[Any, IO]] = List(listServerEndpoint, uploadServerEndpoint)
}

object Endpoints extends UserEndpoints with LibraryEndpoints with FilesServerEndpoints {
  val helloServerEndpoint: ServerEndpoint[Any, IO] =
    helloEndpoint.serverLogicSuccess(user => IO.pure(s"Hello ${user.name}"))

  val booksListingServerEndpoint: ServerEndpoint[Any, IO] = booksListing.serverLogicSuccess(_ => IO.pure(Library.books))

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(helloServerEndpoint, booksListingServerEndpoint) ++ endpoints

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "local-share", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints
}
