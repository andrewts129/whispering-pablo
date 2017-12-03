package controllers.api

import java.util.UUID
import javax.inject._

import play.api.db.Database
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

@Singleton
class ApiController @Inject()(cc: ControllerComponents, db: Database) extends AbstractController(cc) {
  private val messageFetcher = new services.MessageFetcher()

  def postMessage(request: String) = Action {
    val requestId:String = UUID.randomUUID().toString

    val responseMessage: String = messageFetcher.message
    val responseId: String = messageFetcher.id

    messageFetcher.message = request
    messageFetcher.id = requestId

    val conn = db.getConnection()
    conn.createStatement().execute("INSERT INTO messages(id, text, time) VALUES ('testId', 'testMessage', now())")
    conn.close()

    val response: JsValue = Json.obj("request" -> Json.obj("message" -> request, "id" -> requestId), "response" -> Json.obj("message" -> responseMessage, "id" -> responseId))
    Ok(response)
  }

}
