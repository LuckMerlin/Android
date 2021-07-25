package luckmerlin.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import luckmerlin.android.demo.R
import luckmerlin.android.demo.databinding.TestBinding
import luckmerlin.databinding.OnModelResolve

class TestActivity: Activity(), OnModelResolve {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<TestBinding>(this, R.layout.test);
    }

    override fun onResolveModelView(context: Context?): Any {
        TODO("Not yet implemented")
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

}