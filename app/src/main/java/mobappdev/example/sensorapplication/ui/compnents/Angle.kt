package mobappdev.example.sensorapplication.ui.compnents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobappdev.example.sensorapplication.ui.theme.Styles.yellowAppleWatch
import mobappdev.example.sensorapplication.ui.viewmodels.CombinedSensorData
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
fun Angle(
    vm: DataVM
) {
    val combinedSensorData = vm.combinedDataFlow.collectAsState().value
    val polarGData = vm.polarGyroDataFlow.collectAsState().value
    val polarAData = vm.polarAccDataFlow.collectAsState().value

    var polarLinAngle1 = 0.0f
    var polarGyroAngle1 = 0.0f

    var polarGyroAngle = polarGData?.let { (x) ->
        polarGyroAngle1 = x
        x
    } ?: 0.0f

    var polarLinAngle = polarAData?.let { (x) ->
        polarLinAngle1 = x
        x
    } ?: 0.0f

    var linAngle = 0.0f
    var gyroAngle = 0.0f

    if (combinedSensorData is CombinedSensorData.LinAccAndGyroData) {
        combinedSensorData.linAcc?.let { (x) ->
            linAngle = x
        }
        combinedSensorData.gyro?.let { (x) ->
            gyroAngle = x
        }
    }
    Column {

        Row {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp,bottom = 20.dp, start = 5.dp, end = 5.dp),
                    text = " ${linAngle.toInt()}°",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                    color = yellowAppleWatch,
                )
                Text(text = "Acc", color =yellowAppleWatch)
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp,bottom = 20.dp, start = 5.dp, end = 5.dp),
                    text = " ${gyroAngle.toInt()}°",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                    color = yellowAppleWatch,
                )
                Text(text = "Gyro", color =yellowAppleWatch)
            }
        }
        Row {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 20.dp,bottom = 20.dp, start = 5.dp, end = 5.dp),
                        text = " ${polarLinAngle.toInt()}°",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                        color = yellowAppleWatch,
                    )
                    Text(text = "P Acc", color =yellowAppleWatch)
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 20.dp,bottom = 20.dp, start = 5.dp, end = 5.dp),
                        text = " ${polarGyroAngle.toInt()}°",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                        color = yellowAppleWatch,
                    )
                    Text(text = "P Gyro", color =yellowAppleWatch)
                }

        }
    }


}

