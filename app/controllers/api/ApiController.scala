package controllers.api

import java.sql.Timestamp
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

    val feedData: List[(String, String, Timestamp, Timestamp)] = messageFetcher.getFeed
    val feed: List[FeedMessage] = for (x: (String, String, Timestamp, Timestamp) <- feedData) yield FeedMessage(x._1, x._2, x._3, x._4)

    val AsJson: JsValue = Json.toJson(feed)
    Ok(AsJson)
  }

  def getNumberSubmissions = Action {
    Ok(Json.obj("number" -> counter.getTotalNumSubmissions))
  }

}
