package com.leejuhaeun.weatherseeker.WeatherAdviceImpl

import java.io.IOException

data class RecommandData(val title:String)
data class NewsData(val title:String, val link:String)

interface WeatherNews {
    @Throws(IOException::class)
    fun getWeatherNews():ArrayList<String>
}