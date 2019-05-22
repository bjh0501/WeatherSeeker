package com.leejuhaeun.weatherseeker.Main

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.leejuhaeun.weatherseeker.R

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherLocalSearch.WeatherLocalSearch
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_temp_page.*

//class MainActivity : AppCompatActivity() {
//    private val localWeatherIntent = 3000
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//
//    class CustomPagerAdapter(private val mContext: Context) : PagerAdapter() {
//        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
//            val modelObject = Model.values()[position]
//            val inflater = LayoutInflater.from(mContext)
//            val layout = inflater.inflate(modelObject.layoutResId, collection, false) as ViewGroup
//            collection.addView(layout)
//            return layout
//        }
//
//        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
//            collection.removeView(view as View)
//        }
//
//        override fun getCount(): Int {
//            return Model.values().size
//        }
//
//        override fun isViewFromObject(view: View, `object`: Any): Boolean {
//            return view === `object`
//        }
//
//        override fun getPageTitle(position: Int): CharSequence {
//            val customPagerEnum = Model.values()[position]
//            return mContext.getString(customPagerEnum.titleResId)
//        }
//    }
//
//    enum class Model private constructor(val titleResId: Int, val layoutResId: Int) {
//        RED(R.string.one, R.layout.activity_temp_page),
//        BLUE(R.string.two, R.layout.activity_advice),
//       // GREEN(R.string.three, R.layout.layout_three)
//    }
//
//
//
//}
