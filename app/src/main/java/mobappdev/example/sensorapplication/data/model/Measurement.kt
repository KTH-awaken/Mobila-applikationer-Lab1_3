package mobappdev.example.sensorapplication.data.model

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

data class Measurement(val angle:Double, val time: Date)


fun MutableStateFlow<List<Measurement>>.addMeasurement(linAcc: Triple<Float, Float, Float>?) {
    if(linAcc == null)
        return
    this.value = (this.value.orEmpty() +
            linAcc?.first?.let {
                Measurement(
                    it.toDouble(), Date()
                )
            }) as List<Measurement>
}