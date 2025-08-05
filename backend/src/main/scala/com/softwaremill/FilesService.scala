package com.softwaremill

import cats.effect.*
import FilesEndpoints.*

import java.nio.file.*
import scala.jdk.CollectionConverters.given
import cats.syntax.traverse.*
import org.http4s.MediaType

class FilesService {

  def list(paths: List[String]): IO[List[FileEntry]] = {
    val path = Paths.get("/", paths.mkString("/"))
    println(s"listing: $path")
    contentPaths(path).flatMap { paths =>
      println(s"found ${paths.size} entries")
      paths.traverse { path => IO {
        val isDirectory = Files.isDirectory(path)
        FileEntry(path.toString,
          if(isDirectory) FileType.Directory else FileType.File,
          Files.size(path),
          Option(Files.probeContentType(path))
        )
      }}
    }
  }

  private def contentPaths(path: Path) =
    for {
      stream <- IO { Files.list(path) }
      list <- IO { stream.toList.asScala }
    } yield list.toList
}
