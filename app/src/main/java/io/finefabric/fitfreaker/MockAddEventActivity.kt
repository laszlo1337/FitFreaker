package io.finefabric.fitfreaker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_event.*

/**
 * Created by laszlo on 2017-08-14.
 */
class MockAddEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        toolbar.title = getString(R.string.title_add_event)
    }
}