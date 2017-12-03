package controllers.api

import java.util.UUID
import javax.inject._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.MessageFetcher

@Singleton
class ApiController @Inject()(cc: ControllerComponents, messageFetcher: MessageFetcher) extends AbstractController(cc) {

  def postMessage(request: String) = Action {
    val requestId:String = UUID.randomUUID().toString

    val(responseId, responseMessage) = messageFetcher.getLastMessage()
    messageFetcher.postMessage(requestId, request)

    val response: JsValue = Json.obj("request" -> Json.obj("id" -> requestId, "message" -> request), "response" -> Json.obj("id" -> responseId, "message" -> responseMessage))
    Ok(response)
  }

}
