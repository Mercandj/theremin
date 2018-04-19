package com.mercandalli.android.sdk.soundsystem.lesson

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class ParseSteps(private val context: Context) {

    fun parse(): List<Step> {

        val buf = StringBuilder()
        val json = context.assets.open("shape_of_you_steps.json")
        val `in` = BufferedReader(InputStreamReader(json, "UTF-8"))
        var str = `in`.readLine()
        while (str != null) {
            buf.append(str)
            str = `in`.readLine()
        }
        `in`.close()
        val jsonArray = JSONObject(buf.toString()).getJSONArray("events")
        return Step.fromJson(jsonArray)
    }

}