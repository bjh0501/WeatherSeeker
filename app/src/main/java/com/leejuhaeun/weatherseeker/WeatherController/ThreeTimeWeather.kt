package com.leejuhaeun.weatherseeker.WeatherApi.WeatherController

import android.util.Log
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherEntitiy.CategoryData

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ThreeTimeWeather  {
    @Throws(IOException::class)
    fun getWeatherInfo(date: String, time: String, nx: String, ny: String): ArrayList<CategoryData> {
        val startTime = System.currentTimeMillis()

        val weatherData = ArrayList<CategoryData>()
        var nTime: Int = Integer.parseInt(time)
        var convertTime:String = time

        // Base_time  : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
        var convertDate = date

        // 변환 안해도 값은나오는데 해야 모든 값이 다나옴, 즉 무조건해야함
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
            c1.add(Calendar.DATE,-1) // 0으로해야 오늘날짜로부터 -1
            val sdf = SimpleDateFormat("yyyyMMdd") // 날짜 포맷
            convertDate = sdf.format(c1.getTime()) // String으로 저장
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

        try {
            val urlBuilder =
                StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData") /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY())
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + convertDate)
            urlBuilder.append(
                "&" + URLEncoder.encode(
                    "base_time",
                    "UTF-8"
                ) + "=" + convertTime
            ) /*파일구분 -ODAM: 동네예보실황 -VSRT: 동네예보초단기 -SHRT: 동네예보단기*/
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + nx)
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + ny)
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "300")
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

            val dataSplit = sb.split("<item>")
            var cd = CategoryData()

            val startTime = System.currentTimeMillis()

            for(args in dataSplit) {
                if(args.indexOf("POP") > 0) { // 강수확률
                    cd.pop = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                } else if(args.indexOf("REH") > 0) { // 습도
                    cd.reh = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                } else if(args.indexOf("T3H") > 0) { // 온도
                    cd.t3h = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                } else if(args.indexOf("TMN") > 0) { // 최저
                    cd.tmn = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                } else if(args.indexOf("TMX") > 0) { // 최고
                    cd.tmx = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                } else if(args.indexOf("VEC") > 0) { // 풍향
                    cd.vec = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                } else if(args.indexOf("WSD") > 0) { // 풍속
                    cd.wsd = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                    cd.date = args.replace(".*<fcstDate>|</fcstDate>.*".toRegex(),"")
                    cd.time = args.replace(".*<fcstTime>|</fcstTime>.*".toRegex(),"")
                    weatherData.add(cd)
                    cd = CategoryData()
                } else if(args.indexOf("SKY") > 0) { // 하늘상태
                    cd.sky = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                }else if(args.indexOf("PTY") > 0) { // 강수형태
                    cd.pty = args.replace(".*<fcstValue>|</fcstValue>.*".toRegex(),"")
                }
            }
            val endTime = System.currentTimeMillis()
            val spendTime = endTime - startTime


            Log.d("AA","AA")
        } catch (e:Exception) {
            Log.d("JHTEST",e.message)
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return weatherData
    }
}