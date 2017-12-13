package pojos

import java.sql.Timestamp

import play.api.libs.json._

case class FeedMessage(id: String, message: String, creationTime: Timestamp, deletionTime: Timestamp)

object FeedMessage {
  implicit object FeedMessageFormat extends Format[FeedMessage] {

    // Not used, just put here so the code compiles, minimal chance it actually works
    override def reads(json: JsValue): JsResult[FeedMessage] = JsSuccess(FeedMessage("", "", null, null))

    override def writes(fm: FeedMessage): JsValue = {
      JsObject(Seq("id" -> JsString(fm.id), "message" -> JsString(fm.message), "creation_time" -> JsString(fm.creationTime.toString), "deletion_time" -> JsString(fm.deletionTime.toString)))
    }
  }
}
