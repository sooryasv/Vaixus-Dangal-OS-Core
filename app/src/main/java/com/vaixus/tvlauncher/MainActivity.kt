package com.vaixus.tvlauncher

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "VaixusTVLauncher"
        private const val SPLASH_DURATION_MS = 3000L
    }

    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.activity_main)

        // Lock Back Button to keep home launcher persistent
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing to prevent exiting the home launcher
                }
            }
        )

        val splashOverlay: View = findViewById(R.id.splashOverlay)
        val dashboardContainer: View = findViewById(R.id.dashboardContainer)

        val btnAvInput: Button = findViewById(R.id.btnAvInput)
        val btnMiracast: Button = findViewById(R.id.btnMiracast)
        val btnWifi: Button = findViewById(R.id.btnWifi)
        val btnSettings: Button = findViewById(R.id.btnSettings)

        // Set button click listeners
        btnAvInput.setOnClickListener { openLiveTv() }
        btnMiracast.setOnClickListener { openMiracast() }
        btnWifi.setOnClickListener { openWifiSettings() }
        btnSettings.setOnClickListener { openSystemSettings() }

        // Start 3000ms Splash screen timer with smooth fade transition
        Handler(Looper.getMainLooper()).postDelayed({
            splashOverlay.animate()
                .alpha(0f)
                .setDuration(500L)
                .withEndAction {
                    splashOverlay.visibility = View.GONE
                }

            dashboardContainer.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(500L)
                    .withEndAction {
                        btnAvInput.requestFocus()
                    }
            }
        }, SPLASH_DURATION_MS)

        // Setup real-time Network Status Callback
        setupNetworkListener()
    }

    private fun setupNetworkListener() {
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
        updateNetworkStatus(isNetworkConnected())

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread { updateNetworkStatus(true) }
            }

            override fun onLost(network: Network) {
                runOnUiThread { updateNetworkStatus(isNetworkConnected()) }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                runOnUiThread { updateNetworkStatus(hasInternet) }
            }
        }

        try {
            networkCallback?.let {
                connectivityManager?.registerDefaultNetworkCallback(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering NetworkCallback", e)
            updateNetworkStatus(isNetworkConnected())
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = connectivityManager ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = cm.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo != null && networkInfo.isConnected
        }
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        val ivNetworkStatus: ImageView = findViewById(R.id.ivNetworkStatus) ?: return
        val colorRes = if (isConnected) R.color.net_connected else R.color.net_disconnected
        ivNetworkStatus.setColorFilter(ContextCompat.getColor(this, colorRes))
    }

    private fun openLiveTv() {
        var launched = false

        // 1. Try standard Android TV Core App
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.android.tv")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                launched = true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Core TV app launch failed", e)
        }

        // 2. Try Xiaomi specific TV Player
        if (!launched) {
            try {
                val intent = packageManager.getLaunchIntentForPackage("com.xiaomi.mitv.tvplayer")
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    launched = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Xiaomi TV Player launch failed", e)
            }
        }

        // 3. Try TV URI Passthrough (AV/HDMI)
        if (!launched) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tv://channel"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                launched = true
            } catch (e: Exception) {
                Log.w(TAG, "TV URI intent launch failed", e)
            }
        }

        if (!launched) {
            Toast.makeText(this, "Live TV service not found.", Toast.LENGTH_LONG).show()
        }
    }

    private fun openMiracast() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.xiaomi.mitv.smartshare")
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                startActivity(Intent("android.settings.CAST_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Miracast not available.", Toast.LENGTH_LONG).show()
        }
    }

    private fun openWifiSettings() {
        try {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun openSystemSettings() {
        try {
            startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            Toast.makeText(this, "Settings access blocked.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            networkCallback?.let {
                connectivityManager?.unregisterNetworkCallback(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering NetworkCallback", e)
        }
    }
}