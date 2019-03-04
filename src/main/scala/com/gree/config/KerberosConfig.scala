package com.gree.config

import java.io.{File, IOException}

import com.google.inject.Inject
import com.google.inject.name.Named
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.spark.internal.Logging

class KerberosConfig extends Logging {

  @Inject
  @Named("kerberos.krb5.location")
  var krb5Location: String = _

  @Inject
  @Named("kerberos.keytab.localtion")
  var keytabLocation: String = _

  @Inject
  @Named("kerberos.user")
  var kerberosUser: String = _

  def init() = {

    new File("./").listFiles().foreach(f => {
      logInfo(f.getAbsolutePath)
    })

    System.setProperty("java.security.krb5.conf", krb5Location)

    try {
      val configuration = new Configuration()
      configuration.set("hadoop.security.authentication", "Kerberos")

      UserGroupInformation.setConfiguration(configuration)
      logInfo("------------------------------" + keytabLocation + "------------------------------")
      logInfo("------------------------------" + kerberosUser + "------------------------------")
      UserGroupInformation.loginUserFromKeytab(kerberosUser, keytabLocation)
    } catch {
      case e: IOException => {
        e.printStackTrace()
      }
    }
  }
}
