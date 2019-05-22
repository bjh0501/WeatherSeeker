package com.leejuhaeun.weatherseeker.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.leejuhaeun.weatherseeker.WeatherAdvice.WeatherAdvice
import com.leejuhaeun.weatherseeker.WeatherLocalSearch.WeatherLocalSearch
import kotlinx.android.synthetic.main.activity_temp_page.*

import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.DetailDust
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.LocalWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.ShortWeather
import android.net.ConnectivityManager
import com.leejuhaeun.weatherseeker.R
import org.apache.commons.lang3.StringUtils


class TempPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_page)

        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ninfo = cm.activeNetworkInfo

        val intent = getIntent()

        // 처음 설정하는거라면

        val intentState: String? = intent.getStringExtra("state")

        // 데이터 켜짐, 스플래시 액티비티에서 가져오기
        if(ninfo != null) {
            if(intentState.equals("first")) {
                try {
                    tv_outside_local.setText(WeatherVar.getLocalLocation())

                    tv_outside_temp.setText(intent.getStringExtra("기온"));
                    tv_outside_windSpeed.setText(intent.getStringExtra("풍속"));
                    tv_outside_preci.setText(intent.getStringExtra("1시간 강수량"));
                    tv_outside_humidity.setText(intent.getStringExtra("습도"));
                    iv_outside_windDirection.setRotation(intent.getStringExtra("풍향").toFloat())
                    iv_outside_humidity_icon.setImageResource(Integer.parseInt(intent.getStringExtra("습도아이콘")))

                    tv_outside_maxTemp.setText(intent.getStringExtra("낮 최고기온"))
                    tv_outside_minTemp.setText(intent.getStringExtra("아침 최저기온"))
                    iv_outside_icon.setImageResource(Integer.parseInt(intent.getStringExtra("기상상태")))


                    tv_outside_dust.setText(intent.getStringExtra("미세먼지"))
                    iv_outside_dustIcon.setImageResource(Integer.parseInt(intent.getStringExtra("미세먼지 아이콘")))

                    toConvert()
                } catch (e: Exception) {
                    e.stackTrace
                    //java.lang.IllegalStateException: intent.getStringExtra("풍향") must not be null
                }
            } else if(intentState.equals("origin") || intentState.equals("other")) {
                tv_outside_local.setText(WeatherVar.getLocalLocation())
                localWeather()
                shortWeather()
                detailDust()

                toConvert()
            }
        } else {
      //      getSP()
        }

        btn_localSearch.setOnClickListener {
            btn_advice.isClickable = false
            btn_localSearch.isClickable = false

            val intent = Intent(applicationContext, WeatherLocalSearch::class.java)
            startActivityForResult(intent, WeatherVar.getLocalWeatherIntent())//액티비티 띄우기

            btn_advice.isClickable = true
            btn_localSearch.isClickable = true
        }

        btn_advice.setOnClickListener { v ->
            btn_advice.isClickable = false
            btn_localSearch.isClickable = false

            loading.visibility = View.VISIBLE

            val intent = Intent(applicationContext, WeatherAdvice::class.java)
            startActivity(intent)

           finish()
        }
    }
