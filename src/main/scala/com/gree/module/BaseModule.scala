package com.gree.module

import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Scopes}
import com.gree.utils.PropertyUtil

class BaseModule(path:String) extends AbstractModule {

  override def configure(): Unit = {
    Names.bindProperties(binder(), PropertyUtil.loadFile(path+"/kafka.properties"))
    Names.bindProperties(binder(), PropertyUtil.loadFile(path+"/jdbc.properties"))
    Names.bindProperties(binder(), PropertyUtil.loadFile(path+"/kerberos.properties"))
    Names.bindProperties(binder(), PropertyUtil.loadFile(path+"/kudu.properties"))
  }
}
