package com.leejuhaeun.weatherseeker.WeatherApi.WeatherController

import com.leejuhaeun.weatherseeker.R
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherService.WeatherServiceImpl
import org.apache.commons.lang3.StringUtils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class ShortWeather : WeatherServiceImpl {

    @Throws(IOException::class)
    override fun getWeatherInfo(date: String, time: String, nx: String, ny: String): HashMap<String, String> {
        val startTime = System.currentTimeMillis()

        val weatherData = HashMap<String, String>()

        var nTime: Int = Integer.parseInt(time)
        var convertTime:String = time;

        // Base_time  : 60분
        var convretDate = date

        for(i in 2340 downTo 40 step 100){
            if(nTime >= i ) {
                convertTime = Integer.toString(i-40)
                break;
            }
        }

        if(nTime < 40) {
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

        val urlBuilder =
            StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib") /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY()) /*서비스 인증*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + convretDate) /*Service Key*/
        urlBuilder.append(
            "&" + URLEncoder.encode(
                "base_time",
                "UTF-8"
            ) + "=" + convertTime
        ) /*파일구분 -ODAM: 동네예보실황 -VSRT: 동네예보초단기 -SHRT: 동네예보단기*/
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + nx) /*각각의 base_time 로 검색 참고자료 참조*/
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

        val urlBuilderArray =
            sb!!.replace(".*<items>|</items>.*".toRegex(), "").split("<item>".toRegex()).toTypedArray()

        weatherData["x"] = nx
        weatherData["y"] = ny

        weatherData["error"] = "-99"

        var i = 0

        for (xmlSource in urlBuilderArray) {
            val category = xmlSource.replace(".*<category>|</category>.*".toRegex(), "")

            if (category.equals("PTY")) {
                weatherData["강수형태"] = xmlSource.replace(".*<obsrValue>|</obsrValue>.*".toRegex(), "")
                i++
            } else if (category.equals("REH")) {
                val data = xmlSource.replace(".*<obsrValue>|</obsrValue>.*".toRegex(), "")

                if(data.indexOf("-99") > 0) {
                    weatherData["습도"] = "측정불가"
                } else {
                    weatherData["습도"] = data
                }

                i++
            } else if (category.equals("T1H")) {
                weatherData["기온"] = xmlSource.replace(".*<obsrValue>|</obsrValue>.*".toRegex(), "") // 도
                i++
            } else if (category.equals("VEC")) {
                weatherData["풍향"] = xmlSource.replace(".*<obsrValue>|</obsrValue>.*".toRegex(), "") // m/s
                i++
            } else if (category.equals("WSD")) {
                weatherData["풍속"] = xmlSource.replace(".*<obsrValue>|</obsrValue>.*".toRegex(), "") // 1
                i++
            } else if (category.equals("RN1")) {
                weatherData["1시간 강수량"] = xmlSource.replace(".*<obsrValue>|</obsrValue>.*".toRegex(), "")// mm
                weatherData["baseDate"] = xmlSource.replace(".*<baseDate>|</baseDate>.*".toRegex(), "")
                weatherData["baseTime"] = xmlSource.replace(".*<baseTime>|</baseTime>.*".toRegex(), "")
                i++
            }

            if(i == 6) break;

            weatherData["category"] = category //에러체크용
        }

        if (StringUtils.isNotBlank(weatherData["category"])) {
            weatherData["error"] = "0"
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return weatherData
    }
}