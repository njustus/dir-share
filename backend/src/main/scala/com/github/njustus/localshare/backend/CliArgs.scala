package com.github.njustus.localshare.backend

import com.monovore.decline.*

import java.nio.file.{Path, Paths}
import cats.syntax.all.*
import com.comcast.ip4s.{Host, Port}

import scala.util.Random

final case class CliArgs(port: Port, interface: Host, cwd: Path, secured: Boolean) {
  override def toString: String = s"$interface:$port - $cwd - pwd: $password"

  lazy val password: Option[String] =
    if (!secured) {
      None
    } else {
      val list = Random.alphanumeric.take(50).toList
      val pwd  = Random.shuffle(list).take(8).mkString
      Some(pwd)
    }
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
    Opts
      .argument[String](metavar = "cwd")
      .map(Paths.get(_))
      .withDefault(Paths.get(System.getProperty("user.dir")))
      .map(_.toAbsolutePath)

  private val securedArg: Opts[Boolean] = Opts.flag("secure", short = "s", help = "Secured by password").orTrue

  val cliArgs: Opts[CliArgs] =
    (portOpt, interfaceOpt, cwdArg, securedArg).mapN(CliArgs.apply)
}
