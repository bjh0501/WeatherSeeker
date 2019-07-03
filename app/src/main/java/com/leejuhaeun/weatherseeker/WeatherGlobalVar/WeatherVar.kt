package com.leejuhaeun.weatherseeker.WeatherApi.Glboal

import android.util.Log
import org.apache.commons.lang3.StringUtils
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*
import org.apache.commons.net.ntp.NTPUDPClient

object WeatherVar {
    private val api_key =
        "APIKEY"

    private val adviceIntent = 1001
    private val localWeatherIntent = 3000
    private val originIntent = 2000

    private val weatherCategory = HashMap<String, String>()
    private var nx = "77"
    private var ny = "77"
    private var sido = "서울특별시"
    private var gu = "강서구"
    private var dong = "방화제1동"



    fun getAdviceIntent():Int {
        return adviceIntent
    }

    fun getLocalLocation():String {
        return (WeatherVar.getSido() + " " + WeatherVar.getGu() + " " + WeatherVar.getDong()).trim()
    }

    fun getDong():String{
        return dong
    }

    fun setDong(dong:String) {
        this.dong = dong
    }

    fun getSido():String{
        return sido
    }

    fun setSido(sido:String) {
        this.sido = sido
    }

    fun getGu():String{
        return gu
    }

    fun setGu(gu:String){
        this.gu = gu
    }

    fun getOriginIntent():Int {
        return originIntent
    }

    fun getLocalWeatherIntent():Int {
        return localWeatherIntent
    }

    fun setNX(nx:String) {
        this.nx = nx;
    }

    fun setNY(ny:String) {
        this.ny = ny;
    }

    fun getNX():String {
        return nx
    }

    fun getNY():String {
        return ny
    }

    fun API_KEY(): String {
        return api_key
    }

    //추후 추가할거있으면 여기에 추가하기
    init {
        weatherCategory["PTY"] = "강수형태" //코드
        weatherCategory["REH"] = "습도" // %
        weatherCategory["T1H"] = "기온" // 도
        weatherCategory["VEC"] = "풍향" // m/s
        weatherCategory["WSD"] = "풍속" // 1
        weatherCategory["RN1"] = "1시간 강수량" // mm
        weatherCategory["TMN"] = "아침 최저기온" // º
        weatherCategory["TMX"] = "낮 최고기온" // º
    }

    fun CATEGORY_NAME(categoryVal: String?): String? {
        return if (StringUtils.isNotBlank(weatherCategory[categoryVal])) weatherCategory[categoryVal] else ""
    }

    fun CATEGORY_VALUE(categoryName: String, categoryVal: String): String {
        var categoryVal = categoryVal
        val sCategoryName:String? = CATEGORY_NAME(categoryName)

        if (sCategoryName == "강수형태") {
            if (categoryVal == "0")
                categoryVal = "없음"
            else if (categoryVal == "1")
                categoryVal = "비"
            else if (categoryVal == "2")
                categoryVal = "비/눈" // 진눈개비
            else if (categoryVal == "3") categoryVal = "눈"
        }

        return categoryVal
    }

    fun getDate():String {
        val date = SimpleDateFormat("yyyyMMdd");
        Log.d("TIMEHHMM", date.format(getCurrentNetworkTime()))
        return date.format(getCurrentNetworkTime())
    }

    fun getTime():String {
        val time = SimpleDateFormat("HHmm");
        Log.d("TIMEHHMM", time.format(getCurrentNetworkTime()))
        return time.format(getCurrentNetworkTime())
    }

    fun getCurrentNetworkTime(): Long {
        val TIME_SERVER = "kr.pool.ntp.org"
        val lNTPUDPClient = NTPUDPClient()
        lNTPUDPClient.setDefaultTimeout(3000)
        var returnTime: Long = 0

        try {
            lNTPUDPClient.open()
            val lInetAddress = InetAddress.getByName(TIME_SERVER)
            val lTimeInfo = lNTPUDPClient.getTime(lInetAddress)
             returnTime =  lTimeInfo.getReturnTime(); // local time
            //returnTime = lTimeInfo.getMessage().getTransmitTimeStamp().getTime()   //server time
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            lNTPUDPClient.close()
        }

        return returnTime
    }
}
