package com.github.njustus.localshare.backend

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import com.comcast.ip4s.{Host, Port, port}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import com.typesafe.scalalogging.StrictLogging
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.staticcontent.*
import org.http4s.{HttpRoutes, Request, Response, StaticFile}
import sttp.tapir.server.http4s.Http4sServerInterpreter

object BackendMain extends CommandIOApp(
      name = "local-share",
      header = "local-share Server CLI"
    ) with StrictLogging {

   override def main: Opts[IO[ExitCode]] = CliArgs.cliArgs.map { cliArgs =>
    val module = BackendModule(cliArgs)

    EmberServerBuilder
      .default[IO]
      .withHost(cliArgs.interface)
      .withPort(cliArgs.port)
      .withHttpApp(Router("/" -> module.routes).orNotFound)
      .build
      .use { server =>
        for {
          _ <- IO(
            logger.info(
                 s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit."
               ))
          _ <- IO.readLine
        } yield ()
      }
      .as(ExitCode.Success)
   }
}
