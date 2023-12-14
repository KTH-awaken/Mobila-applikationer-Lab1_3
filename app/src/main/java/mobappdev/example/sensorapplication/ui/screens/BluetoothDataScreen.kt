package mobappdev.example.sensorapplication.ui.screens

/**
 * File: BluetoothDataScreen.kt
 * Purpose: Defines the UI of the data screen.
 * Author: Jitse van Esch
 * Created: 2023-07-08
 * Last modified: 2023-07-11
 */

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobappdev.example.sensorapplication.ui.Destinations
import mobappdev.example.sensorapplication.ui.theme.Styles
import mobappdev.example.sensorapplication.ui.viewmodels.CombinedSensorData
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
fun BluetoothDataScreen(
    vm: DataVM
) {
    Devices(vm = vm)
    ScanButton(vm = vm)



    /*
    val state = vm.state.collectAsStateWithLifecycle().value
    val deviceId = vm.deviceId.collectAsStateWithLifecycle().value

    val value: String = when (val combinedSensorData = vm.combinedDataFlow.collectAsState().value) {
        is CombinedSensorData.GyroData -> {
            val triple = combinedSensorData.gyro
            if (triple == null) {
                "-"
            } else {
                String.format("%.1f, %.1f, %.1f", triple.first, triple.second, triple.third)
            }

        }
        is CombinedSensorData.LinAccData -> {
            val triple = combinedSensorData.linAcc
            if (triple == null) {
                "-"
            } else {
                String.format("%.1f, %.1f, %.1f", triple.first, triple.second, triple.third)
            }
        }
        is CombinedSensorData.HrData -> combinedSensorData.hr.toString()
        else -> "-"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = if (state.connected) "connected" else "disconnected")
        Box(
            contentAlignment = Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if(state.measuring) value else "-",
                fontSize = if (value.length < 3) 128.sp else 54.sp,
                color = Color.Black,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                onClick = vm::connectToSensor,
                enabled = !state.connected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Connect\n${deviceId}")
            }
            Button(
                onClick = vm::disconnectFromSensor,
                enabled = state.connected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Disconnect")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                onClick = vm::startHr,
                enabled = (state.connected && !state.measuring),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Start\nHr Stream")
            }
            Button(
                onClick = vm::startGyro,
                enabled = (!state.measuring),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Start\nGyro Stream")
            }
            Button(
                onClick = vm::startLinAcc,
                enabled = (!state.measuring),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Start\nLinAcc Stream")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                onClick = vm::stopDataStream,
                enabled = (state.measuring),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Stop\nstream")
            }
        }
    }*/
}

@Composable
fun ScanButton(
    vm: DataVM
){
    Column(
        Modifier
            .padding(bottom = 35.dp)
            .fillMaxHeight()
        ,
        verticalArrangement = Arrangement.Bottom,
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ){

            Button(
                colors = ButtonDefaults.buttonColors(Styles.yellowAppleWatch),
                onClick = { vm.startDeviceScan() },

            ) {
                Text(text = "Scan", color = Styles.blackText)
            }

        }
    }
}

@Composable
fun Devices(
    vm: DataVM
) {
    // listOfDevices sensors only polar sensor
    val devices = vm.devices.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Styles.blackBg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            items(devices) { device ->
                Device(device, vm)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun Device(
    device: BluetoothDevice,
    vm: DataVM
){
    val state = vm.state.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = Styles.yellowAppleWatch,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .size(width = Styles.componentWidth, height = 140.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        ){

            Text(
                modifier = Modifier
                    .padding(top = 20.dp,bottom = 20.dp),
                text =  device.name ?: "Unknown",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                color = Styles.blackText,
            )

            Button(
                onClick = vm::connectToSensor,
                enabled = !state.connected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Connect\n${device}")
            }
            Button(
                onClick = vm::disconnectFromSensor,
                enabled = state.connected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(text = "Disconnect")
            }

        }
    }



}