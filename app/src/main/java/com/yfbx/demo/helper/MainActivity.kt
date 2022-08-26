package com.yfbx.demo.helper

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yfbx.helper.start


class MainActivity : AppCompatActivity() {

    private val imageView by lazy { findViewById<ImageView>(R.id.image) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn1).setOnClickListener {
            startTestActivity()
        }

        findViewById<Button>(R.id.btn2).setOnClickListener {
            startCamera()
        }
    }

    /**
     * Activity 跳转
     */
    private fun startTestActivity() {
        start<TestActivity> {
            putExtra("key", "value")

            //onActivityResult
            onResult { result ->
                val msg = result.data?.getStringExtra("data")
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 拍照
     */
    private fun startCamera() {
        start(null, ActivityResultContracts.TakePicturePreview()) {
            imageView.setImageBitmap(it)
        }
    }
}
