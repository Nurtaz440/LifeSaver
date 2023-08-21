package com.example.ambulance.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.ambulance.R
import com.example.ambulance.model.LocationDetails

class MyLocationListAdapter (private val context: Activity,
                             private val title: String, private val description : String, list : MutableList<LocationDetails>, private val imgid: Int)
    : ArrayAdapter<LocationDetails>(context, R.layout.custom_list,list) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val titleText = rowView.findViewById(R.id.tv_title) as TextView
        val imageView = rowView.findViewById(R.id.ic_message) as ImageView
        val subtitleText = rowView.findViewById(R.id.tv_desc) as TextView

        titleText.text = title
        imageView.setImageResource(imgid)
        subtitleText.text = description

        return rowView
    }
}