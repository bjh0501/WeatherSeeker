package com.leejuhaeun.weatherseeker.WeatherAdviceController

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.leejuhaeun.weatherseeker.WeatherAdviceService.WeatherNews
import org.jsoup.Jsoup
import java.io.IOException

class YonhapWeatherNews: WeatherNews {
    @Throws(IOException::class)
    override fun getWeatherNews():ArrayList<String> {
        val startTime = System.currentTimeMillis()
        var arrList = ArrayList<String>()

        try {
            var url:String = "https://www.yonhapnewstv.co.kr/news/getNewsList?p=1&ct=9&srt=l&d=20191030"
            var str = "";
            try {
                str = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .ignoreContentType(true)
                    .execute().body();
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val parser = JsonParser()
            val jsonObj = parser.parse(str) as JsonObject
            val memberArray = jsonObj.get("list") as JsonArray

            for (i in 0 until memberArray.size()) {
                val innerObj = memberArray.get(i) as JsonObject
                val parser = JsonParser()
                val element = parser.parse(innerObj.toString())
                val title = element.getAsJsonObject().get("title").getAsString()
                val sequence = element.getAsJsonObject().get("sequence").getAsString()
                val link = "https://www.yonhapnewstv.co.kr/news/" + sequence + "?srt=l&d=Y"
                arrList.add(title + "gubun" + link)
            }
        }catch (e:Exception) {
            Log.d("errorWeather", e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return arrList
    }
}
class weatherVO {
    val title:String = "";
    val result:String = "";
    val link:String = "";
}
