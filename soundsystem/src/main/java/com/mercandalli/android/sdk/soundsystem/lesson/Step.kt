package com.mercandalli.android.sdk.soundsystem.lesson

import com.mercandalli.android.sdk.soundsystem.StringUtils
import org.json.JSONArray
import org.json.JSONObject

data class Step(
        val files: List<String>,
        val time: Double) {

    companion object {

        fun fromJson(jsonArray: JSONArray): List<Step> {
            val list = ArrayList<Step>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(fromJson(jsonObject))
            }
            return list
        }

        private fun fromJson(json: JSONObject): Step {
            val time = json.getDouble("time")
            val notes = json.getJSONArray("notes")
            val files = StringUtils.fromJsonArray(notes)
            return Step(files, time)
        }

    }

}