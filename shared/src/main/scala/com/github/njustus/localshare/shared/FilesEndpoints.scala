package com.github.njustus.localshare.shared

import io.circe.{Decoder, Encoder}
import sttp.model.Part
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody

trait FilesEndpoints {
  import FilesEndpoints.*
  export FilesEndpoints.*

  // TODO require password for upload -- eg generate random hash as token

  private val basePath = endpoint.in("api").in("files")

  private val filesPath = basePath
    .in("listing")
    .in(paths)

  val uploadEndpoint: Endpoint[Unit, (List[String], MultipartUpload), Unit, String, Any] = basePath
    .in("upload")
    .in(paths)
    .post
    .in(multipartBody[MultipartUpload])
    .out(stringBody)

  val downloadEndpoint: Endpoint[Unit, List[String], Unit, DownloadOutput, Any] = basePath
    .in("download")
    .in(paths)
    .get
    .out(header[String]("Content-Disposition"))
    .out(header[Option[String]]("Content-Type"))
    .out(fileBody)
    .mapOutTo[DownloadOutput]

  val listEndpoint: Endpoint[Unit, List[String], Unit, List[FileEntry], Any] =
    filesPath.get.out(jsonBody[List[FileEntry]])
}

object FilesEndpoints {
  case class MultipartUpload(files: List[Part[TapirFile]])

  case class FileEntry(
    path: String,
    name: String,
    `type`: FileType,
    sizeInBytes: Long,
    contentType: Option[String],
    lastModifiedAt: Long
  ) derives Encoder,
      Decoder {
    lazy val isHidden: Boolean = name.startsWith(".")
  }

  enum FileType derives Encoder, Decoder {
    case Directory extends FileType
    case File      extends FileType
  }

  case class DownloadOutput(contentDisposition: String, contentType: Option[String], content: TapirFile)

  given Ordering[FileType]  = Ordering.by(_.ordinal)
  given Ordering[FileEntry] = Ordering.by(entry => (entry.`type`, entry.name))
}
