package com.leejuhaeun.weatherseeker.WeatherAdviceController

import android.util.Log
import com.leejuhaeun.weatherseeker.WeatherAdviceImpl.WeatherNews
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class YonhapWeatherNews: WeatherNews {
    @Throws(IOException::class)
    override fun getWeatherNews():ArrayList<String>{
        val startTime = System.currentTimeMillis()

        var newsData = ArrayList<String>()

        try {
            val urlBuilder = StringBuilder("http://www.yonhapnewstv.co.kr/category/news/weather/") /*URL*/

            val url = URL(urlBuilder.toString())
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8") // Accept-Charset 설정.
            conn.doInput = true
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8")
            val rd: BufferedReader

            if (conn.responseCode >= 200 && conn.responseCode <= 300) {
                rd = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            } else {
                rd = BufferedReader(InputStreamReader(conn.errorStream, "UTF-8"))
            }

            var sb= StringBuffer()

            var line: String? = null;
            while ({ line = rd.readLine(); line }() != null) {
                sb.append(line)
            }

            rd.close()
            conn.disconnect()

            val regex = "<h2 class=\"title\">.+?</span>".toRegex()
            val matchResult = regex.findAll(sb!!)
            var i = 0;

            var a = WeatherVar.getDate()
            var b = WeatherVar.getTime()
            var c = "A"

            matchResult.map {
                it.groupValues[0]
            }.forEach {
                if(it.indexOf(WeatherVar.getDate()) >= 1 ) {
                    val newTitle:String? = it.replace(".+?rel=\"bookmark\"\\stitle=\"|\">.*".toRegex(),"").replace("&#[0-9]{4};".toRegex(),"\"")
                    val newLink:String? = it.replace("<h2.+?href=\"|\"\\srel.*".toRegex(),"")

                    newsData.add(i++,newTitle + "gubun"+ newLink)
                } else {
                    return newsData;
                }
            }
        }catch (e:Exception) {
            Log.d("errorWeather", e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return newsData;
    }
}

// http://www.yonhapnewstv.co.kr/category/news/weather/
