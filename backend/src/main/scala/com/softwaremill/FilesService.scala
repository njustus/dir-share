package com.softwaremill

import cats.effect.*
import FilesEndpoints.*

import java.nio.file.*
import scala.jdk.CollectionConverters.given
import cats.syntax.traverse.*
import sttp.model.Part
import sttp.tapir.TapirFile

import java.io.File

class FilesService {

  def list(paths: List[String]): IO[List[FileEntry]] = {
    val path = paths.toPath
    println(s"listing: $path")
    contentPaths(path).flatMap { paths =>
      println(s"found ${paths.size} entries")
      paths.traverse { path =>
        IO {
          val isDirectory = Files.isDirectory(path)
          FileEntry(
            path.toString,
            path.getFileName.toString,
            if (isDirectory) FileType.Directory else FileType.File,
            Files.size(path),
            Option(Files.probeContentType(path))
          )
        }
      }
    }
  }

  def download(paths: List[String]): IO[TapirFile] = {
    val path = paths.toPath
    println(s"download: $path")
    IO(Files.isDirectory(path)).flatMap {
      case true => IO.raiseError(IllegalArgumentException("directory not supported"))
      case _    => IO(path.toFile)
    }
  }

  def upload(paths: List[String], tapirFile: Part[TapirFile]) = IO {
    val path = paths.toPath.resolve(tapirFile.fileName.get) //TODO handle none
    val target = Files.copy(tapirFile.body.toPath, path)
    println(s"Uploaded ${tapirFile.name} into $target")
    s"Uploaded into $target"
  }

  private def contentPaths(path: Path) =
    for {
      stream <- IO(Files.list(path))
      list   <- IO(stream.toList.asScala)
    } yield list.toList


  extension (paths: List[String]) {
    private def toPath: Path = Paths.get("/", paths.mkString("/"))
  }
}
