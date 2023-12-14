package mobappdev.example.sensorapplication.ui.screens

import androidx.compose.runtime.Composable
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
fun BluetoothDataScreen(
) {

//    val state = vm.state.collectAsStateWithLifecycle().value
//    val deviceId = vm.deviceId.collectAsStateWithLifecycle().value
//
//    val value: String = when (val combinedSensorData = vm.combinedDataFlow.collectAsState().value) {
//        is CombinedSensorData.GyroData -> {
//            val triple = combinedSensorData.gyro
//            if (triple == null) {
//                "-"
//            } else {
//                String.format("%.1f, %.1f, %.1f", triple.first, triple.second, triple.third)
//            }
//
//        }
//        is CombinedSensorData.LinAccData -> {
//            val triple = combinedSensorData.linAcc
//            if (triple == null) {
//                "-"
//            } else {
//                String.format("%.1f, %.1f, %.1f", triple.first, triple.second, triple.third)
//            }
//        }
//        is CombinedSensorData.HrData -> combinedSensorData.hr.toString()
//        else -> "-"
//    }
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = CenterHorizontally
//    ) {
//        Text(text = if (state.connected) "connected" else "disconnected")
//        Box(
//            contentAlignment = Center,
//            modifier = Modifier.weight(1f)
//        ) {
//            Text(
//                text = if(state.measuring) value else "-",
//                fontSize = if (value.length < 3) 128.sp else 54.sp,
//                color = Color.Black,
//            )
//        }
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceAround,
//            modifier = Modifier.fillMaxWidth()
//        ){
//            Button(
//                onClick = vm::connectToSensor,
//                enabled = !state.connected,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = "Connect\n${deviceId}")
//            }
//            Button(
//                onClick = vm::disconnectFromSensor,
//                enabled = state.connected,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = "Disconnect")
//            }
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceAround,
//            modifier = Modifier.fillMaxWidth()
//        ){
//            Button(
//                onClick = vm::startHr,
//                enabled = (state.connected && !state.measuring),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = "Start\nHr Stream")
//            }
//            Button(
//                onClick = vm::startGyro,
//                enabled = (!state.measuring),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = "Start\nGyro Stream")
//            }
//            Button(
//                onClick = vm::startLinAcc,
//                enabled = (!state.measuring),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = "Start\nLinAcc Stream")
//            }
//        }
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceAround,
//            modifier = Modifier.fillMaxWidth()
//        ){
//            Button(
//                onClick = vm::stopDataStream,
//                enabled = (state.measuring),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    disabledContainerColor = Color.Gray
//                )
//            ) {
//                Text(text = "Stop\nstream")
//            }
//        }
//    }
}