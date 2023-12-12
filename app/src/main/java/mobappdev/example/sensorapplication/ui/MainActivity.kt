package mobappdev.example.sensorapplication.ui

/**
 * File: MainActivity.kt
 * Purpose: Defines the main activity of the application.
 * Author: Jitse van Esch
 * Created: 2023-07-08
 * Last modified: 2023-09-21
 */

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
import mobappdev.example.sensorapplication.ui.screens.BluetoothDataScreen
import mobappdev.example.sensorapplication.ui.theme.SensorapplicationTheme
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.sensorapplication.ui.compnents.BottomBar
import mobappdev.example.sensorapplication.ui.screens.Home

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Todo: Change for your own deviceID
    private var deviceId = "B37EA42F"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                val vm = hiltViewModel<DataVM>()

                // Use hardcoded deviceID
                vm.chooseSensor(deviceId)

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxWidth(),
                          bottomBar = { BottomBar(navController = navController, ) }
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


    @Composable
    fun NavigationGraph(navController: NavHostController, vm: DataVM) {
        NavHost(navController, startDestination = Destinations.HomeScreen.route) {
            composable(Destinations.HomeScreen.route) {
                Home(vm = vm)
            }
            composable(Destinations.Search.route) {
                    BluetoothDataScreen(vm = vm)
            }
            composable(Destinations.Favourite.route) {
//                Favorites(vm=vm)
            }
        }
    }
}
