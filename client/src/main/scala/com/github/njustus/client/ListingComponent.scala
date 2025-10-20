package com.github.njustus.client

import com.github.njustus.client.components.{BreadcrumbList, DirectoryItem, ListWrapper}
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
      className := "flex flex-col justify-stretch gap-2",
      p("Select a file to upload."),
      input(
        className := "file-input file-input-primary w-full",
        `type`    := "file",
        multiple  := false,
        inContext(thisNode => onInput.mapTo(thisNode.ref.files.toList) --> handle)
      )
    )
  }

  def render(paths: Seq[String] = Seq()): ReactiveHtmlElement[HTMLDivElement] = {
    val dirPath = s"/${paths.mkString("/")}"

    val contentVar         = Var[List[FileEntry]](List.empty)
    val showHiddenFilesVar = Var(false)

    listingClient.list(paths.toList).foreach { entries =>
      val sorted = entries.sortBy(_.`type`)
      contentVar.set(sorted)
    }

    def handle(file: dom.File) =
      listingClient.upload(paths.toList, MultipartUpload(Part(file.name, file))).onComplete {
        case util.Success(value) => window.location.reload()
        case util.Failure(ex)    => console.error(ex)
      }

    val displayedListItems = contentVar.toObservable.combineWithFn(showHiddenFilesVar) {
      case (xs, true)  => xs
      case (xs, false) => xs.filter(entry => !entry.isHidden)
    }

    div(
      className := "flex flex-col gap-4",
      h2(className := "font-semibold text-2xl", s"Current Directory: ${dirPath}"),
      BreadcrumbList.render(paths),
      fileUploadComponent(handle),
      ListWrapper.render(
        div(
          className := "flex",
          div(className := "flex flex-1", "Contents"),
          label(
            className := "label",
            input(
              `type`    := "checkbox",
              className := "toggle toggle-warning",
              controlled(checked <-- showHiddenFilesVar.signal, onInput.mapToChecked --> showHiddenFilesVar.writer)
            ),
            "Show hidden files: "
          )
        ),
        displayedListItems.toObservable.map { list =>
          list.map(DirectoryItem.render)
        }.changes
      )
    )
  }
}
