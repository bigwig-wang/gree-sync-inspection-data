package com.gree.utils

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

  def get_with_params(url: String, computer: Integer, startTime: String,
                      endTime: String, skipCount: Integer, maxResult: Integer): String = {
    val sb = new StringBuilder(url)
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
    sb.append("&SkipCount=")
    sb.append(skipCount)
    sb.append("&MaxResultCount=")
    sb.append(maxResult)
    println(sb.toString())
    get(sb.toString())
  }

  def main(args: Array[String]): Unit = {
    val url = "http://sysapp.gree.com/GreeMesOpenApi/GreeMesApi/api/services/app/MesQCData/GetQCDatas"
    val computer: Integer = 4
    val startDateTime: String = "2018-09-01"
    val endDateTime: String = "2018-09-02"
    val skipCount: Integer = 3
    val maxResultCount: Integer = 5
    val data = get_with_params(url, computer, startDateTime, endDateTime, skipCount, maxResultCount)

    //如果为空代表已经取完了所有的数据，接着我们需要取下一天的数据，否则增加偏移量继续取，每次增加偏移量1000


  }

  //总数368 从第368个开始取，但是总数也只有368个

}
