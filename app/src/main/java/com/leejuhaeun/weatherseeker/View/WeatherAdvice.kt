package com.leejuhaeun.weatherseeker.View

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.setThreadPolicy
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leejuhaeun.weatherseeker.R
import com.leejuhaeun.weatherseeker.WeatherAdviceController.YonhapWeatherNews
import com.leejuhaeun.weatherseeker.WeatherAdviceService.ClothesData
import com.leejuhaeun.weatherseeker.WeatherAdviceService.NewsData
import com.leejuhaeun.weatherseeker.WeatherAdviceService.PropData
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.DetailDust
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.LocalWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.ShortWeather
import com.leejuhaeun.weatherseeker.WeatherApi.WeatherController.TodayAdvice
import kotlinx.android.synthetic.main.activity_advice.*
import kotlinx.android.synthetic.main.activity_advice.btn_detail
import kotlinx.android.synthetic.main.activity_advice.btn_localSearch
import kotlinx.android.synthetic.main.activity_advice.btn_temp
import kotlinx.android.synthetic.main.activity_advice.btn_advice
import kotlinx.android.synthetic.main.activity_advice.loading
import kotlinx.android.synthetic.main.activity_temp_page.*
import kotlinx.android.synthetic.main.item_advice_clothes.view.*
import kotlinx.android.synthetic.main.item_advice_news.view.*
import kotlinx.android.synthetic.main.item_advice_prop.view.*


