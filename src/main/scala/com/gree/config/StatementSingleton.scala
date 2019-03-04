package com.gree.config

import java.sql.{DriverManager, Statement}

object StatementSingleton {

  @transient private var statement: Statement = _

  def getInstance(config: JdbcConfig): Statement = {

    if (statement == null) {
      Class.forName(config.driverName)
      val conn = DriverManager.getConnection(config.url, config.user, config.password)
      statement = conn.createStatement()
    }
    statement
  }

}
