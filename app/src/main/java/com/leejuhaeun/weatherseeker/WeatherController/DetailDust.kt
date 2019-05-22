package com.leejuhaeun.weatherseeker.WeatherApi.WeatherController

import android.support.v7.app.AppCompatActivity
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherService.DustServiceImpl

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*



class DetailDust : DustServiceImpl, AppCompatActivity() {
    @Throws(IOException::class)
    override fun getDustInfo(sido: String, gu: String): HashMap<String, String> {
        val startTime = System.currentTimeMillis()

        val weatherData = HashMap<String, String>()

//        이어도 해결해야함

        var convertSido:String?

        if(sido.equals("충청남도"))         convertSido ="충남"
        else if(sido.equals("충청북도"))    convertSido ="충북"
        else if(sido.equals("전라북도"))    convertSido ="전북"
        else if(sido.equals("전라남도"))    convertSido ="전남"
        else if(sido.equals("경상북도"))    convertSido ="경북"
        else if(sido.equals("경상남도"))    convertSido ="경남"
        else                                 convertSido = sido.substring(0,2)

        val urlBuilder =
            StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst") /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherVar.API_KEY()) /*서비스 인증*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "100")
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + "1")
        urlBuilder.append("&" + URLEncoder.encode("sidoName", "UTF-8") + "=" + convertSido)
        urlBuilder.append("&" + URLEncoder.encode("searchCondition", "UTF-8") + "=" + "HOUR")
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
        var line: String? = null;
        var check = false

        while ({ line = rd.readLine(); line }() != null) {
            if(line!!.indexOf(gu) >= 0 || check == true) {
                sb.append(line)
                check = true
            }

            if(line!!.indexOf("</item>") > 0 && check == true) {
                break
            }
        }

        rd.close()
        conn.disconnect()

        if(sb!!.indexOf(gu) > 0) {
            weatherData["pm10Value"] = sb!!.replace(".*<pm10Value>|</pm10Value>.*".toRegex(), "")
            weatherData["pm25Value"] = sb!!.replace(".*<pm25Value>|</pm25Value>.*".toRegex(), "")
        } else {
            weatherData["pm10Value"] = "지역미지원";
            weatherData["pm25Value"] = "지역미지원";
        }

        val endTime = System.currentTimeMillis()
        val spendTime = endTime - startTime

        return weatherData
    }
}