package com.github.njustus.client.components

import com.github.njustus.localshare.shared.FilesEndpoints.{FileEntry, FileType}
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLLIElement

object ListWrapper {
  def render(header: HtmlElement, listItems: EventStream[List[LI]]): HtmlElement =
    ul(
      className := "list bg-base-100 rounded-box shadow-md",
      li(className := "p-4 pb-2 text-xs opacity-60 tracking-wide", header),
      children <-- listItems
    )
}

object DirectoryItem {
  def render(entry: FileEntry): ReactiveHtmlElement[HTMLLIElement] = {
    val iconSpan = icon(entry)
    li(
      className := "list-row",
      div(span(className := "size-10, rounded-box", iconSpan)),
      div(
        className := "list-col-grow",
        handleFileType(entry)(
          e => a(href := s"/listing${e.path}", className := "link-hover", e.name),
          e => a(href := s"/api/files/download${e.path}", className := "link-hover", rel := "external", e.name)
        ),
        div(className := "text-xs uppercase font-semibold opacity-60", entry.contentType.getOrElse("unknown"))
      ),
      div(
        className := "btn btn-link",
        handleFileType(entry)(
          e => a(href := s"/listing${e.path}", MatIcon("folder_open")),
          e => a(href := s"/api/files/download${e.path}", rel := "external", MatIcon("download"))
        )
      )
    )
  }

  private def icon(entry: FileEntry) =
    handleFileType(entry)(_ => MatIcon("folder_open"), e => FileIcon.fileIcon(e.name))

  private def handleFileType(
    entry: FileEntry
  )(onDirectory: (FileEntry) => ReactiveHtmlElement[?], onFile: (FileEntry) => ReactiveHtmlElement[?]) =
    entry.`type` match {
      case FileType.Directory => onDirectory(entry)
      case FileType.File      => onFile(entry)
    }
}
