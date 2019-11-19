package com.leejuhaeun.weatherseeker.WeatherApi.Glboal

import android.content.Context
import android.support.v7.app.AppCompatActivity
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
    private var nx = ""
    private var ny = ""
    private var n = ""
    private var sido = ""
    private var gu = ""
    private var dong = ""
    private var location = ""

    fun getLocation():String{
        return location
    }

    fun setLocation(location:String) {
        this.location = location
    }

    fun getN():String{
        return n
    }

    fun setN(n:String) {
        this.n = n
    }


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
        return System.currentTimeMillis();
    }
<<<<<<< HEAD

}
=======
}
>>>>>>> master
