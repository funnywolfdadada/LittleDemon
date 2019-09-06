package com.funnywolf.littledemon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.demo.DemoHostFragment
import com.funnywolf.littledemon.fragments.*
import com.funnywolf.littledemon.live.BaseLiveObservable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        addFragment(FragmentIntent(HostFragment::class.java), false)
        addFragment(FragmentIntent(DemoHostFragment::class.java), false)
        intentObserver.addObserver(this::startFragment, this, false)
    }

    private fun startFragment(intent: FragmentIntent?) {
        addFragment(intent ?: return, true)
    }

    private fun addFragment(intent: FragmentIntent, toStack: Boolean?) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_content, intent.fragmentClass.newInstance().apply {
                arguments = intent.bundle
            })
            .apply {
                if (toStack == true) {
                    addToBackStack(intent.fragmentClass.name)
                }
            }
            .commit()
    }

    data class FragmentIntent(val fragmentClass: Class<out Fragment>, val bundle: Bundle? = null)

    companion object {

        private val intentObserver = BaseLiveObservable<FragmentIntent>()

        fun startFragment(fragmentClass: Class<out Fragment>, bundle: Bundle? = null) {
            intentObserver.dispatchValue(FragmentIntent(fragmentClass, bundle))
        }

    }

}
