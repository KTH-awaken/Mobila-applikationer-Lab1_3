package mobappdev.example.sensorapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import mobappdev.example.sensorapplication.ui.viewmodels.DataVM

@Composable
//column fun called home with no parameters
fun Home(
    vm:DataVM
) {
    //column
    Column {
        //row
        Row {
            //text
            Text(text = "Hello")
            //text
            Text(text = "World")
        }
        //text
        Text(text = "Hello World")
    }

}
