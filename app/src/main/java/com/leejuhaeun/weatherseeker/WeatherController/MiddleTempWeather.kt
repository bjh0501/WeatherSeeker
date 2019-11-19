package com.leejuhaeun.weatherseeker.WeatherApi.WeatherController

import android.util.Log
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherService.MiddleWeatherServiceImpl

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class MiddleTempWeather : MiddleWeatherServiceImpl {

    @Throws(IOException::class)
    override fun getWeatherInfo(date: String, time: String, n: String): HashMap<String, String> {
        val startTime = System.currentTimeMillis()

        val weatherData = HashMap<String, String>()

        var convertTime = "0000"
        var convertDate = date

        if(time.toInt() >= 1800) {
            convertTime = "1800" // 당일 18시
        } else if(time.toInt() <= 600){
            convertTime = "1800" // 전날 18시

            val c1 = GregorianCalendar()
            c1.add(Calendar.DATE, -1) // 오늘날짜로부터 -1
            val sdf = SimpleDateFormat("yyyyMMdd") // 날짜 포맷
            convertDate = sdf.format(c1.getTime()) // String으로 저장
        } else {
            convertTime = "0600" // 당일 6시
        }

        try {
            // ForecastGrib도 써야함
            val urlBuilder =
                StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature") /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY())
            urlBuilder.append("&" + URLEncoder.encode("tmFc", "UTF-8") + "=" + (convertDate+convertTime))
            urlBuilder.append("&" + URLEncoder.encode("regId", "UTF-8") + "=" + n)
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "10")
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + "1")
            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + "xml")

            val url = URL(urlBuilder.toString())
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Content-type", "application/json") // Accept-Charset 설정.

            val rd: BufferedReader

            if (conn.responseCode >= 200 && conn.responseCode <= 300) {
                rd = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            } else {
                rd = BufferedReader(InputStreamReader(conn.errorStream, "UTF-8"))
            }

            var sb= StringBuffer()
            var line: String?=""

            do {
                line = rd.readLine()
                if (line == null)
                    break
                sb.append(line)
            } while (true)

            rd.close()
            conn.disconnect()

            for(i in 3..10) {
                val convertMaxRegex = ".*<taMax${i}>|</taMax${i}>.*".toRegex()
                val convertMinRegex = ".*<taMin${i}>|</taMin${i}>.*".toRegex()

                weatherData["taMax" + i.toString()] = sb.toString().replace(convertMaxRegex, "").trim()
                weatherData["taMin" + i.toString()] = sb.toString().replace(convertMinRegex, "").trim()
            }
        } catch (e:Exception) {
            Log.d("JHTEST",e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return weatherData
    }


}