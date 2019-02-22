package com.gree

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object HttpClientUtils {
  //后面需要加上get的参数
  def get(url: String): String = {
    val client: HttpClient = HttpClients.createDefault()

    val get = new HttpGet(url)
    val response = client.execute(get)
    val entity = response.getEntity
    EntityUtils.toString(entity, "UTF-8")
  }

  def deal_params(computer: Integer, startTime: String,
                  endTime: String, skipCount: Integer, maxResult: Integer): Unit = {
    val sb = new StringBuilder("http://localhost:8080/hello")
    //Computer  公司或基地 郑州格力:1 石家庄格力:2 武汉格力:3 长沙格力:4 珠海格力:5 芜湖格力:6 重庆格力:7 合肥格力:8 interger
    // StartDateTime 开始日期 yyyy-MM-dd string
    //EndDateTime 结束日期 yyyy-MM-dd  string
    //SkipCount integer
    //MaxResultCount integer
    sb.append("?Computer=")
    sb.append(computer)
    sb.append("&StartDateTime=")
    sb.append(startTime)
    sb.append("&EndDateTime=")
    sb.append(endTime)
    sb.append("&skipCount=")
    sb.append(skipCount)
    sb.append("&MaxResultCount=")
    sb.append(maxResult)
    get(sb.toString())
  }

  def main(args: Array[String]): Unit = {
    get("http://localhost:8080/hello")
  }

}
