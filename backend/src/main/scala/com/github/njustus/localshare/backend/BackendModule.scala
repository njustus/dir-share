package com.github.njustus.localshare.backend

import com.softwaremill.macwire.*
import cats.effect.IO
import com.github.njustus.localshare.*
import org.http4s.{HttpRoutes, Response, StaticFile}
import org.http4s.server.staticcontent.*
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import cats.implicits.*
import com.typesafe.scalalogging.StrictLogging

class BackendModule(cliArgs: CliArgs) extends Endpoints with StrictLogging {
  private val filesServerEndpoints = wireRec[FilesServerEndpoints]

  override def endpoints: List[ServerEndpoint[Any, IO]] = {
    val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(filesServerEndpoints.endpoints).flatten

    val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
      .fromServerEndpoints[IO](apiEndpoints, "local-share", "1.0.0")

    apiEndpoints ++ docEndpoints
  }

  lazy val routes: HttpRoutes[IO] = {
    val staticRoutes = resourceServiceBuilder[IO]("/public").toRoutes
    val fallbackRoute: HttpRoutes[IO] = HttpRoutes.of[IO] { case req =>
      StaticFile
        .fromResource("/public/index.html", Some(req))
        .getOrElse(Response.notFound)
    }

    logger.info(s"Server started with config: $cliArgs")
    (Http4sServerInterpreter[IO]().toRoutes(endpoints)
      <+> staticRoutes <+> fallbackRoute)
  }

}
