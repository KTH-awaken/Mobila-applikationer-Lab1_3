package mobappdev.example.sensorapplication.data.model

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

data class Measurement(val angle:Double, val time: Date, val sensorType:String,val deviceType:String)


fun MutableStateFlow<List<Measurement>>.addMeasurement(linAcc: Triple<Float, Float, Float>?,sensorType: String,deviceType: String) {
    if(linAcc == null)
        return
    val newMeasurement = linAcc?.let {
        Measurement(
            it.first.toDouble(),
            Date(),
            sensorType,
            deviceType
        )
    }
    this.value = (this.value.orEmpty() + listOfNotNull(newMeasurement))
}

fun List<List<Measurement>>.addMeasurementToList(
    measurements: List<Measurement>
): List<List<Measurement>> {
    // Append the entire list of measurements as a single element
    return this + listOf(measurements)
}