package com.softwaremill

import sttp.tapir.*
import cats.effect.IO
import sttp.tapir.RawBodyType.FileBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

trait FilesServerEndpoints extends FilesEndpoints {
  val fileService = FilesService()
  
  val listServerEndpoint = listEndpoint.serverLogicSuccess { paths =>
    fileService.list(paths)
  }

  val downloadServerEndpoint = downloadEndpoint.serverLogicSuccess { paths =>
    fileService.download(paths)
  }

  val endpoints = List(listServerEndpoint, downloadServerEndpoint)
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
