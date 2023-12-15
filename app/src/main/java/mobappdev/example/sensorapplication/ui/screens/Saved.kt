package mobappdev.example.sensorapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobappdev.example.sensorapplication.R
import mobappdev.example.sensorapplication.data.model.Measurement
import mobappdev.example.sensorapplication.ui.theme.Styles
import mobappdev.example.sensorapplication.ui.theme.Styles.blackText
import mobappdev.example.sensorapplication.ui.theme.Styles.yellowAppleWatch
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Saved(
    vm: DataVM
){
    val isPolarStream = vm.isPolarStreaming.collectAsState()
    vm.updateUIMeasurements()
    val savedData = vm.savedData.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Styles.blackBg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            modifier = Modifier.padding(1.dp)
        ) {
            items(savedData.value.size) { index ->
                val session = savedData.value[index]
                SavedItem(vm = vm,session = session)
            }
        }
    }
}

@Composable
fun SavedItem(
    vm:DataVM,
    session: List<Measurement>
){
    val firstMesurment = session.firstOrNull()
    Column(
        modifier = Modifier
            .padding(1.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color(32, 33, 36),
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .size(width = Styles.componentWidth, height = 80.dp)
                .padding(5.dp)


        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceAround

            ){
                val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.getDefault())
                val formattedTime = if (firstMesurment != null) {
                    dateFormat.format(firstMesurment.time)
                } else {
                    ""
                }
                Text(
                    modifier = Modifier.padding(top = 17.dp),
                    text = formattedTime,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp),
                    color = yellowAppleWatch,
                )
                IconButton(
                    modifier = Modifier.padding(top = 5.dp),

                    onClick = {
                        vm.exportMeasurements(session)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_file_download_24),
                        contentDescription = "Download",
                        tint = yellowAppleWatch,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}