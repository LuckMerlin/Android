package com.merlin.file;

import android.app.Activity;
import android.os.Bundle;

import com.file.manager.R;
import luckmerlin.databinding.M;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        M.setContentView(this,R.layout.activity_browser);
    }
}