package com.leejuhaeun.weatherseeker.View

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.*
import com.leejuhaeun.weatherseeker.R
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.DetailDust
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.LocalWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.ShortWeather
import kotlinx.android.synthetic.main.activity_advice.*
import kotlinx.android.synthetic.main.activity_advice.loading
import kotlinx.android.synthetic.main.activity_temp_page.*
import kotlinx.android.synthetic.main.activity_temp_page.btn_advice
import kotlinx.android.synthetic.main.activity_temp_page.btn_detail
import kotlinx.android.synthetic.main.activity_temp_page.btn_localSearch
import kotlinx.android.synthetic.main.activity_temp_page.btn_temp


class TempPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_page)

        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ninfo = cm.activeNetworkInfo

        if(ninfo == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("데이터나 와이파이를 켜주신 후\n다시 어플을 실행해 주세요.")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, id ->
                    finish()
                }
            val alert = builder.create()
            alert.show()
        }

        val intent = getIntent()

        // 처음 설정하는거라면
        val intentState: String? = intent.getStringExtra("state")

        // 데이터 켜짐, 스플래시 액티비티에서 가져오기
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
                e.message
                //java.lang.IllegalStateException: intent.getStringExtra("풍향") must not be null
            }
        } else if(intentState.equals("origin") || intentState.equals("other")) {
            tv_outside_local.setText(WeatherVar.getLocalLocation())
            localWeather()
            shortWeather()
            detailDust()

            toConvert()
        }

        getInnerData()
        getDry()
        getCold()

        btn_localSearch.setOnClickListener {
            MoveLocalSearch()
        }

        btn_advice.setOnClickListener {
            MoveAdvidce()
        }

        btn_detail.setOnClickListener {
            MoveDetailWeather()
        }

        Inside_tip.setOnClickListener {
            MoveAdvidce()
        }
    }

    fun getCold() {
        try {
            var calcTemp:Double = tv_outside_maxTemp.text.toString().replace("[^0-9|\\.]".toRegex(), "").toDouble() -
                    tv_outside_minTemp.text.toString().replace("[^0-9|\\.]".toRegex(), "").toDouble()

            var lowTemp:Double = tv_outside_minTemp.text.toString().replace("[^0-9|\\.]".toRegex(), "").toDouble()

            lowTemp = lowTemp / 100
            calcTemp = calcTemp / 100

            val calcResult:Double = lowTemp + calcTemp

            var FV = (lowTemp * 2) + (calcTemp * 2) * (+(calcResult* 2)) + 2
            var  REDATA = ""

            if( FV >= 3.1316 ) {
                REDATA = "매우높음";
            }else if( FV >= 2.6918 && FV < 3.1316 ) {
                REDATA = "높음";
            }else if( FV >= 1.4759 && FV < 2.6918 ) {
                REDATA = "보통";
            }else {
                REDATA = "낮음";
            }

            tv_cold.text = REDATA

            Log.d("ColdIndex", FV.toString())
        } catch (e:Exception) {
            e.message
        }
    }

    fun getDry() {
        try {
            val windSpeed = tv_outside_windSpeed.text.toString().replace("[^0-9|\\.]".toRegex(), "").toDouble()
            val highTemp = tv_outside_maxTemp.text.toString().replace("[^0-9|\\.]".toRegex(), "").toDouble()
            var dryVal = 80.664 - (0.056 * windSpeed) + (0.729 * highTemp)
            var str = ""

            if (dryVal >= 80) {
                str = "최적"
            } else if (dryVal >= 60) {
                str = "적당"
            } else if (dryVal >= 40) {
                str = "어려움"
            } else {
                str = "부적합"
            }


            tv_dry.text = str
        } catch (e:java.lang.Exception) {
            e.message
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


    fun setData(key:String, value:String) {
        val prefs = getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(key, value)
        editor.commit()
    }

    fun getInnerData() {
        getFirebaseData("Temp")
        getFirebaseData("Humidity")
        getFirebaseData("Dust")
        getFirebaseData("Discom")
    }

    fun getFirebaseData(str:String) {
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference(str)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println("Failed to read value.")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val value = dataSnapshot?.value
                    if (str.equals("Temp")) {
                        inside_Temp.text = value.toString() + "º"
                    } else if (str.equals("Humidity")) {
                        Inside_Humidity.text = value.toString() + "%"
                    } else if (str.equals("Dust")) {
                        Inside_DustText.text = value.toString()
                        val dustNum: Int = value.toString().toInt()

                        if (dustNum >= 151) iv_inside_dustIcon.setImageResource(R.drawable.dustverybad)
                        else if (dustNum >= 81) iv_inside_dustIcon.setImageResource(R.drawable.dustbad)
                        else if (dustNum >= 31) iv_inside_dustIcon.setImageResource(R.drawable.dustclean)
                        else iv_inside_dustIcon.setImageResource(R.drawable.dustgood)
                    } else if(str.equals("Discom")) {
                        if(value.toString().toInt() >= 80) {
                            Inside_Discom.text = "매우 높음"
                        } else if(value.toString().toInt() >= 75) {
                            Inside_Discom.text = "높음"
                        } else if(value.toString().toInt() >= 68) {
                            Inside_Discom.text = "보통"
                        }  else {
                            Inside_Discom.text = "낮음"
                        }

                        // (9/5*T - 0.55) * (1-RH)* (9/5*T-26) +32 // T 온도 RH습도
                    }
                } catch (e: java.lang.Exception) {
                    e.message
                }
            }
        })
    }

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
            setData("기온", vShortWeather.get("기온").toString())
            tv_outside_temp.setText(vShortWeather.get("기온") + "º");
            tv_outside_windSpeed.setText(vShortWeather.get("풍속") + "m/s");

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

            // 습도
            if(!vShortWeather.get("1시간 강수량").equals("-998")) {
                tv_outside_preci.setText(vShortWeather.get("1시간 강수량") + "mm");

                if (Integer.parseInt(vShortWeather.get("1시간 강수량")) > 10) Outside_WindAmountIcon.setImageResource(R.drawable.rainrecord4)
                else if (Integer.parseInt(vShortWeather.get("1시간 강수량")) > 4) Outside_WindAmountIcon.setImageResource(R.drawable.rainrecord3)
                else if (Integer.parseInt(vShortWeather.get("1시간 강수량")) > 1) Outside_WindAmountIcon.setImageResource(R.drawable.rainrecord2)
                else Outside_WindAmountIcon.setImageResource(R.drawable.rainrecord1)
            } else {
                tv_outside_preci.setText("측정불가");
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
                if(vDetailDust.get("pm25Value").equals("-")) {
                    tv_outside_dust.setText("정보없음");
                    iv_outside_dustIcon.setImageResource(R.drawable.dustgood)
                } else {
                    tv_outside_dust.setText(vDetailDust.get("pm25Value") + "/" + vDetailDust.get("pm10Value"));

                    if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 76) {
                        iv_outside_dustIcon.setImageResource(R.drawable.dustverybad)
                        setData("미세먼지상태", "4")
                    } else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 36) {
                        iv_outside_dustIcon.setImageResource(R.drawable.dustbad)
                        setData("미세먼지상태", "3")
                    } else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 16) {
                        iv_outside_dustIcon.setImageResource(R.drawable.dustclean)
                        setData("미세먼지상태", "2")
                    } else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 0) {
                        iv_outside_dustIcon.setImageResource(R.drawable.dustgood)
                        setData("미세먼지상태", "1")
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.d("AJH", e.message)
            tv_outside_dust.setText("정보없음");
            iv_outside_dustIcon.setImageResource(R.drawable.dustgood)
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

                setData("강수형태", "0")
            } else if(vLocalWeather.get("강수형태").equals("1")) {
                setData("강수형태", "1")
                iv_outside_icon.setImageResource(R.drawable.rain)
            } else if(vLocalWeather.get("강수형태").equals("2")) {
                setData("강수형태", "2")
                iv_outside_icon.setImageResource(R.drawable.rainsnow)
            } else if(vLocalWeather.get("강수형태").equals("3")) {
                setData("강수형태", "3")
                iv_outside_icon.setImageResource(R.drawable.snow)
            } else { // 소나기
                setData("강수형태", "2")
                iv_outside_icon.setImageResource(R.drawable.rain)
            }
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
