package com.github.njustus.client

import com.github.njustus.client.components.{BreadcrumbList, DirectoryItem, ListWrapper}
import com.github.njustus.localshare.shared.FilesEndpoints.{FileEntry, FileType, MultipartUpload, given}
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom
import org.scalajs.dom.{HTMLDivElement, console, window}
import sttp.model.Part

import scala.concurrent.ExecutionContext

enum SortMode {
  case ByName, ByDate
}

class ListingComponent(listingClient: ListEndpointsClient)(using ExecutionContext) {

  private def sortEntries(entries: List[FileEntry], mode: SortMode): List[FileEntry] = {
    val (dirs, files) = entries.partition(_.`type` == FileType.Directory)
    val sortedFiles = mode match {
      case SortMode.ByName => files.sortBy(_.name)
      case SortMode.ByDate => files.sortBy(_.lastModifiedAt)(using Ordering[Long].reverse)
    }
    dirs.sortBy(_.name) ++ sortedFiles
  }

  private def fileUploadComponent(onFilesUpload: List[dom.File] => Unit): Div =
    div(
      className := "flex flex-col justify-stretch gap-2",
      p("Select files to upload."),
      input(
        className := "file-input file-input-primary w-full",
        `type`    := "file",
        multiple  := true,
        inContext(thisNode => onInput.mapTo(thisNode.ref.files.toList) --> onFilesUpload)
      )
    )

  def render(paths: Seq[String] = Seq()): ReactiveHtmlElement[HTMLDivElement] = {
    val dirPath = s"/${paths.mkString("/")}"

    val contentVar         = Var[List[FileEntry]](List.empty)
    val showHiddenFilesVar = Var(false)
    val sortModeVar        = Var[SortMode](SortMode.ByName)

    listingClient.list(paths.toList).foreach { entries =>
      contentVar.set(entries)
    }

    def handle(files: List[dom.File]) = {
      listingClient.upload(paths.toList, MultipartUpload(files.map(f => Part(f.name, f)))).onComplete {
        case util.Success(_)  => window.location.reload()
        case util.Failure(ex) => console.error(ex)
      }
    }

    val displayedListItems = contentVar.signal
      .combineWithFn(showHiddenFilesVar.signal) { (xs, showHidden) =>
        if showHidden then xs else xs.filter(!_.isHidden)
      }
      .combineWithFn(sortModeVar.signal)(sortEntries)

    def sortButton(label: String, mode: SortMode) =
      button(
        className <-- sortModeVar.signal.map(m =>
          "btn btn-xs" + (if m == mode then " btn-active" else "")
        ),
        label,
        onClick --> { _ => sortModeVar.set(mode) }
      )

    div(
      className := "flex flex-col gap-4",
      h2(className := "font-semibold text-2xl", s"Current Directory: ${dirPath}"),
      BreadcrumbList.render(paths),
      fileUploadComponent(handle),
      ListWrapper.render(
        div(
          className := "flex items-center gap-2",
          div(className := "flex flex-1", "Contents"),
          div(
            className := "flex items-center gap-1",
            span(className := "text-xs opacity-60", "Sort:"),
            sortButton("Name", SortMode.ByName),
            sortButton("Date", SortMode.ByDate)
          ),
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
        displayedListItems.changes.map { list =>
          list.map(DirectoryItem.render)
        }
      )
    )
  }
}
