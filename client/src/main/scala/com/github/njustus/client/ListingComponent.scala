package com.github.njustus.client

import com.github.njustus.client.components.{DirectoryItem, ListWrapper}
import com.github.njustus.localshare.shared.FilesEndpoints.{FileEntry, MultipartUpload, given}
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom
import org.scalajs.dom.{File, HTMLDivElement, console, window}
import sttp.model.Part

import scala.concurrent.ExecutionContext

class ListingComponent(listingClient: ListEndpointsClient)(using ExecutionContext) {

  private def fileUploadComponent(onFileUpload: dom.File => Unit): Div = {
    def handle(files: List[dom.File]) = files.headOption.foreach(onFileUpload)

    div(
      input(
        className := "file-input file-input-primary",
        `type`    := "file",
        multiple  := false,
        inContext(thisNode => onInput.mapTo(thisNode.ref.files.toList) --> handle)
      ),
      p("Select a file to upload.")
    )
  }

  def render(paths: Seq[String] = Seq()): ReactiveHtmlElement[HTMLDivElement] = {
    val path       = paths.mkString("/")
    val contentVar = Var[List[FileEntry]](List.empty)

    listingClient.list(paths.toList).foreach { entries =>
      val sorted = entries.sortBy(_.`type`)
      contentVar.set(sorted)
    }

    def handle(file: dom.File) =
      listingClient.upload(paths.toList, MultipartUpload(Part(file.name, file))).onComplete {
        case util.Success(value) => window.location.reload()
        case util.Failure(ex)    => console.error(ex)
      }

    div(
      className := "p-6",
      h1(s"Contents of: $path"),
      fileUploadComponent(handle),
      ListWrapper.render(
        "Contents",
        contentVar.toObservable.map { list =>
          list.map(DirectoryItem.render)
        }.changes
      )
    )
  }
}
