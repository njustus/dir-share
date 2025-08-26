package com.softwaremill

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import com.comcast.ip4s.{Host, Port, port}
import org.http4s.{HttpRoutes, Request, Response, StaticFile}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.http4s.server.staticcontent.*

object BackendMain extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val staticRoutes = resourceServiceBuilder[IO]("/public").toRoutes

    val fallbackRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case req =>
        StaticFile
          .fromResource("/public/index.html", Some(req))
        .getOrElse(Response.notFound)
    }

    val routes = (Http4sServerInterpreter[IO]().toRoutes(Endpoints.all)
                  <+> staticRoutes <+> fallbackRoute)

    val port = sys.env
      .get("HTTP_PORT")
      .flatMap(_.toIntOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")

    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("localhost").get)
      .withPort(port)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use { server =>
        for {
          _ <- IO.println(
                 s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit."
               )
          _ <- IO.readLine
        } yield ()
      }
      .as(ExitCode.Success)
  }
}
