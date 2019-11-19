package com.leejuhaeun.weatherseeker.View

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leejuhaeun.weatherseeker.R
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.MiddleTempWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.RiseWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.ThreeTimeWeather
import kotlinx.android.synthetic.main.activity_local_term_weather.*
import kotlinx.android.synthetic.main.activity_local_term_weather.btn_advice
import kotlinx.android.synthetic.main.activity_local_term_weather.btn_detail
import kotlinx.android.synthetic.main.activity_local_term_weather.btn_localSearch
import kotlinx.android.synthetic.main.activity_local_term_weather.btn_temp
import kotlinx.android.synthetic.main.activity_temp_page.loading
import kotlinx.android.synthetic.main.detail_weather_info.view.*
import kotlinx.android.synthetic.main.detail_weather_three.view.*
import kotlinx.android.synthetic.main.detail_weather_three.view.date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailWeather : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_term_weather)

        getThreeWeather()
        getWeekWeather()
        getRiseWeather()

        btn_localSearch.setOnClickListener {
            MoveLocalSearch()
        }

        btn_advice.setOnClickListener {
            MoveAdvidce()
        }

        btn_temp.setOnClickListener {
            MoveTemp()
        }
    }

    fun MoveLocalSearch() {
        btn_detail.isClickable = false
        btn_temp.isClickable = false
        btn_advice.isClickable = false
        btn_localSearch.isClickable = false

        val intent = Intent(applicationContext, WeatherLocalSearch::class.java)
        startActivityForResult(intent, WeatherVar.getLocalWeatherIntent())//액티비티 띄우기

        btn_detail.isClickable = true
        btn_temp.isClickable = true
        btn_advice.isClickable = true
        btn_localSearch.isClickable = true
    }

    fun MoveTemp() {
        loading.visibility = View.VISIBLE
        btn_detail.isClickable = false
        btn_temp.isClickable = false
        btn_advice.isClickable = false
        btn_localSearch.isClickable = false

        val intent = Intent(applicationContext, TempPage::class.java)
        intent.putExtra("state", "other")
        startActivity(intent);

        finish()
    }

    fun MoveAdvidce() {
        loading.visibility = View.VISIBLE

        btn_detail .isClickable = false
        btn_temp.isClickable = false
        btn_advice.isClickable = false
        btn_localSearch.isClickable = false

        val intent = Intent(applicationContext, WeatherAdvice::class.java)
        startActivity(intent)

        finish()
    }

    fun MoveDetailWeather() {
        loading.visibility = View.VISIBLE

        btn_detail .isClickable = false
        btn_temp.isClickable = false
        btn_advice.isClickable = false
        btn_localSearch.isClickable = false

        val intent = Intent(applicationContext, DetailWeather::class.java)
        startActivity(intent)

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK || resultCode == WeatherVar.getOriginIntent()) {
            if(requestCode == WeatherVar.getLocalWeatherIntent() || requestCode== WeatherVar.getAdviceIntent()) {
                try {
                    getThreeWeather()
                    getWeekWeather()
                    getRiseWeather()
                } catch(e: Exception) {
                    Log.d("JHO", e.message)
                }
            }
        }
    }

    fun getRiseWeather() {
        try {
            if(WeatherVar.getLocation().equals("NULL")) {
                tv_sunRise.text = "미지원지역"
                tv_sunDown.text = "미지원지역"
                tv_moonRise.text = "미지원지역"
                tv_moonDown.text = "미지원지역"
            } else {
                var data = RiseWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getLocation())
                val sunRise = data.get("일출").toString()
                val sunDown = data.get("일몰").toString()
                val moonRise = data.get("월출").toString()
                val moonDown = data.get("월몰").toString()

                if(sunRise.indexOf("-") >=0) {
                    tv_sunRise.text = "미지원"
                } else {
                    tv_sunRise.text = sunRise.substring(0, 2) + "시 " + sunRise.substring(2, 4) + "분"
                }

                if(sunDown.indexOf("-") >=0) {
                    tv_sunDown.text = "미지원"
                } else {
                    tv_sunDown.text = sunDown.substring(0, 2) + "시 " + sunDown.substring(2, 4) + "분"
                }

                if(moonRise.indexOf("-") >=0) {
                    tv_moonRise.text = "미지원"
                } else {
                    tv_moonRise.text = moonRise.substring(0, 2) + "시 " + moonRise.substring(2, 4) + "분"
                }

                if(moonDown.indexOf("-") >=0) {
                    tv_moonDown.text = "미지원"
                } else {
                    tv_moonDown.text = moonDown.substring(0, 2) + "시 " + moonDown.substring(2, 4) + "분"
                }
            }
        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getThreeWeather() {
        try {
            val ttw = ThreeTimeWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getTime(), WeatherVar.getNX(), WeatherVar.getNY())

            var threeWeatherDataList = arrayListOf<ThreeWeatherData>()

            for(args in ttw) {
                val convertDate = args.date.substring(4,6) + "월 " + args.date.substring(6,8) + "일"
                val convertTime = args.time.substring(0, 2) + "시"

                val data = ThreeWeatherData(
                    convertDate + " " + convertTime
                ,   args.t3h  + "º"
                ,   args.reh + "%"
                ,   args.sky
                ,   args.pty
                )

                threeWeatherDataList.add(data)
            }

            val layoutManager = LinearLayoutManager(this@DetailWeather)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            rv_3.layoutManager = layoutManager

            rv_3.adapter = RecyclerViewAdapter2(threeWeatherDataList)
        } catch(e:Exception) {
            e.message
        }
    }

    fun getWeekWeather() {
        try {
            val middleTempWeather = MiddleTempWeather().getWeatherInfo(
                WeatherVar.getDate(),
                WeatherVar.getTime(),
                WeatherVar.getN()
            )

            var MiddleWeatherDataList = arrayListOf<WeekWeatherData>()

            for(i in 3..10) {
                val tMax:String? = middleTempWeather.get("taMax" + i.toString())
                val tMin:String? = middleTempWeather.get("taMin" + i.toString())

                val c1 = GregorianCalendar()
                c1.add(Calendar.DATE, +i) // 오늘날짜로부터 -1
                val sdf = SimpleDateFormat("MM월 dd일") // 날짜 포맷

                val data = WeekWeatherData(
                    sdf.format(c1.getTime()),
                    tMax + "º",
                    tMin + "º",
                    "ss"
                );
                MiddleWeatherDataList.add(data)
            }

            val layoutManager = LinearLayoutManager(this@DetailWeather)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            rv_week.layoutManager = layoutManager

            rv_week.adapter = RecyclerViewAdapter(MiddleWeatherDataList)
        } catch(e:Exception) {
            e.message
        }
    }

    data class ThreeWeatherData(var date: String="", var temp:String="", var humi:String?="", var state:String?="", var rainState:String?="")

    class RecyclerViewAdapter2(val weatherDatas:ArrayList<ThreeWeatherData>): RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>() {

        //아이템의 갯수
        override fun getItemCount(): Int {
            return weatherDatas.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.detail_weather_three, parent, false)
            return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(weatherDatas[position])
        }

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            fun bindItems(data: ThreeWeatherData) {
                itemView.date.text = data.date
                itemView.temp.text = data.temp
                itemView.humi.text = data.humi

                var sState = "맑음"

                if (data.rainState.equals("0")) {
                    if (data.state.equals("1")) {
                        sState = "맑음"
                    } else if (data.state.equals("3")) {
                        sState = "구름많음"
                    } else if (data.state.equals("4")) {
                        sState = "흐림"
                    }
                } else if(data.rainState.equals("1")) {
                    sState = "비"
                } else if(data.rainState.equals("2")) {
                    sState = "진눈개비"
                } else if(data.rainState.equals("3")) {
                    sState = "눈"
                } else if(data.rainState.equals("4")) {
                    sState = "소나기"
                }

                itemView.state.text = sState
                //itemView.imageView_photo.setImageBitmap(data.photo)

            }
        }
    }

    data class WeekWeatherData(var date: String="", var max:String?="", var min:String?="", var state:String?="")

    class RecyclerViewAdapter(val weatherDatas:ArrayList<WeekWeatherData>):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        //아이템의 갯수
        override fun getItemCount(): Int {
            return weatherDatas.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.detail_weather_info, parent, false)
            return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(weatherDatas[position])

        }

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            fun bindItems(data : WeekWeatherData){

                itemView.date.text = data.date
                itemView.max.text = data.max
                itemView.min.text = data.min
                itemView.weekState.text = data.state
                //itemView.imageView_photo.setImageBitmap(data.photo)

            }
        }
    }

    fun getData(key:String): String {
        val prefs = getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        val text = prefs.getString(key, "")

        return text
    }
}