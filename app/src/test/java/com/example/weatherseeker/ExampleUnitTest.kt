package com.leejuhaeun.weatherseeker

import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class leejuhaeunUnitTest {
    @Test
    fun test() {
        var test = ArrayList<CategoryData>()
        for(i in 1.. 3) {
            var cd = CategoryData()
            cd.setCategory("A" + i)
            cd.setFcstDate("날짜")
            cd.setFcstTime("ㅅ간")
            cd.setFcstValue("값")

            test.add(cd)
        }

        println(test.get(2).getCategory())
        println(test.get(2).getFcstDate())
        println(test.get(2).getFcstTime())
        println(test.get(2).getFcstValue())


    }

    class CategoryData {
        private var category:String = ""
        private var fcstDate: String = ""
        private var fcstTime: String= ""
        private var fcstValue: String= ""

        fun getCategory():String {
            return category
        }

        fun getFcstDate() :String {
            return fcstDate
        }

        fun getFcstTime():String {
            return fcstTime
        }

        fun getFcstValue():String {
            return fcstValue
        }

        fun setCategory(category: String) {
            this.category = category
        }

        fun setFcstDate(fcstDate: String) {
            this.fcstDate = fcstDate
        }

        fun setFcstTime(fcstTime: String) {
            this.fcstTime = fcstTime
        }

        fun setFcstValue(fcstValue: String) {
            this.fcstValue = fcstValue
        }
    }
}
