package io.finefabric.fitfreaker

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { itemSelected ->
        when (itemSelected.itemId) {
            R.id.navigation_home -> {
                message.visibility = View.GONE
                day_view.visibility = View.VISIBLE
            }
            R.id.navigation_dashboard -> {
                message.visibility = View.VISIBLE
                day_view.visibility = View.GONE
                message.setText(R.string.title_dashboard)
            }
            R.id.navigation_notifications -> {
                message.visibility = View.VISIBLE
                message.setText(R.string.title_home)
                day_view.visibility = View.GONE
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(toolbar)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        day_view.setOnAddEventClickListener(object:DayScheduleView.OnAddEventClickListener{
            override fun onClick() {
                navigation.selectedItemId = R.id.navigation_dashboard
                message.text = "add new event"
                Log.d("DayView", "add new event clicked")
            }
        })
    }
}
