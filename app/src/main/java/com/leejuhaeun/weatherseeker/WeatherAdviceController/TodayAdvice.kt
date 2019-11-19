package com.leejuhaeun.weatherseeker.WeatherApi.WeatherController

import android.util.Log
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherService.WeatherServiceImpl
import org.apache.commons.lang3.StringUtils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*



class TodayAdvice{

    @Throws(IOException::class)
    fun getInfo():String {
        val startTime = System.currentTimeMillis()
        var resultStr:String = ""

        try {
            val urlBuilder =
                StringBuilder("http://newsky2.kma.go.kr/service/WetherSpcnwsInfoService/SpecialNewsStatus") /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY())
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "100")
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

            val strOther:String =sb.replace(".*<other>|</other>.*".toRegex(), "")
            val strT6:String =sb.replace(".*<t6>|</t6>.*".toRegex(), "")
            val strT7:String =sb.replace(".*<t7>|</t7>.*".toRegex(), "")

            if(strOther.indexOf("없음") == -1) {
                resultStr += strOther + "\n"
            }

            if(strT6.indexOf("없음") == -1) {
                resultStr += strT6 + "\n"
            }

            if(strT7.indexOf("없음") == -1) {
                resultStr += strT7 + "\n"
            }

            resultStr = resultStr.replace("o|&#xD;".toRegex(),"\n").trim()
        } catch (e:Exception) {
            Log.d("JHTEST",e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return resultStr
    }
}