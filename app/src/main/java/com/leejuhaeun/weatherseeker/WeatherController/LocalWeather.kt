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

class LocalWeather : WeatherServiceImpl {

    @Throws(IOException::class)
    override fun getWeatherInfo(date: String, time: String, nx: String, ny: String): HashMap<String, String> {
        val startTime = System.currentTimeMillis()

        var nTime: Int = Integer.parseInt(time)
        var convertTime:String

        // Base_time  : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
        var convretDate = date

        if(nTime >= 2310 )               convertTime = "2300"
        else if(nTime >= 2010)          convertTime = "2000"
        else if(nTime >= 1710)          convertTime = "1700"
        else if(nTime >= 1410)          convertTime = "1400"
        else if(nTime >= 1110)          convertTime = "1100"
        else if(nTime >= 810)          convertTime = "800"
        else if(nTime >= 510)          convertTime = "500"
        else if(nTime >= 210)          convertTime = "200"
        else {
            val c1 = GregorianCalendar()
            c1.add(Calendar.DATE, -1) // 오늘날짜로부터 -1
            val sdf = SimpleDateFormat("yyyyMMdd") // 날짜 포맷
            convretDate = sdf.format(c1.getTime()) // String으로 저장
            convertTime = "2300"
        }

        // 글자수 다른경우
        if(convertTime.length == 3) {
            convertTime = "0" + convertTime;
        } else if(convertTime.length == 2) {
            convertTime = "00" + convertTime;
        } else if(convertTime.length == 1) {
            convertTime = "000" + convertTime;
        }

        val weatherData = HashMap<String, String>()

        try {
            val urlBuilder =
                StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData") /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY())
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + convretDate)
            urlBuilder.append(
                "&" + URLEncoder.encode(
                    "base_time",
                    "UTF-8"
                ) + "=" + convertTime
            ) /*파일구분 -ODAM: 동네예보실황 -VSRT: 동네예보초단기 -SHRT: 동네예보단기*/
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + nx)
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + ny)
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

            // ??
            val tmn: String? = Regex(pattern = "<category>TMN</category>.+?</item>").find(input = sb!!)?.value
            weatherData["아침 최저기온"] = tmn!!.replace(".*<fcstValue>|</fcstValue>.*".toRegex(), "") //도

            val tmx: String? = Regex(pattern = "<category>TMX</category>.+?</item>").find(input = sb!!)?.value
            weatherData["낮 최고기온"] = tmx!!.replace(".*<fcstValue>|</fcstValue>.*".toRegex(), "")

            val sky: String? = Regex(pattern = "<category>SKY</category>.+?</item>").find(input = sb!!)?.value
            weatherData["하늘상태"] = sky!!.replace(".*<fcstValue>|</fcstValue>.*".toRegex(), "")

            val pty: String? = Regex(pattern = "<category>PTY</category>.+?</item>").find(input = sb!!)?.value
            weatherData["강수형태"] = pty!!.replace(".*<fcstValue>|</fcstValue>.*".toRegex(), "")

            weatherData["x"] = nx
            weatherData["y"] = ny
            weatherData["error"] = "-99"

            if (StringUtils.isNotBlank(weatherData["category"])) {
                weatherData["error"] = "0"
            }
        } catch (e:Exception) {
            Log.d("JHTEST",e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return weatherData
    }
}