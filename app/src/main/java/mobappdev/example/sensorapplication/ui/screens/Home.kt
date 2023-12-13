package mobappdev.example.sensorapplication.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import mobappdev.example.sensorapplication.ui.compnents.ActionButtons
import mobappdev.example.sensorapplication.ui.compnents.Angle
import mobappdev.example.sensorapplication.ui.theme.Styles.blackBg
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
//column fun called home with no parameters
fun Home(
    vm:DataVM
) {
    Column( // Container
        modifier = Modifier
            .fillMaxSize()
            .background(blackBg),
        horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Angle(vm = vm)

            ActionButtons(vm = vm)

    }
}
