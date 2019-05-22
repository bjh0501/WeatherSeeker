package com.leejuhaeun.weatherseeker.WeatherRepository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONArray
import java.lang.Exception


class DatabaseHandler(context: Context) :SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                "($NO INTEGER PRIMARY KEY AUTOINCREMENT, $FIRST_STEP TEXT, $SECOND_STEP TEXT, $THIRD_STEP TEXT, $NX TEXT, $NY TEXT, " +
                "  UNIQUE ($FIRST_STEP,$SECOND_STEP,$THIRD_STEP));"
        db?.execSQL(CREATE_TABLE)
    }

    fun getRows():Int {
        var cnt: String = "0";

        try {
            val db = readableDatabase
            val selectALLQuery = "SELECT count(*) AS CNT FROM $TABLE_NAME"
            val cursor = db.rawQuery(selectALLQuery, null)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        cnt = cursor.getString(cursor.getColumnIndex("CNT"))
                    } while (cursor.moveToNext())
                }
            } else {
                Log.d("NODB", "NODB!!")
            }

            cursor.close()
            db.close()
        } catch (e: Exception) {
            Log.d("ERROREXCEPT", e.message)
        }

        return cnt.toInt()
    }

    fun getLocationXY(first:String, second:String, third:String) : HashMap<String, String> {
        val db= readableDatabase
        var selectALLQuery = ""

        if(third != "") {
            selectALLQuery = "SELECT DISTINCT $NX, $NY FROM $TABLE_NAME WHERE $FIRST_STEP = \"$first\" AND $SECOND_STEP = \"$second\" AND $THIRD_STEP = \"$third\""
        } else if(second != "") {
            selectALLQuery = "SELECT DISTINCT $NX, $NY FROM $TABLE_NAME WHERE $FIRST_STEP = \"$first\" AND $SECOND_STEP = \"$second\" AND $THIRD_STEP = \"NULL\""
        } else {
            selectALLQuery = "SELECT DISTINCT $NX, $NY FROM $TABLE_NAME WHERE $FIRST_STEP = \"$first\" AND $SECOND_STEP = \"NULL\" AND $THIRD_STEP = \"NULL\""
        }

        val cursor = db.rawQuery(selectALLQuery, null)
        var nx: String =""
        var ny : String =""

        var nxnyInfo = HashMap<String, String>()

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                var i = 0;

                do {
                    nx = cursor.getString(cursor.getColumnIndex(NX))
                    ny = cursor.getString(cursor.getColumnIndex(NY))

                    nxnyInfo.put("NX", nx)
                    nxnyInfo.set("NY", ny)
                } while (cursor.moveToNext())
            }
        }

        cursor.close()
        db.close()

        return nxnyInfo!!
    }

    fun getFirstStep() : ArrayList<String> {
        var arrFirstStep = ArrayList<String>()

        val db = readableDatabase
        val selectALLQuery = "SELECT DISTINCT $FIRST_STEP FROM $TABLE_NAME"
            val cursor = db.rawQuery(selectALLQuery, null)
        var firstStep : String =""

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                var i = 0;

                do {
                    firstStep = cursor.getString(cursor.getColumnIndex(FIRST_STEP))
                    arrFirstStep.add(i, firstStep)
                } while (cursor.moveToNext())
            }
        }

        cursor.close()
        db.close()
        arrFirstStep.reverse()

        return arrFirstStep
    }

    fun getSecondStep(firstStep:String) : ArrayList<String> {
        var arrSecondStep = ArrayList<String>()

        val db = readableDatabase
        val selectALLQuery = "SELECT DISTINCT $SECOND_STEP FROM $TABLE_NAME WHERE $FIRST_STEP = \"$firstStep\" AND $SECOND_STEP != \"NULL\""
        val cursor = db.rawQuery(selectALLQuery, null)
        var secondStep : String =""

        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    var i = 0;

                    do {
                        secondStep = cursor.getString(cursor.getColumnIndex(SECOND_STEP))
                        arrSecondStep.add(i, secondStep)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.d("REALERROR:", e.message)
        }

        cursor.close()
        db.close()
        arrSecondStep.reverse()

       return arrSecondStep
    }

    fun getThirdStep(firstStep:String, secondStep:String) : ArrayList<String> {
        var arrThirdStep = ArrayList<String>()

        val db = readableDatabase
        val selectALLQuery = "SELECT DISTINCT $THIRD_STEP FROM $TABLE_NAME WHERE $FIRST_STEP = \"$firstStep\" AND $SECOND_STEP = \"$secondStep\" AND $THIRD_STEP != \"NULL\""
        val cursor = db.rawQuery(selectALLQuery, null)
        var thirdStep : String =""

        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    var i = 0;

                    do {
                        thirdStep = cursor.getString(cursor.getColumnIndex(THIRD_STEP))
                        arrThirdStep.add(i, thirdStep)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.d("REALERROR:", e.message)
        }

        cursor.close()
        db.close()
        arrThirdStep.reverse()

        return arrThirdStep
    }

    fun initLocalData(s1: String, s2: String, s3: String, x:String, y:String): Boolean {
        //Create and/or open a database that will be used for reading and writing.
        val db = this.writableDatabase
        val values = ContentValues()

     //   values.put(NO, "0")
        values.put(FIRST_STEP, s1)
        values.put(SECOND_STEP, s2)
        values.put(THIRD_STEP, s3)
        values.put(NX, x)
        values.put(NY, y)

        val _success = db.insert(TABLE_NAME, null, values)

        db.close()

        return (Integer.parseInt("$_success") != -1)
    }

    companion object {
        private val DB_NAME = "WeatherDB"
        private val DB_VERSIOM = 1;
        private val TABLE_NAME = "LOCAL";

        private val NO = "NO";
        private val FIRST_STEP = "FIRST_STEP";
        private val SECOND_STEP = "SECOND_STEP";
        private val THIRD_STEP = "THRID_STEP";
        private val NX = "NX";
        private val NY = "NY";
    }
}

