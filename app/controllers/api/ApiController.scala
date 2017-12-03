package controllers.api

import java.util.UUID
import javax.inject._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

@Singleton
class ApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private val messageFetcher = new services.MessageFetcher()

  def postMessage(request: String) = Action {
    val requestId:String = UUID.randomUUID().toString

    val responseMessage: String = messageFetcher.message
    val responseId: String = messageFetcher.id

    messageFetcher.message = request
    messageFetcher.id = requestId

    val response: JsValue = Json.obj("request" -> Json.obj("message" -> request, "id" -> requestId), "response" -> Json.obj("message" -> responseMessage, "id" -> responseId))
    Ok(response)
  }

}
