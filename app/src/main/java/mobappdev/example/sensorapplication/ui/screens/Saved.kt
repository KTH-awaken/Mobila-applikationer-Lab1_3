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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobappdev.example.sensorapplication.data.model.Measurement
import mobappdev.example.sensorapplication.ui.theme.Styles
import mobappdev.example.sensorapplication.ui.theme.Styles.blackText
import mobappdev.example.sensorapplication.ui.theme.Styles.yellowAppleWatch
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
fun Saved(
    vm: DataVM
){
    val savedData = vm.savedData.collectAsState()
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
            items(savedData.value.size) { index -> // Iterate over the index of savedData list
                val session = savedData.value[index] // Get the session which is a List<Measurement>
                SavedItem(session = session)
            }
        }

    }
}

@Composable
fun SavedItem(
    session: List<Measurement>
){
    val firstMesurment = session.first()
    Column(
        modifier = Modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = yellowAppleWatch,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .size(width = Styles.componentWidth, height = 140.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        ){

            Text(
                modifier = Modifier
                    .padding(top = 20.dp,bottom = 20.dp),
                text = if (firstMesurment.time != null) firstMesurment.time.toString() else "",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                color = blackText,
            )



        }
    }
}