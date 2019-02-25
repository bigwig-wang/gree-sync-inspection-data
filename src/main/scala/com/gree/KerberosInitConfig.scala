package com.gree

import java.io.IOException
import java.util.Properties

import com.gree.singleton.StatementSingleton
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation

object KerberosInitConfig {

  def initKerberos():Unit = {
    val in = StatementSingleton.getClass.getClassLoader.getResourceAsStream("gree/kerberos.properties")
    val properties = new Properties()
    properties.load(in)

    val krb5Location = properties.get("kerberos.krb5.location").toString
    val keytabLocation = properties.get("kerberos.keytab.localtion").toString
    val kerberosUser = properties.get("kerberos.user").toString

    System.setProperty("java.security.krb5.conf", krb5Location)
    try {
      val configuration = new Configuration()
      configuration.set("hadoop.security.authentication" , "Kerberos" )
      UserGroupInformation.setConfiguration(configuration)
      UserGroupInformation.loginUserFromKeytab(kerberosUser, keytabLocation)
    } catch {
      case e: IOException => {
        e.printStackTrace()
      }
    }
  }

}
