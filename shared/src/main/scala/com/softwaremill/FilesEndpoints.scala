package com.softwaremill

import io.circe.{Decoder, Encoder}
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto.*
import java.io.File

trait FilesEndpoints {
  import FilesEndpoints.*
  export FilesEndpoints.*

  private val basePath = endpoint.in("files")

  private val filesPath =  basePath.in("listing")
    .in(paths)

  private val downloadPath = basePath.in("download")
    .in(paths)

  val listEndpoint: Endpoint[Unit, List[String], Unit, List[FileEntry], Any] = filesPath.get.out(jsonBody[List[FileEntry]])

  val downloadEndpoint: Endpoint[Unit, List[String], Unit, File, Any] = downloadPath.get.out(fileBody)
}

object FilesEndpoints {
  case class FileEntry(path: String,
                       `type`: FileType,
                       sizeInBytes: Long,
                       contentType: Option[String])
    derives Encoder, Decoder

  enum FileType derives Encoder, Decoder {
    case File extends FileType
    case Directory extends FileType
  }
}
