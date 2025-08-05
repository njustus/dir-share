package com.softwaremill

import io.circe.{Decoder, Encoder}
import sttp.tapir.*
import sttp.tapir.RawBodyType.FileBody
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto.*

trait FilesEndpoints {
  import FilesEndpoints.*
  export FilesEndpoints.*

  private val basePath = endpoint.in("files")

  private val filesPath =  basePath.in("listing")
    .in(paths)

  private val downloadPath = basePath.in("download")
    .in(paths)

  val listEndpoint = filesPath.get.out(jsonBody[List[FileEntry]])

  val downloadEndpoint = downloadPath.get.out(rawBinaryBody(FileBody))
}

object FilesEndpoints {
  case class FileEntry(path: String, `type`: FileType)
    derives Encoder, Decoder

  enum FileType derives Encoder, Decoder {
    case File
    case Directory
  }
}
