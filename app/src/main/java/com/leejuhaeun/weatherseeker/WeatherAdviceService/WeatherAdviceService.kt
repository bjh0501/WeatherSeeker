package com.leejuhaeun.weatherseeker.WeatherAdviceService

import java.io.IOException

data class PropData(val title:String)
data class ClothesData(val title:String)
data class NewsData(val title:String, val link:String)

interface WeatherNews {
    @Throws(IOException::class)
    fun getWeatherNews():ArrayList<String>
}