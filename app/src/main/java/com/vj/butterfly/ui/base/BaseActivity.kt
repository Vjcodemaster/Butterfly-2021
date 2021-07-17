package com.vj.butterfly.ui.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vj.butterfly.R

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_base)
    }
}