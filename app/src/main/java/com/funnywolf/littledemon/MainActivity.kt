package com.funnywolf.littledemon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.demo.DemoHostFragment
import com.funnywolf.littledemon.fragments.*
import com.funnywolf.littledemon.utils.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFragment(FragmentIntent(HostFragment::class.java), false)
//        addFragment(FragmentIntent(DemoHostFragment::class.java), false)
        GlobalObserverManager.addObserver(LiveObserver<FragmentIntent>(TYPE_FRAGMENT_INTENT,
            { stateData -> startFragment(stateData.data) }, this))
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
        private const val TYPE_FRAGMENT_INTENT = -1

        fun startFragment(fragmentClass: Class<out Fragment>, bundle: Bundle? = null) {
            GlobalObserverManager.post(TYPE_FRAGMENT_INTENT, STATE_READY, FragmentIntent(fragmentClass, bundle))
        }

    }

}
