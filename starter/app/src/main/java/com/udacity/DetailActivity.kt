package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    val fileName = ""

    private lateinit var gobackButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val filename = intent.getStringExtra("selected_option")

        val optionView = findViewById<TextView>(R.id.file_name).apply {
            text = filename
        }

        val status = intent.getStringExtra("status")

        val statusView = findViewById<TextView>(R.id.status).apply {
            text = status
        }
        gobackButton = findViewById<Button>(R.id.goback_button)
        gobackButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
    }

}
