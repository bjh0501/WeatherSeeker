package com.leejuhaeun.weatherseeker.WeatherAdvice

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.setThreadPolicy
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.leejuhaeun.weatherseeker.Main.TempPage
import com.leejuhaeun.weatherseeker.R
import com.leejuhaeun.weatherseeker.WeatherAdviceController.YonhapWeatherNews
import com.leejuhaeun.weatherseeker.WeatherAdviceImpl.NewsData
import com.leejuhaeun.weatherseeker.WeatherAdviceImpl.RecommandData
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherLocalSearch.WeatherLocalSearch
import kotlinx.android.synthetic.main.activity_advice.*
import kotlinx.android.synthetic.main.item_advice_clothes.view.*
import kotlinx.android.synthetic.main.item_advice_news.view.*

class WeatherAdvice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice)

        var getWeatherNews = YonhapWeatherNews().getWeatherNews();

        if(getWeatherNews.count() > 0) {
            getWeatherNews.reverse()
            newsDataItem.clear()

            for (newsDataStr in getWeatherNews) {
                val newsDataSplit = newsDataStr.split("gubun")
                newsDataItem.add(0, NewsData(newsDataSplit[0],newsDataSplit[1]))
            }
        } else {
            newsDataItem.clear()
            newsDataItem.add(0, NewsData("오늘의 뉴스가 없습니다.", ""))
        }

        rv_news.adapter = RecentNews()
        rv_news.layoutManager = LinearLayoutManager(this)

        rv_clothes.adapter = RecommandClothes()
        rv_clothes.layoutManager = LinearLayoutManager(this)

        btn_localSearch.setOnClickListener {
            val intent = Intent(applicationContext, WeatherLocalSearch::class.java)
            startActivityForResult(intent, WeatherVar.getLocalWeatherIntent())//액티비티 띄우기
        }

        home.setOnClickListener {
            home.isClickable = false
            btn_localSearch.isClickable = false

            loading.visibility = View.VISIBLE

            val intent = Intent(applicationContext, TempPage::class.java)
            intent.putExtra("state", "other")
            startActivity(intent);

            finish()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == WeatherVar.getLocalWeatherIntent()) {
                try {
                    // 위에있는것만 온도가져오기

                } catch(e: Exception) {
                    Log.d("JHO", e.message)
                }
            }
        }
    }
}

class RecommandClothes : RecyclerView.Adapter<RecommandClothes.MainViewHolder>() {
    var items: MutableList<RecommandData> = mutableListOf(RecommandData("반팔"), RecommandData("반바지"),RecommandData("우비"))
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MainViewHolder(parent)
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holer: MainViewHolder, position: Int) {
        items[position].let { item ->
            with(holer) {
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