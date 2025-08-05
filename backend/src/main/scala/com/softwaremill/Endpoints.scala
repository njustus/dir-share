package com.softwaremill

import sttp.tapir.*

import cats.effect.IO
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Endpoints extends UserEndpoints with LibraryEndpoints {
  val helloServerEndpoint: ServerEndpoint[Any, IO] =
    helloEndpoint.serverLogicSuccess(user => IO.pure(s"Hello ${user.name}"))

  val booksListingServerEndpoint: ServerEndpoint[Any, IO] = booksListing.serverLogicSuccess(_ => IO.pure(Library.books))

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(helloServerEndpoint, booksListingServerEndpoint)

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "local-share", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints
}
