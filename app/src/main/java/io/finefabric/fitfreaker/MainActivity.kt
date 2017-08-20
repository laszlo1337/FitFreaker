package io.finefabric.fitfreaker

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        day_view.setOnAddEventClickListener(listener = object : DayScheduleView.OnAddEventClickListener {
            override fun onClick() {
                launchAddEventActivity()
            }
        })

    }

    fun launchAddEventActivity() {
        val intent = Intent(applicationContext, MockAddEventActivity::class.java)
        startActivityForResult(intent, 1)
    }
}
