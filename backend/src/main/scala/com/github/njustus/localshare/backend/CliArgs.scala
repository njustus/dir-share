package com.github.njustus.localshare.backend

import com.monovore.decline.*

import java.nio.file.{Path, Paths}
import cats.syntax.all.*
import com.comcast.ip4s.{Host, Port}

final case class CliArgs(
                          port: Port,
                          interface: Host,
                          cwd: Path
                        ) {
  override def toString: String = s"$interface:$port - $cwd"
}

object CliArgs {
  private val portOpt: Opts[Port] =
    Opts
      .option[Int]("port", short = "p", help = "Server port.")
      .orElse(Opts.env[Int]("PORT", "Server port (from env)"))
      .withDefault(8080)
      .map(Port.fromInt)
      .map(_.get)

  private val interfaceOpt: Opts[Host] =
    Opts
      .option[String]("interface", short = "i", help = "Server interface.")
      .withDefault("0.0.0.0")
      .map(Host.fromString)
      .map(_.get)

  private val cwdArg: Opts[Path] =
    Opts.argument[String](metavar = "cwd")
      .map(Paths.get(_))
      .withDefault(Paths.get(System.getProperty("user.dir")))
      .map(_.toAbsolutePath)

  val cliArgs: Opts[CliArgs] =
    (portOpt, interfaceOpt, cwdArg).mapN(CliArgs.apply)
}
