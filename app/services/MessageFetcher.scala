package services

import java.sql.{ResultSet, Statement}
import java.util.UUID
import javax.inject.Inject

import play.api.db.Database

class MessageFetcher @Inject()(db: Database) {
  var message: String = "Hello World!"
  var id: String = UUID.randomUUID().toString

  def getLastMessage(): (String, String) = {
    val connection = db.getConnection()

    // Default values in case something goes wrong
    var id = "ERROR"
    var message = "ERROR"

    try {
      val statement: Statement = connection.createStatement()

      // Gets the last thing submitted to the db
      val response: ResultSet = statement.executeQuery("SELECT id, text FROM messages WHERE id = (SELECT id FROM messages ORDER BY time DESC LIMIT 1);")
      if (response.next()) {
        id = response.getString("id")
        message = response.getString("text")
      }

      // Deletes the result retrieved above
      // TODO Combine statements
      statement.executeUpdate("DELETE FROM messages WHERE id = '" + id + "'")

    }
    finally {
      connection.close()
    }

    (id, message)
  }

  def postMessage(id: String, text: String): Unit = {
    val connection = db.getConnection()

    try {
      val statement: Statement = connection.createStatement()
      statement.executeUpdate("INSERT INTO messages(id, text, time) VALUES ('" + id + "', '" + text + "', now());")
    }
    finally {
      connection.close()
    }
  }
}
