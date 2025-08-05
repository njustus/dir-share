package com.softwaremill

import io.circe.{Decoder, Encoder}
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto.*

trait LibraryEndpoints {
  import Library.*

  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] = endpoint.get
    .in("books" / "list" / "all")
    .out(jsonBody[List[Book]])
}

object Library {
  case class Author(name: String) derives Encoder, Decoder
  case class Book(title: String, year: Int, author: Author) derives Encoder, Decoder

  val books: List[Book] = List(
    Book("The Sorrows of Young Werther", 1774, Author("Johann Wolfgang von Goethe")),
    Book("On the Niemen", 1888, Author("Eliza Orzeszkowa")),
    Book("The Art of Computer Programming", 1968, Author("Donald Knuth")),
    Book("Pharaoh", 1897, Author("Boleslaw Prus"))
  )
}
