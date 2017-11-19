package services

import java.util.UUID

import akka.http.scaladsl.model.headers.CacheDirectives.public

class MessageFetcher {
  var message: String = "Hello World!"
  var id: String = UUID.randomUUID().toString
}
