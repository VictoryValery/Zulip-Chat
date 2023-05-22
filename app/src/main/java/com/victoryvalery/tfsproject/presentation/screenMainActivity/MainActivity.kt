package com.victoryvalery.tfsproject.presentation.screenMainActivity

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.victoryvalery.tfsproject.App
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.databinding.ActivityMainBinding
import com.victoryvalery.tfsproject.domain.services.NetworkConnectivityObserver
import com.victoryvalery.tfsproject.domain.services.NetworkConnectivityObserver.NetworkConnectivityStatus
import com.victoryvalery.tfsproject.presentation.cicerone.Screens.MeUser
import com.victoryvalery.tfsproject.presentation.cicerone.Screens.People
import com.victoryvalery.tfsproject.presentation.cicerone.Screens.Streams
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private lateinit var networkStatus: StateFlow<NetworkConnectivityStatus>

    private val navigator = AppNavigator(this, R.id.nav_host_fragment_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.INSTANCE.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        networkStatus = networkConnectivityObserver.observe().stateIn(
            lifecycleScope,
            SharingStarted.WhileSubscribed(),
            NetworkConnectivityStatus.Init
        )

        networkStatus.onEach {
            if (it != NetworkConnectivityStatus.Available) {
                binding.refreshLayout.isVisible = true
                binding.navHostFragmentMain.isVisible = false
            } else {
                if (savedInstanceState == null) {
                    setCurrentFragment(Streams())
                    binding.bottomNavigationView.selectedItemId = R.id.menu_channels
                }
                binding.navHostFragmentMain.isVisible = true
                binding.refreshLayout.isVisible = false
            }
        }.launchIn(lifecycleScope)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.title) {
                getString(R.string.menu_channels) -> setCurrentFragment(Streams())
                getString(R.string.menu_people) -> setCurrentFragment(People())
                getString(R.string.menu_profile) -> setCurrentFragment(MeUser())
                else -> Log.d("TAG_CICERONE", "error")
            }
            return@setOnItemSelectedListener true
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        if (savedInstanceState == null) {
            setCurrentFragment(Streams())
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            router.exit()
        }
    }

    private fun setCurrentFragment(screen: Screen) {
        router.newRootScreen(screen)
    }

    fun setStatusBarColor(@ColorRes color: Int) {
        window?.statusBarColor = ContextCompat.getColor(this, color)
    }
    fun setNavigationViewVisibility(isVisible: Boolean) {
        binding.bottomNavigationView.isVisible = isVisible
    }
    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}
