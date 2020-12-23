package com.example.asgkalitekontrol.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.asgkalitekontrol.R
import com.example.asgkalitekontrol.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
   lateinit var mainActivityBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mainActivityBinding.root)

    }
}