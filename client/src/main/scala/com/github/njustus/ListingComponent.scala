package com.github.njustus

import com.raquo.laminar.api.L.*
import com.softwaremill.FilesEndpoints.{FileEntry, FileType, MultipartUpload}

import scala.concurrent.ExecutionContext
import org.scalajs.dom.{Event, File, FileList, HTMLDivElement, InputEvent, console, window}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom
import sttp.model.Part
import sttp.tapir.TapirFile

import scala.collection.mutable.ArrayBuffer

class ListingComponent(listingClient: ListEndpointsClient)(using ExecutionContext) {

  private def fileUploadComponent(onFileUpload: dom.File => Unit): Div = {
//    // Event bus to handle file changes
//    val fileEvents = new EventBus[List[dom.File]]
//
//    // Observer to process the file
//    val fileObserver = fileEvents.events.map { files =>
//      val file = files.head
//    }

    def handle(files: List[dom.File]) = files.headOption.foreach(onFileUpload)

    div(
      input(
        className := "file-input file-input-primary",
        `type` := "file",
        multiple := false,
        inContext(thisNode => onInput.mapTo(thisNode.ref.files.toList) --> handle)
      ),
      p(
        "Select a file to upload."
      )
    )
  }

  def render(paths: Seq[String]): ReactiveHtmlElement[HTMLDivElement] = {
    val path       = paths.mkString("/")
    val contentVar = Var[List[FileEntry]](List.empty)

    listingClient.list(paths.toList).foreach { entries =>
      contentVar.set(entries)
    }

    def handle(file: dom.File) = {
      listingClient.upload(paths.toList, MultipartUpload(Part(file.name, file))).onComplete {
        case util.Success(value) => window.location.reload()
        case util.Failure(ex) => console.error(ex)
      }
    }

    div(
      className := "p-6",
      h1(s"Contents of: $path"),
      fileUploadComponent(handle),
      ul(
        className := "list-inside list-disc",
        children <-- contentVar.toObservable.map { list =>
          list.map {
            case entry if entry.`type` == FileType.File =>
              li(span(className := "font-semibold", entry.name, " == ", entry.contentType))
            case entry =>
              li(a(className := "font-semibold", href := s"/listing${entry.path}", entry.name))
          }
        }
      )
    )
  }
}
