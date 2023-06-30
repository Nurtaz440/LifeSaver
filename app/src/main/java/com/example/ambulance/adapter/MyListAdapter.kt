package com.example.ambulance.adapter

import android.content.Context
import android.graphics.ColorSpace.Model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.ambulance.R
import com.example.ambulance.model.LocationModel

class MyListAdapter(var mCtx: Context, var resourse: Int, var items: List<LocationModel>) :
    ArrayAdapter<LocationModel>(mCtx, resourse, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view : View = layoutInflater.inflate(resourse,null)
        val textView = view.findViewById<TextView>(R.id.tv_id)
        val textViewLat = view.findViewById<TextView>(R.id.tv_lat)
        val textViewLon = view.findViewById<TextView>(R.id.tv_lon)
        var mItems : LocationModel = items[position]
        textView.text = mItems.title
        textViewLat.text = mItems.lat
        textViewLon.text = mItems.long

        return view
    }
}