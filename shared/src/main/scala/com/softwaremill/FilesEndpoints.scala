package com.softwaremill

import io.circe.{Decoder, Encoder}
import sttp.model.Part
import sttp.tapir.*
import sttp.tapir.model.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto.*

trait FilesEndpoints {
  import FilesEndpoints.*
  export FilesEndpoints.*
  
  private val basePath = endpoint.in("api").in("files")

  private val filesPath = basePath
    .in("listing")
    .in(paths)

  val uploadEndpoint: Endpoint[Unit, (List[String], MultipartUpload), Unit, String, Any] = basePath
    .in("upload")
    .in(paths)
      .in(
      multipartBody[MultipartUpload])
      .out(stringBody)


  val listEndpoint: Endpoint[Unit, List[String], Unit, List[FileEntry], Any] =
    filesPath.get.out(jsonBody[List[FileEntry]])

//  val downloadEndpoint = downloadPath.get.out(binaryBody(RawBodyType.ByteArrayBody))
}

object FilesEndpoints {
  case class MultipartUpload(file: Part[TapirFile])
  
  case class FileEntry(path: String, name: String, `type`: FileType, sizeInBytes: Long, contentType: Option[String])
      derives Encoder,
        Decoder

  enum FileType derives Encoder, Decoder {
    case File      extends FileType
    case Directory extends FileType
  }
}