class WeatherAdvice : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice)

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

        // 오늘의조언

        //TodayAdvice().getInfo().toString().replace("", "")

        tv_advice.text = TodayAdvice().getInfo().toString()

        getNewsFunc()
        getRecommandFunc()

        btn_localSearch.setOnClickListener {
            MoveLocalSearch()
        }

        btn_detail.setOnClickListener {
            MoveDetailWeather()
        }

        btn_temp.setOnClickListener {
            MoveTemp()
        }

        // 뉴스링크 클릭
        rv_news.addOnItemClickListener(object: OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val newsLink = newsDataItem[position].link

                if(!newsLink.equals("")) {
                    val uri = Uri.parse(newsLink)
                    val it = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(it)
                }
            }
        })

    }

    fun getRecommandFunc() {
        clothesItems.clear()
        propItems.clear()

        try {
            val rain = getData("강수형태").toInt() // 0과 1만있음
            val dustState = getData("미세먼지상태").toInt()
            val tempState = getData("기온").toFloat()

            if (tempState >= 24) {
                clothesItems.add(0, ClothesData("모자"))
                clothesItems.add(0, ClothesData("반팔"))
                propItems.add(0, PropData("부채"))
            } else if (tempState >= 15) {
                clothesItems.add(0, ClothesData("가벼운 겉옷"))
                clothesItems.add(0, ClothesData("셔츠"))
            }else  if (tempState >= 3) {
                clothesItems.add(0, ClothesData("긴옷"))
                clothesItems.add(0, ClothesData("바람막이"))
            } else {
                clothesItems.add(0, ClothesData("패딩"))
                propItems.add(0, PropData("장갑"))
                propItems.add(0, PropData("귀마개"))
            }

            if (rain == 1) {
                propItems.add(0, PropData("우산"))
                clothesItems.add(0, ClothesData("장화"))
            }

            if (dustState >= 3) {
                propItems.add(0, PropData("물"))
                propItems.add(0, PropData("마스크"))
            }

            if(clothesItems.size == 0) {
                clothesItems.add(0, ClothesData("없음"))
            }

            if(propItems.size == 0) {
                propItems.add(0, PropData("없음"))
            }
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
        }

        rv_clothes.adapter = RecommandClothes()
        rv_clothes.layoutManager = LinearLayoutManager(this)

        rv_prop.adapter = RecommandProp()
        rv_prop.layoutManager = LinearLayoutManager(this)
    }

    fun getNewsFunc() {
        var getWeatherNews = YonhapWeatherNews().getWeatherNews();

        newsDataItem.clear()

        if(getWeatherNews.count() > 0) {
            getWeatherNews.reverse()

            for (newsDataStr in getWeatherNews) {
                val newsDataSplit = newsDataStr.split("gubun")
                newsDataItem.add(0, NewsData(newsDataSplit[0],newsDataSplit[1]))
            }
        } else {
            newsDataItem.add(0, NewsData("오늘의 뉴스가 없습니다.", ""))
        }

        rv_news.adapter = RecentNews()
        rv_news.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK || resultCode == WeatherVar.getOriginIntent()) {
            if(requestCode == WeatherVar.getLocalWeatherIntent() || requestCode== WeatherVar.getAdviceIntent()) {
                try {
                    localWeather()
                    shortWeather()
                    detailDust()
                    getRecommandFunc()
                } catch(e: Exception) {
                    Log.d("JHO", e.message)
                }
            }
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

    fun getData(key:String): String {
        val prefs = getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        val text = prefs.getString(key, "")

        return text
    }

    fun shortWeather() {
        try {
            val vShortWeather = ShortWeather().getWeatherInfo(WeatherVar.getDate(), WeatherVar.getTime(), WeatherVar.getNX(), WeatherVar.getNY())

            //기온, 풍속, 강수량
            setData("기온", vShortWeather.get("기온").toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun detailDust() {
        try {
            val vDetailDust = DetailDust().getDustInfo(WeatherVar.getSido(), WeatherVar.getGu());

            if(vDetailDust.get("pm25Value").equals("지역미지원")) {
                setData("미세먼지상태", "0")
            } else {
                if(vDetailDust.get("pm25Value").equals("-")) {
                    setData("미세먼지상태", "0")
                } else {
                    if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 76) {
                        setData("미세먼지상태", "4")
                    } else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 36) {
                        setData("미세먼지상태", "3")
                    } else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 16) {
                        setData("미세먼지상태", "2")
                    } else if (Integer.parseInt(vDetailDust.get("pm25Value")) >= 0) {
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

            if(vLocalWeather.get("강수형태").equals("0")) {
                setData("강수형태", "0")
            } else if(vLocalWeather.get("강수형태").equals("1")) {
                setData("강수형태", "1")
            } else if(vLocalWeather.get("강수형태").equals("2")) {
                setData("강수형태", "1")
            } else if(vLocalWeather.get("강수형태").equals("3")) {
                setData("강수형태", "1")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setData(key:String, value:String) {
        val prefs = getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString(key, value)
        editor.commit()
    }
}

private var propItems: MutableList<PropData> = arrayListOf()

class RecommandProp : RecyclerView.Adapter<RecommandProp.MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)
    override fun getItemCount(): Int = propItems.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        propItems[position].let { item ->
            with(holder) {
                tvContent.text = item.title
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_advice_prop, parent, false)) {
        val tvContent = itemView.tv_prop
    }
}

private var clothesItems: MutableList<ClothesData> = arrayListOf()

class RecommandClothes : RecyclerView.Adapter<RecommandClothes.MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)
    override fun getItemCount(): Int = clothesItems.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        clothesItems[position].let { item ->
            with(holder) {
                tvContent.text = item.title
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_advice_clothes, parent, false)) {
        val tvContent = itemView.tv_clothes
    }
}

private var newsDataItem: MutableList<NewsData> = mutableListOf()

class RecentNews : RecyclerView.Adapter<RecentNews.MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)
    override fun getItemCount(): Int = newsDataItem.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        newsDataItem[position].let { item ->
            with(holer) {
                tvContent.text = item.title
            }
        }
    }

    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_advice_news, parent, false)) {
        val tvContent = itemView.tv_news
    }
}

interface OnItemClickListener {
    fun onItemClicked(position: Int, view: View)
}

fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
    this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View?) {
            view?.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(view: View?) {
            view?.setOnClickListener({
                val holder = getChildViewHolder(view)
                onClickListener.onItemClicked(holder.adapterPosition, view)
            })
        }
    })
}