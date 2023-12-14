package mobappdev.example.sensorapplication.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import mobappdev.example.sensorapplication.ui.screens.BluetoothDataScreen
import mobappdev.example.sensorapplication.ui.theme.SensorapplicationTheme
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.sensorapplication.R
import mobappdev.example.sensorapplication.ui.compnents.BottomBar
import mobappdev.example.sensorapplication.ui.screens.Home
import mobappdev.example.sensorapplication.ui.screens.Saved

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PermissionRequester {
    companion object {
        private const val CHANNEL_ID = "background_service_channel"
    }

    // Todo: Change for your own deviceID
//    private var deviceId = "B37EA42F" //defult
//    private var deviceId = "C07C8F22" //INTE ALEX sesor
//    private var deviceId = "B5073A26" // ALEX sesor
    private var deviceId = "C07BD021" //vår sensor
    private lateinit var vm: DataVM


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), 31)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 30)
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 29)
        }

        setContent {
            SensorapplicationTheme {
                val navController = rememberNavController()
                  vm = hiltViewModel<DataVM>()
                hideSystemBars()
                // Use hardcoded deviceID
                vm.chooseSensor(deviceId)
                vm.permissionRequester = this

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxWidth(),
                          bottomBar = { BottomBar(navController = navController) }
                    ) {paddingValues ->
                        Box(
                            modifier =Modifier.padding(paddingValues)
                        ){
                            NavigationGraph(navController = navController, vm = vm )
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (vm.maximumMeasurementTime.value==43200000) {
            Log.d("BLUETOOTH", "onStop")
            showBackgroundRunningNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Background Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
    private fun showBackgroundRunningNotification() {
            Log.d("BLUETOOTH", "showBackgroundRunningNotification")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Background Task")
            .setContentText("The task is running in the background.")
            .setSmallIcon(R.drawable.outline_file_download_24) // Replace with your icon
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification) // 1 is the notification ID
    }


    private fun requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), 31)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 30)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 29)
        }
    }

    @Composable
    fun NavigationGraph(navController: NavHostController, vm: DataVM) {
        NavHost(navController, startDestination = Destinations.HomeScreen.route) {
            composable(Destinations.HomeScreen.route) {
                Home(vm = vm)
            }
            composable(Destinations.Saved.route) {
                Saved(vm = vm)
            }
            composable(Destinations.Devices.route) {
                    BluetoothDataScreen(vm = vm)
            }
        }
    }
    override fun triggerPermissionRequest(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == DataVM.REQUEST_FINE_LOCATION_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                vm.startDeviceScan()
            } else {
                // Permission was denied, handle this case
            }
        }
    }



}





fun ComponentActivity.hideSystemBars() {
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LOW_PROFILE
            )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
interface PermissionRequester {
    fun triggerPermissionRequest(permissions: Array<String>, requestCode: Int)
}