//    fun getSP() {
//        val pref = this.getPreferences(0)
//        val weatherSP = TempWeatherSharedPreferences(this@TempPage)
//
//        tv_outside_temp.setText(pref.getString(weatherSP.outside_temp, ""))
//    }
//
//    fun setSP() {
//        val pref = this.getPreferences(0)
//        val editor = pref.edit()
//        val weatherSP = TempWeatherSharedPreferences(this@TempPage)
//
//        editor.putString(weatㄹherSP.outside_temp, tv_outside_temp.text.toString()).apply()
//        editor.putString(weatherSP.outside_dust, tv_outside_dust.text.toString()).apply()
//        editor.putString(weatherSP.outside_windSpeed, tv_outside_windSpeed.text.toString()).apply()
//        editor.putString(weatherSP.outside_preci, tv_outside_preci.text.toString()).apply()
//        editor.putString(weatherSP.outside_minTemp, tv_outside_minTemp.text.toString()).apply()
//        editor.putString(weatherSP.outside_maxTemp, tv_outside_maxTemp.text.toString()).apply()
//        editor.putString(weatherSP.outside_humidity, tv_outside_humidity.text.toString()).apply()
//
//        // 날씨아이콘
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK || resultCode == WeatherVar.getOriginIntent()) {
            if(requestCode == WeatherVar.getLocalWeatherIntent() || requestCode== WeatherVar.getAdviceIntent()) {
                try {
                    tv_outside_local.setText(WeatherVar.getLocalLocation())
                    localWeather()
                    shortWeather()
                    detailDust()
                } catch(e: Exception) {
                    Log.d("JHO", e.message)
                }
            }
        }
    }

    // 확인하기
    fun shortWeather() {
        try {
            val vShortWeather = ShortWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getTime(), WeatherVar.getNX(), WeatherVar.getNY())

            //기온, 풍속, 강수량
            tv_outside_temp.setText(vShortWeather.get("기온") + "º");
            tv_outside_windSpeed.setText(vShortWeather.get("풍속") + "m/s");
            tv_outside_preci.setText(vShortWeather.get("1시간 강수량") + "mm");

            // 풍향
            val windDirection = if (vShortWeather.get("풍향") != null) vShortWeather.get("풍향") else "-1.0";
            iv_outside_windDirection.setRotation(windDirection.toString().toFloat());

            // 습도
            if(!vShortWeather.get("습도").equals("-998")) {
                tv_outside_humidity.setText(vShortWeather.get("습도") + "%");

                if (Integer.parseInt(vShortWeather.get("습도")) > 80) iv_outside_humidity_icon.setImageResource(R.drawable.water10)
                else if (Integer.parseInt(vShortWeather.get("습도")) > 40) iv_outside_humidity_icon.setImageResource(R.drawable.water8)
                else if (Integer.parseInt(vShortWeather.get("습도")) > 20) iv_outside_humidity_icon.setImageResource(R.drawable.water4)
                else if (Integer.parseInt(vShortWeather.get("습도")) > 0) iv_outside_humidity_icon.setImageResource(R.drawable.water2)
                else iv_outside_humidity_icon.setImageResource(R.drawable.water0)
            } else {
                tv_outside_humidity.setText("측정불가");
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun detailDust() {
        try {
            val vDetailDust = DetailDust().getDustInfo(WeatherVar.getSido(), WeatherVar.getGu());

            if(vDetailDust.get("pm25Value").equals("지역미지원")) {
                tv_outside_dust.setText("지역미지원");
                iv_outside_dustIcon.setImageResource(R.drawable.dustgood)
            } else {

                tv_outside_dust.setText(vDetailDust.get("pm25Value") + "/" + vDetailDust.get("pm10Value"));

                if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 76) iv_outside_dustIcon.setImageResource(R.drawable.dustverybad)
                else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 36) iv_outside_dustIcon.setImageResource(R.drawable.dustbad)
                else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 16) iv_outside_dustIcon.setImageResource(R.drawable.dustclean)
                else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 0) iv_outside_dustIcon.setImageResource(R.drawable.dustgood)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.d("AJH", e.message)
            //line must not be null
        }
    }

    fun localWeather() {
        try {
            val vLocalWeather = LocalWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getTime(), WeatherVar.getNX(), WeatherVar.getNY())

            //  setContentView(R.layout.activity_temp_page)
            tv_outside_maxTemp.setText(vLocalWeather.get("낮 최고기온") + "º");
            tv_outside_minTemp.setText(vLocalWeather.get("아침 최저기온") + "º");

            if(vLocalWeather.get("강수형태").equals("0")) {
                if(vLocalWeather.get("하늘상태").equals("1"))           iv_outside_icon.setImageResource(R.drawable.sunny)
                else if(vLocalWeather.get("하늘상태").equals("2"))          iv_outside_icon.setImageResource(R.drawable.littlecloud)
                else if(vLocalWeather.get("하늘상태").equals("3"))          iv_outside_icon.setImageResource(R.drawable.manycloud)
                else if(vLocalWeather.get("하늘상태").equals("4"))          iv_outside_icon.setImageResource(R.drawable.cloudy)
            } else if(vLocalWeather.get("강수형태").equals("1"))        iv_outside_icon.setImageResource(R.drawable.rain)
            else if(vLocalWeather.get("강수형태").equals("2"))          iv_outside_icon.setImageResource(R.drawable.rainsnow)
            else if(vLocalWeather.get("강수형태").equals("3"))          iv_outside_icon.setImageResource(R.drawable.snow)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun toConvert() {
        if(tv_outside_temp.text.contains("-998")) {
            tv_outside_temp.setText("측정불가")
        }

        if(tv_outside_preci.text.contains("-998")) {
            tv_outside_preci.setText("측정불가")
        }

        if(tv_outside_windSpeed.text.contains("-998")) {
            tv_outside_windSpeed.setText("측정불가")
        }

        if(tv_outside_humidity.text.contains("-998")) {
            tv_outside_humidity.setText("측정불가")
        }
    }
}
