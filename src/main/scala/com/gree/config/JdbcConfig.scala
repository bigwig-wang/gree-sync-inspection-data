package com.gree.config

import com.google.inject.Inject
import com.google.inject.name.Named

class JdbcConfig {

  @Inject
  @Named("impala.jdbc.driver")
  var driverName: String = _

  @Inject
  @Named("impala.jdbc.url")
  var url: String = _

  @Inject
  @Named("impala.jdbc.user")
  var user: String = _

  @Inject
  @Named("impala.jdbc.password")
  var password: String = _

}
