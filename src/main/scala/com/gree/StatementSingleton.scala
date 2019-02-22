package com.gree

import java.sql.{DriverManager, Statement}
import java.util.Properties

object StatementSingleton {

  @transient private var statement: Statement = _

  def getInstance(): Statement = {

    if(statement == null) {
      val in = StatementSingleton.getClass.getClassLoader.getResourceAsStream("jdbc.properties")
      val properties = new Properties()
      properties.load(in)

      val driverName = properties.get("impala.jdbc.driver").toString
      val url = properties.get("impala.jdbc.url").toString
      val user = properties.get("impala.jdbc.user").toString
      val password = properties.get("impala.jdbc.password").toString

      Class.forName(driverName)
      val conn = DriverManager.getConnection(url, user, password)
      statement =  conn.createStatement()
    }
    statement
  }

}
