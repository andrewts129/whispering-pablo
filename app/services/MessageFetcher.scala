package services

import java.sql.{PreparedStatement, ResultSet, Statement, Timestamp}
import javax.inject.Inject

import play.api.db.Database
import pojos.FeedMessage

class MessageFetcher @Inject()(db: Database) {
  def getFeed: List[FeedMessage] = {
    var feed: List[FeedMessage] = List()

    val connection = db.getConnection()

    try {
      val statement = connection.createStatement()
      val result: ResultSet = statement.executeQuery("SELECT id, text, creation_time, deletion_time FROM messages3;")

      while (result.next()) {
        val id: String = result.getString("id")
        val text: String = result.getString("text")
        val creationTime: Timestamp = result.getTimestamp("creation_time")
        val deletionTime: Timestamp = result.getTimestamp("deletion_time")

        feed = FeedMessage(id, text, creationTime, deletionTime) :: feed
      }

    }
    finally {
      connection.close()
    }

    feed
  }

  def purgeOldMessages(): Unit = {
    val connection = db.getConnection()

    try {
      val statement: Statement = connection.createStatement()
      statement.executeUpdate("DELETE FROM messages3 WHERE deletion_time <= now();")
    }
    finally {
      connection.close()
    }
  }

  def postMessage(id: String, text: String): Unit = {
    val connection = db.getConnection()

    try {
      val statement: PreparedStatement = connection.prepareStatement("INSERT INTO messages3 (id, text, creation_time, deletion_time) VALUES (?, ?, now(), now() + '1 hour');")
      statement.setString(1, id)
      statement.setString(2, text)

      statement.executeUpdate()
    }
    finally {
      connection.close()
    }
  }
}
