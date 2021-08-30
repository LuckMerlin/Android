package luckmerlin.android

import android.app.Activity
import android.os.Bundle
import luckmerlin.android.demo.R
import luckmerlin.databinding.M

class TestActivity: Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        M.setContentView(this, R.layout.test)
    }

}