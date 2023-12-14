package mobappdev.example.sensorapplication.ui.compnents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mobappdev.example.sensorapplication.R
import mobappdev.example.sensorapplication.ui.theme.Styles.blackText
import mobappdev.example.sensorapplication.ui.theme.Styles.yellowAppleWatch
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
fun SelectorButtons(
    vm : DataVM
){
    val sensorMode = vm.sensorMode.collectAsState().value
    val sensorType = vm.sensorType.collectAsState().value
    val isToggled = vm.sensorMode.collectAsState().value == "GYRO"
    val show = true //todo set show till false när messurement är igång
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.End,
    ){
       Column(

       ){
//            Column (
//                modifier = Modifier,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ){
//                    Text("Gyro", color = yellowAppleWatch)
//                    Switch(
//                        checked = isToggled,
//                        onCheckedChange = { isChecked ->
//                            vm.setSensorMode(if (isChecked) "GYRO" else "ACC")
//                        },
//                        colors = SwitchDefaults.colors(
//                            checkedThumbColor = blackText,
//                            uncheckedThumbColor = yellowAppleWatch,
//                            checkedTrackColor = yellowAppleWatch,
//                            uncheckedTrackColor = blackText,
//                            checkedBorderColor = yellowAppleWatch,
//                            uncheckedBorderColor = yellowAppleWatch
//
//                        )
//                    )
//                }

            Column {
                IconButton(
                    onClick = {
                      //TODO DOWNOLOAD AS CSV
                    }
                ) {
                    if(show){
                        Icon(
                            painter = painterResource(id = R.drawable.outline_file_download_24),
                            contentDescription = "Download",
                            tint = yellowAppleWatch,
                            modifier = Modifier.size(36.dp)
                        )
                    }else{
                        Icon(
                            painter = painterResource(id = R.drawable.outline_file_download_24),
                            contentDescription = "Download",
                            tint = yellowAppleWatch,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
       }
   }
}
