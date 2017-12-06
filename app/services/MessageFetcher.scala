package services

import java.sql.{PreparedStatement, ResultSet, Statement}
import java.util.UUID
import javax.inject.Inject

import play.api.db.Database

class MessageFetcher @Inject()(db: Database) {
  def getLastMessage: (String, String) = {
    val connection = db.getConnection()

    // Default values in case something goes wrong
    var id = "ERROR"
    var message = "ERROR"

    try {
      val insertStatement: Statement = connection.createStatement()

      // Gets the last thing submitted to the db
      val response: ResultSet = insertStatement.executeQuery("SELECT id, text FROM messages WHERE id = (SELECT id FROM messages ORDER BY time DESC LIMIT 1);")
      if (response.next()) {
        id = response.getString("id")
        message = response.getString("text")
      }

      // Deletes the result retrieved above
      // TODO Combine statements
      val deleteStatement: PreparedStatement = connection.prepareStatement("DELETE FROM messages WHERE id = ?")
      deleteStatement.setString(1, id)

      deleteStatement.executeUpdate()

    }
    finally {
      connection.close()
    }

    (id, message)
  }

  def postMessage(id: String, text: String): Unit = {
    val connection = db.getConnection()

    try {
      val statement: PreparedStatement = connection.prepareStatement("INSERT INTO messages(id, text, time) VALUES (?, ?, now());")
      statement.setString(1, id)
      statement.setString(2, text)

      statement.executeUpdate()
    }
    finally {
      connection.close()
    }
  }
}
