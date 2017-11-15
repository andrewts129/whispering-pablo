package controllers.api

import java.util.UUID
import javax.inject._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

@Singleton
class ApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def postMessage(request: String) = Action {
    val requestId: String = UUID.randomUUID().toString
    val response: JsValue = Json.obj("request" -> request, "request_id" -> requestId, "response" -> "TODO", "response_id" -> "TODO")
    Ok(response)
  }

}
