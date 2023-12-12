package mobappdev.example.sensorapplication.data

/**
 * File: InternalSensorControllerImpl.kt
 * Purpose: Implementation of the Internal Sensor Controller.
 * Author: Jitse van Esch
 * Created: 2023-09-21
 * Last modified: 2023-09-21
 */

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobappdev.example.sensorapplication.domain.InternalSensorController
import java.util.Vector
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

private const val LOG_TAG = "Internal Sensor Controller"

class InternalSensorControllerImpl(
    context: Context
): InternalSensorController, SensorEventListener {

    // Expose acceleration to the UI
    private val _currentLinAccUI = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    override val currentLinAccUI: StateFlow<Triple<Float, Float, Float>?>
        get() = _currentLinAccUI.asStateFlow()

    private var _currentGyro: Triple<Float, Float, Float>? = null

    // Expose gyro to the UI on a certain interval
    private val _currentGyroUI = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    override val currentGyroUI: StateFlow<Triple<Float, Float, Float>?>
        get() = _currentGyroUI.asStateFlow()

    private val _streamingGyro = MutableStateFlow(false)
    override val streamingGyro: StateFlow<Boolean>
        get() = _streamingGyro.asStateFlow()

    private val _streamingLinAcc = MutableStateFlow(false)
    override val streamingLinAcc: StateFlow<Boolean>
        get() = _streamingLinAcc.asStateFlow()

    private val _xAngle = MutableStateFlow(0.0)
    override val xAngle: StateFlow<Double>
        get() = _xAngle.asStateFlow()

    private val _yAngle = MutableStateFlow(0.0)
    override val yAngle: StateFlow<Double>
        get() = _yAngle.asStateFlow()
    private val _zAngle = MutableStateFlow(0.0)
    override val zAngle: StateFlow<Double>
        get() = _zAngle.asStateFlow()


    private var _xPrevAngle = 0.0
    private var _yPrevAngle = 0.0
    private var _zPrevAngle = 0.0

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    override fun startImuStream() {
        // Todo: implement
    }

    override fun stopImuStream() {
        // Todo: implement
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun startGyroStream() {
        if (gyroSensor == null) {
            Log.e(LOG_TAG, "Gyroscope sensor is not available on this device")
            return
        }
        if (_streamingGyro.value) {
            Log.e(LOG_TAG, "Gyroscope sensor is already streaming")
            return
        }

        // Register this class as a listener for gyroscope events
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_UI)

        // Start a coroutine to update the UI variable on a 2 Hz interval
        GlobalScope.launch(Dispatchers.Main) {
            _streamingGyro.value = true
            while (_streamingGyro.value) {
                // Update the UI variable
                _currentGyroUI.update { _currentGyro }
                delay(500)
            }
        }

    }

    override fun stopGyroStream() {
        if (_streamingGyro.value) {
            // Unregister the listener to stop receiving gyroscope events (automatically stops the coroutine as well
            sensorManager.unregisterListener(this, gyroSensor)
            _streamingGyro.value = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            // Extract gyro data (angular speed around X, Y, and Z axes
            val v = Triple(event.values[0], event.values[1], event.values[2])
            _currentGyro = Triple(event.values[0], event.values[1], event.values[2])
            val xAngleRaw = atan2(v.second, v.third) * (180 / Math.PI)
            val yAngleRaw = atan2(v.first, v.third) * (180 / Math.PI)
            val zAngleRaw = atan2(sqrt(v.first * v.first + v.second * v.second), v.third) * (180 / Math.PI)
            _xAngle.value = filterEWMA(xAngleRaw,_xPrevAngle)
            _yAngle.value = filterEWMA(yAngleRaw,_yPrevAngle)
            _zAngle.value = filterEWMA(zAngleRaw,_zPrevAngle)
            Log.d("ANGLE","X:${_xAngle.value.toInt()}"+" Y:${_yAngle.value.toInt()}"+" Z:${_zAngle.value.toInt()}")
        }
    }

    private fun calculateAngle(v:Triple<Float, Float, Float>):Double{
        val refVector = Triple(1.0,0.0,0.0)

        val dotProduct = v.first * refVector.first + v.second * refVector.second + v.third * refVector.third
        val magnitudeRef =  sqrt(refVector.first*refVector.first + refVector.second * refVector.second + refVector.third * refVector.third)
        val magnitudeVector = sqrt(v.first*v.first + v.second * v.second + v.third * v.third)

        val cosTheta = dotProduct / (magnitudeRef * magnitudeVector)
        val safeCosTheta = if (cosTheta > 1.0) 1.0 else if (cosTheta < -1.0) -1.0 else cosTheta

        return atan(safeCosTheta)
    }





    private fun filterEWMA(value:Double,prevValue:Double):Double{
        val a = 0.5
        return a * value + (1 - a) * prevValue
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not used in this example
    }


}