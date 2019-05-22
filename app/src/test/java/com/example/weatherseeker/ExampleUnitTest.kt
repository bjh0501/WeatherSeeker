package com.leejuhaeun.weatherseeker

import android.util.Log
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class leejuhaeunUnitTest {
    @Test
    fun test() {

        print("AAA : " + WeatherVar.getDate())
        print(WeatherVar.getTime())

    }
}
