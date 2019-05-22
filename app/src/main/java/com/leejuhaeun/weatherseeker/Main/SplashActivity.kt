package com.leejuhaeun.weatherseeker.Main

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.setThreadPolicy
import android.support.v7.app.AppCompatActivity
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.DetailDust
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.LocalWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.ShortWeather
import com.leejuhaeun.weatherseeker.R
import com.leejuhaeun.weatherseeker.WeatherLocalSearch.WeatherLocalSearch
import com.leejuhaeun.weatherseeker.WeatherRepository.DatabaseHandler
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import java.lang.Exception
import android.os.Looper


/**
 * 테스트 1 : 메인페이지까지 실행한다. > 종료한 후 다시 켜본다.
 * 테스트 2 : 데이터를 불러오는 도중에 종료 한 후 다시 켜본다. > 종료한 후 다시 켜본다.
 * 테스트 3 : 초기 지역을 선택하지 않고 종료한후 다시켜본다.
 */
class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long=50
    private val LOCAL_DATA_CNT:Int=3780

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 데이터 가능여부
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ninfo = cm.activeNetworkInfo

        if(ninfo != null) {
            val handler = Handler()



            handler.postDelayed({
                val intent = Intent(this@SplashScreenActivity, TempPage::class.java)

                var dbHandler: DatabaseHandler? = null
                dbHandler = DatabaseHandler(this@SplashScreenActivity)
                var progressBar = ProgressDialog(this@SplashScreenActivity)
                var progressBarStatus = 0
                val progressBarHandler = Handler()

                //프로그레스바
                progressBar.setCancelable(false)
                progressBar.setMessage("지역정보를 불러오고있습니다.\n어플을 최초 실행할 때만 불러옵니다.")
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                progressBar.max = LOCAL_DATA_CNT // 전체 날짜 데이터 수
                progressBar.progress = 0

                val localDataRows = dbHandler!!.getRows()

                if (localDataRows != LOCAL_DATA_CNT) {
                    progressBar.show()
                }

                Thread(Runnable {
                    try {
                        // 시가 입력된경우

                        Looper.prepare(); // 스레드안에서 스레드돌리기
                        kotlin.run {

                            if(StringUtils.isNotBlank(getData("Sido"))) {
                                WeatherVar.setSido(getData("Sido"))
                                WeatherVar.setGu(getData("Gu"))
                                WeatherVar.setDong(getData("Dong"))
                                WeatherVar.setNX(getData("NX"))
                                WeatherVar.setNY(getData("NY"))

                                var detailDust:HashMap<String, String> = detailDust()
                                var shortWeather:HashMap<String, String> = shortWeather()
                                var localWeather:HashMap<String, String>? = localWeather()

                                intent.putExtra("기온", shortWeather?.get("기온"))
                                intent.putExtra("풍속", shortWeather?.get("풍속"))
                                intent.putExtra("1시간 강수량", shortWeather?.get("1시간 강수량"))
                                intent.putExtra("습도", shortWeather?.get("습도"))
                                intent.putExtra("풍향", shortWeather?.get("풍향"))
                                intent.putExtra("습도아이콘", shortWeather?.get("습도아이콘"))

                                intent.putExtra("낮 최고기온", localWeather?.get("낮 최고기온"))
                                intent.putExtra("아침 최저기온", localWeather?.get("아침 최저기온"))
                                intent.putExtra("기상상태", localWeather?.get("기상상태"))

                                intent.putExtra("미세먼지", detailDust?.get("미세먼지"))
                                intent.putExtra("미세먼지 아이콘", detailDust?.get("미세먼지 아이콘"))
                                intent.putExtra("state", "first")
                                startActivity(intent)

                                finish()
                            } else if (localDataRows != LOCAL_DATA_CNT) {
                                val `is` = assets.open("area.json")
                                val size = `is`.available()
                                val buffer = ByteArray(size)

                                `is`.read(buffer)
                                `is`.close()

                                val areaData = String(buffer)
                                val jarray = JSONArray(areaData) // JSONArray 생성

                                for (i in localDataRows until jarray.length()) {
                                    val jObject = jarray.getJSONObject(i) // JSONObject 추출
                                    val firstStep = jObject.getString("1단계")
                                    val secondStep = jObject.getString("2단계")
                                    val thirdStep: String = jObject.getString("3단계")
                                    val nx: String = jObject.getString("격자 X")
                                    val ny: String = jObject.getString("격자 Y")

                                    val success = dbHandler!!.initLocalData(firstStep, secondStep, thirdStep, nx, ny)

                                    progressBarStatus = (i + 1)

                                    if (success) {
                                        progressBarHandler.post { progressBar.progress = progressBarStatus }
                                    }
                                }

                                progressBar.dismiss()

                                if(StringUtils.isBlank(getData("Sido"))) {
                                    val intent2 = Intent(this@SplashScreenActivity, WeatherLocalSearch::class.java)
                                    intent2.putExtra("state", "origin")
                                    startActivity(intent2)

                                    finish()
                                }
                            } else {
                                // 테스트3경우

                                if(StringUtils.isBlank(getData("Sido"))) {
                                    val intent2 = Intent(this@SplashScreenActivity, WeatherLocalSearch::class.java)
                                    intent2.putExtra("state", "origin")
                                    startActivity(intent2)

                                    finish()
                                }
                            }
                        }
                        Looper.loop();

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }).start()
            }, SPLASH_TIME_OUT)
        } else {
            val intent = Intent(applicationContext, TempPage::class.java)
            startActivity(intent)

            finish()
        }
    }

    fun detailDust():HashMap<String, String> {
        val hashData = HashMap<String, String>()

        try {
            val vDetailDust = DetailDust().getDustInfo(WeatherVar.getSido(), WeatherVar.getGu())

            if(vDetailDust.get("pm25Value").equals("지역미지원")) {
                hashData.put("미세먼지", "지역미지원");
                hashData.put("미세먼지 아이콘", R.drawable.dustgood.toString());
            } else {
                hashData.put("미세먼지", vDetailDust.get("pm25Value") + "/" + vDetailDust.get("pm10Value"));

                val dustIcon = (if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 76) R.drawable.dustverybad
                else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 36) R.drawable.dustbad
                else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 16) R.drawable.dustclean
                else R.drawable.dustgood)

                hashData.put("미세먼지 아이콘", dustIcon.toString());
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //java.lang.IllegalStateException: line must not be null 예외라면 그지역에 미세먼지정보없음

        }

        return hashData
    }

    fun setData(key:String, value:String) {
        val prefs = getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(key, value)
        editor.commit()
    }

    fun getData(key:String): String {
        val prefs = getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        val text = prefs.getString(key, "")

        return text
    }

    fun shortWeather():HashMap<String, String> {
        var hashData = HashMap<String, String>()

        try {
            val vShortWeather = ShortWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getTime(), WeatherVar.getNX(), WeatherVar.getNY())
            val windDirection = if (vShortWeather.get("풍향") != null) vShortWeather.get("풍향") else "-1.0";

            val humidityIcon =
                if(Integer.parseInt(vShortWeather.get("습도")) > 80)          R.drawable.water10
                else if(Integer.parseInt(vShortWeather.get("습도")) > 40)    R.drawable.water8
                else if(Integer.parseInt(vShortWeather.get("습도")) > 20)    R.drawable.water4
                else if(Integer.parseInt(vShortWeather.get("습도")) > 0)    R.drawable.water2
                else                                                        R.drawable.water0

            //기온, 풍속, 강수량
            hashData.put("기온", vShortWeather.get("기온") + "º")
            hashData.put("풍속", vShortWeather.get("풍속") + "m/s")
            hashData.put("1시간 강수량", vShortWeather.get("1시간 강수량")+ "mm")

            if(vShortWeather.get("습도").equals("-998")) {
                hashData.put("습도", "측정불가")
            } else {
                hashData.put("습도", vShortWeather.get("습도") + "%")
            }

            hashData.put("풍향", windDirection.toString())
            hashData.put("습도아이콘", humidityIcon.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return hashData
    }

    fun localWeather():HashMap<String, String> {
        var hashData = HashMap<String, String>()

        try {
            val vLocalWeather = LocalWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getTime(), WeatherVar.getNX(), WeatherVar.getNY())

            hashData.put("낮 최고기온", vLocalWeather.get("낮 최고기온") + "º")
            hashData.put("아침 최저기온", vLocalWeather.get("아침 최저기온") + "º")

            val weatherState = (if(vLocalWeather.get("강수형태").equals("0")) {
                if(vLocalWeather.get("하늘상태").equals("1"))           R.drawable.sunny
                else if(vLocalWeather.get("하늘상태").equals("2"))          R.drawable.littlecloud
                else if(vLocalWeather.get("하늘상태").equals("3"))          R.drawable.manycloud
                else                                                                R.drawable.cloudy
            } else if(vLocalWeather.get("강수형태").equals("1"))        R.drawable.rain
            else if(vLocalWeather.get("강수형태").equals("2"))          R.drawable.rainsnow
            else                                                                R.drawable.snow)

            hashData.put("기상상태", weatherState.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return hashData
    }
}