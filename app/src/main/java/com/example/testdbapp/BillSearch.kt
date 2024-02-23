package com.example.testdbapp
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class BillSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bill_search_filter)

        val home: ImageButton = findViewById(R.id.btn_return_home_bill)
        home.setOnClickListener {
            finish()
        }
    }
}