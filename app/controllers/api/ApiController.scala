package controllers.api

import java.sql.Timestamp
import java.util.UUID
import javax.inject._

import play.api.libs.json.{JsArray, JsValue, Json}
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
    val feedData: List[(String, String, Timestamp, Timestamp)] = messageFetcher.getFeed

    var feed: List[FeedMessage] = List()
    for (feedItem: (String, String, Timestamp, Timestamp) <- feedData) {
      feed = FeedMessage(feedItem._1, feedItem._2, feedItem._3, feedItem._4) :: feed
    }

    val AsJson: JsValue = Json.toJson(feed) //TODO this doesn't work
    Ok(AsJson)
  }

  def getNumberSubmissions = Action {
    Ok(Json.obj("number" -> counter.getTotalNumSubmissions))
  }

}
