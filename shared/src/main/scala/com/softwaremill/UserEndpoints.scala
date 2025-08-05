package com.softwaremill

import sttp.tapir.*

trait UserEndpoints {
  import UserEndpoints.*
  export UserEndpoints.*

  val helloEndpoint: PublicEndpoint[User, Unit, String, Any] = endpoint.get
    .in("hello")
    .in(query[User]("name"))
    .out(stringBody)
}

object UserEndpoints {
  case class User(name: String) extends AnyVal
}
