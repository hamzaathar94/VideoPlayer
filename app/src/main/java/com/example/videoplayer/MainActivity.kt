package com.example.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.videoplayer.databinding.ActivityMainBinding
import com.example.videoplayer.view.AudioFragment
import com.example.videoplayer.view.GridViewFragment
import com.example.videoplayer.view.ImageFragment
import com.example.videoplayer.view.LinearViewFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var navView: BottomNavigationView? = null
    private var fragment1: LinearViewFragment? = null
    private var fragment2: GridViewFragment? = null
    private var binding: ActivityMainBinding? = null
    private var fragment3: AudioFragment?=null
    private var fragment4: ImageFragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        fragment1 = LinearViewFragment()
        fragment2 = GridViewFragment()
        fragment3 = AudioFragment()
        fragment4 = ImageFragment()
        setFragment(fragment1)
        navView = binding?.bottomNavigationView

        navView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pdfFragment -> {
                    setFragment(fragment1)
                    true
                }
                R.id.videoFragment -> {
                    setFragment(fragment2)
                    true

                }
//                R.id.audio -> {
//                    setFragment(fragment3)
//                    true
//                }
//                R.id.image -> {
//                    setFragment(fragment4)
//                    true
//
//                }
                else -> false
            }
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.grid -> {
                setFragment(fragment1)
                Toast.makeText(this, "Linear Selected", Toast.LENGTH_SHORT).show()
            }
            R.id.linear -> {
                setFragment(fragment2)
                Toast.makeText(this, "Grid Selected", Toast.LENGTH_SHORT).show()
            }
            R.id.audio -> {
                setFragment(fragment3)
                Toast.makeText(this, "Linear Selected", Toast.LENGTH_SHORT).show()
            }
            R.id.image -> {
                setFragment(fragment4)
                Toast.makeText(this, "Grid Selected", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setFragment(fragment: Fragment?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activity_main_nav_host_fragment, fragment!!)
        fragmentTransaction.commit()
    }
}