package services

import java.sql.{ResultSet, Statement}
import javax.inject.Inject

import play.api.db.Database

class Counter @Inject()(db: Database) {
  def getTotalNumSubmissions: Int = {
    val connection = db.getConnection()

    var result: Int = -1

    try {
      val statement: Statement = connection.createStatement()
      val response: ResultSet = statement.executeQuery("SELECT value FROM stats WHERE key = 'num_submissions';")

      if (response.next()) {
        result = response.getString("value").toInt
      }
    }
    finally {
      connection.close()
    }

    result
  }

  def incrementNumSubmissions(): Unit = {
    val connection = db.getConnection()
    try {
      val statement: Statement = connection.createStatement()
      statement.executeUpdate("UPDATE stats SET value = value + 1 WHERE key = 'num_submissions';")
    }
    finally {
      connection.close()
    }

  }
}
