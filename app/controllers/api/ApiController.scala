package controllers.api

import java.sql.Timestamp
import java.util.UUID
import javax.inject._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{Counter, MessageFetcher}

@Singleton
class ApiController @Inject()(cc: ControllerComponents, messageFetcher: MessageFetcher, counter: Counter) extends AbstractController(cc) {

  def postMessage(message: String) = Action {
    val id: String = UUID.randomUUID().toString

    messageFetcher.postMessage(id, message)
    counter.incrementNumSubmissions()

    val response: JsValue = Json.obj("request" -> Json.obj("id" -> id, "message" -> message))
    Ok(response)
  }

  def getFeed = Action {
    val feed: List[(String, String, Timestamp)] = messageFetcher.getFeed
    val AsJson: JsValue = Json.toJson(feed.map {f => Map("id" -> f._1, "text" -> f._2, "creation_time" -> f._3)})
    Ok(AsJson)
  }

  def getNumberSubmissions = Action {
    Ok(Json.obj("number" -> counter.getTotalNumSubmissions))
  }

}
