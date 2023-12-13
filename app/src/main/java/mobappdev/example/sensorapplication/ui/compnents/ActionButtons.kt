package mobappdev.example.sensorapplication.ui.compnents
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobappdev.example.sensorapplication.ui.theme.Styles.blackText
import mobappdev.example.sensorapplication.ui.theme.Styles.yellowAppleWatch
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
fun ActionButtons(
    vm: DataVM
){
    val state = vm.state.collectAsStateWithLifecycle().value
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

            if(state.measuring){
                Button(
                    colors = ButtonDefaults.buttonColors(yellowAppleWatch),
                    onClick = vm::stopDataStream,
                    enabled = (state.measuring)
                ) {
                    Text(text = "Stop", color = blackText)
                }
            }else{
                Button(
                    colors = ButtonDefaults.buttonColors(yellowAppleWatch),
                    onClick = vm::startLinAcc,
                    enabled = (!state.measuring)
                ) {
                    Text(text = "Start", color = blackText)
                }
            }
        }
    }
}