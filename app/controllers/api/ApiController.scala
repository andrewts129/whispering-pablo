package controllers.api

import java.util.UUID
import javax.inject._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import pojos.FeedMessage
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
    messageFetcher.purgeOldMessages()

    val feed: List[FeedMessage] = messageFetcher.getFeed

    Ok(Json.toJson(feed))
  }

  def getNumberSubmissions = Action {
    Ok(Json.obj("number" -> counter.getTotalNumSubmissions))
  }

}
