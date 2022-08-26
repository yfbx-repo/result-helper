package com.yfbx.demo.helper

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

/**
 * Date: 2022-08-25
 * Author: Edward
 * Desc:
 */
class TestActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn2).visibility = View.GONE
        findViewById<ImageView>(R.id.image).visibility = View.GONE

        findViewById<Button>(R.id.btn1).text = "set result"
        findViewById<Button>(R.id.btn1).setOnClickListener {
            setResult(RESULT_OK, Intent().apply { putExtra("data", "data form TestActivity") })
            finish()
        }
    }
}