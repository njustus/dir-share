package com.github.njustus.components

import com.raquo.laminar.api.L.*
import com.softwaremill.FilesEndpoints.{FileEntry, FileType}

import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLLIElement

object ListWrapper {
  def render(title: String, listItems: EventStream[List[LI]]): HtmlElement =
    ul(
      className := "list bg-base-100 rounded-box shadow-md",
      li(className := "p-4 pb-2 text-xs opacity-60 tracking-wide", title),
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
        div(entry.name),
        div(className := "text-xs uppercase font-semibold opacity-60", entry.contentType.getOrElse("unknown"))
      ),
      button(
        className := "btn btn-square btn-ghost",
        handleFileType(entry)(
          e => a(href := s"/listing${e.path}", MatIcon("folder_open")),
          e =>
            a(
              // TODO download?
              href   := s"/api/download${e.path}",
              target := "_blank",
              MatIcon("download")
            )
        )
      )
    )
  }

  private def icon(entry: FileEntry) = handleFileType(entry)(_ => MatIcon("folder"), _ => MatIcon("draft"))

  private def handleFileType(
    entry: FileEntry
  )(onDirectory: (FileEntry) => ReactiveHtmlElement[?], onFile: (FileEntry) => ReactiveHtmlElement[?]) =
    entry.`type` match {
      case FileType.Directory => onDirectory(entry)
      case FileType.File      => onFile(entry)
    }
}
