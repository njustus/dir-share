package com.github.njustus.client.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLSpanElement

object FileIcon {
  case class FileIcon(name: String, colorClass: String)

  private val fileIcons: Map[String, FileIcon] = Map(
    // Document Icons
    "pdf"  -> FileIcon("picture_as_pdf", "text-red-500"),
    "doc"  -> FileIcon("description", "text-blue-500"),
    "docx" -> FileIcon("description", "text-blue-500"),
    "ppt"  -> FileIcon("description", "text-orange-500"),
    "pptx" -> FileIcon("description", "text-orange-500"),
    "odt"  -> FileIcon("description", "text-purple-500"),
    "odp"  -> FileIcon("description", "text-orange-500"),
    "rtf"  -> FileIcon("description", "text-gray-500"),

    // Spreadsheet Icons
    "xls"  -> FileIcon("table", "text-green-600"),
    "xlsx" -> FileIcon("table", "text-green-600"),
    "csv"  -> FileIcon("table", "text-green-600"),

    // Code & Text Icons
    "txt"   -> FileIcon("code", "text-gray-500"),
    "log"   -> FileIcon("code", "text-gray-500"),
    "sh"    -> FileIcon("code", "text-gray-500"),
    "md"    -> FileIcon("markdown", "text-gray-500"),
    "conf"  -> FileIcon("settings", "text-gray-500"),
    "json"  -> FileIcon("file_json", "text-purple-600"),
    "xml"   -> FileIcon("code", "text-yellow-600"),
    "html"  -> FileIcon("html", "text-orange-600"),
    "htm"   -> FileIcon("html", "text-orange-600"),
    "css"   -> FileIcon("code", "text-blue-600"),
    "js"    -> FileIcon("javascript", "text-yellow-500"),
    "ts"    -> FileIcon("code", "text-blue-700"),
    "java"  -> FileIcon("code", "text-red-600"),
    "scala" -> FileIcon("code", "text-orange-600"),
    "py"    -> FileIcon("code", "text-blue-600"),
    "cs"    -> FileIcon("code", "text-purple-600"),
    "c"     -> FileIcon("code", "text-blue-500"),
    "cpp"   -> FileIcon("code", "text-blue-500"),

    // Media Icons
    "jpg"  -> FileIcon("image", "text-pink-500"),
    "jpeg" -> FileIcon("image", "text-pink-500"),
    "png"  -> FileIcon("image", "text-pink-500"),
    "gif"  -> FileIcon("image", "text-pink-500"),
    "svg"  -> FileIcon("image", "text-pink-500"),
    "webp" -> FileIcon("image", "text-pink-500"),
    "mp3"  -> FileIcon("audio_file", "text-blue-500"),
    "wav"  -> FileIcon("audio_file", "text-blue-500"),
    "ogg"  -> FileIcon("audio_file", "text-blue-500"),
    "mp4"  -> FileIcon("video_file", "text-red-500"),
    "mov"  -> FileIcon("video_file", "text-red-500"),
    "avi"  -> FileIcon("video_file", "text-red-500"),
    "mkv"  -> FileIcon("video_file", "text-red-500"),
    "webm" -> FileIcon("video_file", "text-red-500"),

    // Archive Icons
    "zip" -> FileIcon("folder_zip", "text-yellow-500"),
    "rar" -> FileIcon("folder_zip", "text-yellow-500"),
    "7z"  -> FileIcon("folder_zip", "text-yellow-500"),
    "tar" -> FileIcon("folder_zip", "text-yellow-500")
  )

  private def getFileIcon(extension: String): FileIcon =
    fileIcons.getOrElse(extension.toLowerCase, FileIcon("draft", "text-gray-400"))

  def fileIcon(fileName: String): ReactiveHtmlElement[HTMLSpanElement] = {
    val suffix = fileName.split('.').last
    val icon   = getFileIcon(suffix)

    span(className := icon.colorClass, MatIcon(icon.name))
  }
}
