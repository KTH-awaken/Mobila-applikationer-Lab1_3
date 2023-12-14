package mobappdev.example.sensorapplication.domain

/**
 * File: InternalSensorController.kt
 * Purpose: Defines the blueprint for the Internal Sensor Controller.
 * Author: Jitse van Esch
 * Created: 2023-09-21
 * Last modified: 2023-09-21
 */

import kotlinx.coroutines.flow.StateFlow
import mobappdev.example.sensorapplication.data.Repository.MeasurementsRepo
import mobappdev.example.sensorapplication.data.model.Measurement

interface InternalSensorController {
    val currentLinAccUI: StateFlow<Triple<Float, Float, Float>?>
    val currentGyroUI: StateFlow<Triple<Float, Float, Float>?>
    val streamingGyro: StateFlow<Boolean>
    val streamingLinAcc: StateFlow<Boolean>

    val measurementsUI:StateFlow<List<List<Measurement>>>


    fun startImuStream()
    fun stopImuStream()

    fun startGyroStream()
    fun stopGyroStream()
}