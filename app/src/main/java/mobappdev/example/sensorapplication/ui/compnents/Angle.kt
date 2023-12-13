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
    var angle = 0.0f

    if (combinedSensorData is CombinedSensorData.LinAccData){
        combinedSensorData.linAcc?.let {
            angle = it.first
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp,bottom = 20.dp),
            text = " ${angle.toInt()}°",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
            color = yellowAppleWatch,
        )

    }
}