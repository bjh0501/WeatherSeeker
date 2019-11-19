package com.leejuhaeun.weatherseeker.WeatherApi.WeatherController

import android.util.Log
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherService.RiseWeatherService

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class RiseWeather : RiseWeatherService {

    @Throws(IOException::class)
    override fun getWeatherInfo(date: String, location: String): HashMap<String, String> {
        val startTime = System.currentTimeMillis()

        val weatherData = HashMap<String, String>()

        try {
            // ForecastGrib도 써야함
            val urlBuilder =
                StringBuilder("http://apis.data.go.kr/B090041/openapi/service/RiseSetInfoService/getAreaRiseSetInfo") /*URL*/
            // &locdate=20150101&location=%EC%84%9C%EC%9A%B8
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY())
            urlBuilder.append("&" + URLEncoder.encode("locdate", "UTF-8") + "=" + date)
            urlBuilder.append("&" + URLEncoder.encode("location", "UTF-8") + "=" + location)

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

            weatherData["일출"] = sb.replace(".*<sunrise>|</sunrise>.*".toRegex(), "")
            weatherData["일몰"] = sb.replace(".*<sunset>|</sunset>.*".toRegex(), "")
            weatherData["월출"] = sb.replace(".*<moonrise>|</moonrise>.*".toRegex(), "")
            weatherData["월몰"] = sb.replace(".*<moonset>|</moonset>.*".toRegex(), "")
        } catch (e:Exception) {
            Log.d("JHTEST",e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return weatherData
    }
}