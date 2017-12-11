package services

import java.sql.{PreparedStatement, ResultSet, Statement, Timestamp}
import java.util.UUID
import javax.inject.Inject

import play.api.db.Database

import scala.collection.mutable.ListBuffer

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

  def getFeed: List[(String, String, Timestamp)] = {
    var feed: List[(String, String, Timestamp)] = List()

    val connection = db.getConnection()

    purgeOldMessages()

    try {
      val statement = connection.createStatement()
      val result: ResultSet = statement.executeQuery("SELECT id, text, creation_time FROM messages2;")

      while (result.next()) {
        val id: String = result.getString("id")
        val text: String = result.getString("text")
        val creationTime: Timestamp = result.getTimestamp("creation_time")

        feed = (id, text, creationTime) :: feed
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
      statement.executeUpdate("DELETE FROM messages2 WHERE creation_time + interval '60 seconds' < now();")
    }
    finally {
      connection.close()
    }
  }

  def postMessage(id: String, text: String): Unit = {
    val connection = db.getConnection()

    try {
      val statement: PreparedStatement = connection.prepareStatement("INSERT INTO messages2(id, text, creation_time) VALUES (?, ?, now());")
      statement.setString(1, id)
      statement.setString(2, text)

      statement.executeUpdate()
    }
    finally {
      connection.close()
    }
  }
}
