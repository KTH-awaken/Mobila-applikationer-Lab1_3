package mobappdev.example.sensorapplication.domain

/**
 * File: PolarController.kt
 * Purpose: Defines the blueprint for the polar controller model
 * Author: Jitse van Esch
 * Created: 2023-07-08
 * Last modified: 2023-07-11
 */


import kotlinx.coroutines.flow.StateFlow
import mobappdev.example.sensorapplication.data.model.Measurement

interface PolarController {
    val currentHR: StateFlow<Int?>
    val hrList: StateFlow<List<Int>>

    val connected: StateFlow<Boolean>
    val measuring: StateFlow<Boolean>

    val currentLinAccUI: StateFlow<Triple<Float, Float, Float>?>
    val currentGyroUI: StateFlow<Triple<Float, Float, Float>?>
    val streamingGyro: StateFlow<Boolean>
    val streamingLinAcc: StateFlow<Boolean>

    val measurementsUI:StateFlow<List<List<Measurement>>>

    fun updateUIMeasurements()
    fun connectToDevice(deviceId: String)
    fun disconnectFromDevice(deviceId: String)


    fun startGyroStream(deviceId:String)
    fun stopGyroStream()
    fun startHrStreaming(deviceId: String)
    fun stopHrStreaming()
}