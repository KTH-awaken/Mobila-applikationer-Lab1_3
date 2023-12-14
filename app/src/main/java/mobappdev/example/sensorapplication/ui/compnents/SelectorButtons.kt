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

    val isToggled = vm.sensorType.collectAsState().value == "POLAR"
    val isToggledDay = vm.maximumMeasurementTime.collectAsState().value == 43200000
    val show = vm.state.collectAsState().value.connected
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.End,
    ){
       Column(

       ){

           if (show){
               Column (
                   modifier = Modifier,
                   horizontalAlignment = Alignment.CenterHorizontally
               ){
                   Text("Polar", color = yellowAppleWatch)
                   Switch(
                       checked = isToggled,
                       onCheckedChange = { isChecked ->
                           vm.setSensorType(if (isChecked) "POLAR" else "INTERNAL")
                       },
                       colors = SwitchDefaults.colors(
                           checkedThumbColor = blackText,
                           uncheckedThumbColor = yellowAppleWatch,
                           checkedTrackColor = yellowAppleWatch,
                           uncheckedTrackColor = blackText,
                           checkedBorderColor = yellowAppleWatch,
                           uncheckedBorderColor = yellowAppleWatch

                       )
                   )
               }
           }
           if (true){
               Column (
                   modifier = Modifier,
                   horizontalAlignment = Alignment.CenterHorizontally
               ){
                   Text("Day", color = yellowAppleWatch)
                   Switch(
                       checked = isToggledDay,
                       onCheckedChange = { isChecked ->
                           vm.setMaximumMesurmentTime(if (isChecked) 43200000 else 10000)
                       },
                       colors = SwitchDefaults.colors(
                           checkedThumbColor = blackText,
                           uncheckedThumbColor = yellowAppleWatch,
                           checkedTrackColor = yellowAppleWatch,
                           uncheckedTrackColor = blackText,
                           checkedBorderColor = yellowAppleWatch,
                           uncheckedBorderColor = yellowAppleWatch

                       )
                   )
               }
           }

            /*Column {
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
            }*/
       }
   }
}
