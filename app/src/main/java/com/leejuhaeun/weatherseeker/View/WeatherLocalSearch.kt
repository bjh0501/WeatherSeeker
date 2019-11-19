package com.leejuhaeun.weatherseeker.View

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.leejuhaeun.weatherseeker.WeatherApi.Glboal.WeatherVar
import com.leejuhaeun.weatherseeker.WeatherRepository.DatabaseHandler
import kotlinx.android.synthetic.main.activity_weather_local_search.*
import android.widget.Spinner
import com.leejuhaeun.weatherseeker.R
import java.lang.Exception

class WeatherLocalSearch : AppCompatActivity() {
    private var dbHandler: DatabaseHandler? = null

    // 지역검색을눌러서 불러오는지 체크
    var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_local_search)

        // kakaoMap()
        getStepSpinner()

        btn_ok.setOnClickListener {
            val first = cmbFirstStep.selectedItem.toString()
            val second = if(cmbSecondStep.selectedItem==null) "" else cmbSecondStep.selectedItem.toString()
            val third = if(cmbThirdStep.selectedItem==null) "" else cmbThirdStep.selectedItem.toString()

            val nxny:HashMap<String,String> = dbHandler!!.getLocationXY(first, second, third)

            setData("NX", nxny.get("NX")!!)
            setData("NY", nxny.get("NY")!!)
            setData("N", nxny.get("N")!!)
            setData("Location", nxny.get("LOCATION")!!)
            setData("Sido", first)
            setData("Gu", second)
            setData("Dong", third)

            WeatherVar.setNX(nxny.get("NX")!!)
            WeatherVar.setNY(nxny.get("NY")!!)
            WeatherVar.setN(nxny.get("N")!!)
            WeatherVar.setLocation(nxny.get("LOCATION")!!)
            WeatherVar.setSido(first)
            WeatherVar.setGu(second)
            WeatherVar.setDong(third)

            val intent = intent

            // 초기설정일때
            if(intent.getStringExtra("state") != null && intent.getStringExtra("state").equals("origin")) {
                val intent2 = Intent(this@WeatherLocalSearch, TempPage::class.java)
                intent2.putExtra("state", "origin")
                startActivity(intent2)
            } else {
                setResult(Activity.RESULT_OK, intent)
            }

            finish()
        }
    }

    fun getSpinnerValue(spinner:Spinner, value:String) {
        try {
            val spinnerCount: Int = spinner.count

            for (i in 0..spinnerCount) {
                if (value.equals(spinner.getItemAtPosition(i).toString())) {
                    spinner.setSelection(i)
                    break
                }
            }
        }catch (e:Exception) {
            Log.d("TESTJH", e.message)
        }
    }

    private fun getStepSpinner() {
        dbHandler = DatabaseHandler(this)

        val localStep = dbHandler!!.getFirstStep()
        setSpinner(localStep, R.layout.spinner_local_item, 1)

        if(isFirst) {
            getSpinnerValue(cmbFirstStep, getData("Sido"))//첫번째 기본값
        }

        cmbFirstStep.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            private var selectedFirstItem:String = ""

            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){ // 첫번째 선택
                selectedFirstItem = parent.getItemAtPosition(position).toString()
                val localStep = dbHandler!!.getSecondStep(selectedFirstItem)
                setSpinner(ArrayList(), R.layout.spinner_local_item, 2)
                setSpinner(ArrayList(), R.layout.spinner_local_item, 3)
                setSpinner(localStep, R.layout.spinner_local_item, 2)

                if(isFirst==true) {
                    getSpinnerValue(cmbSecondStep, getData("Gu")) //두번째 기본값
                }

                cmbSecondStep.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{ // 두번째 선택
                    override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        val localStep = dbHandler!!.getThirdStep(selectedFirstItem, selectedItem)
                        setSpinner(ArrayList(), R.layout.spinner_local_item, 3)
                        setSpinner(localStep, R.layout.spinner_local_item,3)

                        if(isFirst==true) {
                            getSpinnerValue(cmbThirdStep, getData("Dong")) //세번째 기본값
                            isFirst = false
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>){
                        // Another interface callback
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>){
                // Another interface callback
            }
        }
    }

    fun setSpinner(localStep:ArrayList<String>, layout:Int, step:Int) {
        val spinner_adapter = ArrayAdapter<String>(this@WeatherLocalSearch,layout, localStep)
        spinner_adapter.setDropDownViewResource(R.layout.spinner_local_item)
        //스피너의 어댑터 지정

        if(step == 1) {
            cmbFirstStep.setAdapter(spinner_adapter)
        } else if(step == 2) {
            cmbSecondStep.setAdapter(spinner_adapter)
        } else {
            cmbThirdStep.setAdapter(spinner_adapter)
        }
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
}
