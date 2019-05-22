package com.leejuhaeun.weatherseeker.WeatherApi.WeatherService

import java.io.IOException
import java.util.HashMap

interface WeatherServiceImpl {
    @Throws(IOException::class)
    fun getWeatherInfo(date: String, time: String, nx: String, ny: String): HashMap<String, String>
}

interface DustServiceImpl {
    @Throws(IOException::class)
    fun getDustInfo(sido :String, gu:String ): HashMap<String, String>
}