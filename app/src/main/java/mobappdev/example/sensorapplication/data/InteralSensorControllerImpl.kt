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
import mobappdev.example.sensorapplication.data.Repository.MeasurementsRepo
import mobappdev.example.sensorapplication.data.model.MathFilter
import mobappdev.example.sensorapplication.data.model.Measurement
import mobappdev.example.sensorapplication.data.model.addMeasurement
import mobappdev.example.sensorapplication.domain.InternalSensorController
import java.util.Collections.addAll
import java.util.Date
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

private const val LOG_TAG = "Internal Sensor Controller"

class InternalSensorControllerImpl(
    context: Context,
    val measurementsRepo: MeasurementsRepo
): InternalSensorController, SensorEventListener {

    // Expose acceleration to the UI
    private val _currentLinAccUI = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    override val currentLinAccUI: StateFlow<Triple<Float, Float, Float>?>
        get() = _currentLinAccUI.asStateFlow()

    private var _currentGyro: Triple<Float, Float, Float>? = null
    private var _currentLinAcc: Triple<Float, Float, Float>? = null

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

    private val _measurementsUI = MutableStateFlow<List<Measurement>>(emptyList())
    override val measurementsUI: StateFlow<List<Measurement>?>
        get() = _measurementsUI.asStateFlow()

    private var _currentMeasurements = MutableStateFlow<List<Measurement>>(emptyList())

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }
    private val linAccSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private var lastLinAccSample: Triple<Float, Float, Float>? = null
    private var lastGyroSample: Triple<Float, Float, Float>? = null

    private lateinit var mathFilter: MathFilter


    override fun startImuStream() {
        if (linAccSensor == null) {
            Log.e(LOG_TAG, "LinAcc sensor is not available on this device")
            return
        }
        if (_streamingLinAcc.value) {
            Log.e(LOG_TAG, "LinAcc sensor is already streaming")
            return
        }

        // Register this class as a listener for gyroscope events
        sensorManager.registerListener(this, linAccSensor, SensorManager.SENSOR_DELAY_UI)
        mathFilter = MathFilter()

        // Start a coroutine to update the UI variable on a 2 Hz interval
        GlobalScope.launch(Dispatchers.Main) {
            _streamingLinAcc.value = true
            while (_streamingLinAcc.value) {
                // Update the UI variable
                measurementsRepo.listOfMeasurementsFlow.collect { listOfMeasurements ->
                    Log.d("COLLECTING","Size of listOfMeasurements: ${listOfMeasurements.size}")
                    Log.d("COLLECTING","listOfMeasurements: $listOfMeasurements")
                }
                _currentMeasurements.addMeasurement(_currentLinAcc)
                Log.d("MEASUREMENT", "Size=${_currentMeasurements.value.size}")
                Log.d("MEASUREMENT", "Measurement=${_currentMeasurements.value.last()}")

                _measurementsUI.update { _currentMeasurements.value }
                _currentLinAccUI.update { _currentLinAcc }
                delay(500)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun stopImuStream() {
        if (_streamingLinAcc.value) {
            sensorManager.unregisterListener(this, linAccSensor)
            GlobalScope.launch {
                measurementsRepo.saveMeasurementsToList(_currentMeasurements.value)
                _currentMeasurements.value = emptyList()
            }
            _streamingLinAcc.value = false
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun startGyroStream() {
        if (gyroSensor == null && linAccSensor == null) {
            Log.e(LOG_TAG, "Gyroscope and LinAcc sensor is not available on this device")
            return
        }
        if (_streamingGyro.value && _streamingLinAcc.value) {
            Log.e(LOG_TAG, "Gyroscope and LinAcc sensor is already streaming")
            return
        }

        // Register this class as a listener for gyroscope events
        sensorManager.registerListener(this, linAccSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_UI)
        mathFilter = MathFilter()
        // Start a coroutine to update the UI variable on a 2 Hz interval
        GlobalScope.launch(Dispatchers.Main) {
            _streamingGyro.value = true
            _streamingLinAcc.value = true
            while (_streamingGyro.value && _streamingLinAcc.value) {
                measurementsRepo.listOfMeasurementsFlow.collect { listOfMeasurements ->
                    Log.d("COLLECTING","Size of listOfMeasurements: ${listOfMeasurements.size}")
                    Log.d("COLLECTING","listOfMeasurements: $listOfMeasurements")
                }
                _currentMeasurements.addMeasurement(_currentLinAcc)
                Log.d("MEASUREMENT", "Size=${_currentMeasurements.value.size}")
                Log.d("MEASUREMENT", "Measurement=${_currentMeasurements.value.last()}")

                _measurementsUI.update { _currentMeasurements.value }
                _currentLinAccUI.update { _currentLinAcc }
                _currentGyroUI.update { _currentGyro }
                delay(500)
            }
        }

    }

    override fun stopGyroStream() {
        if (_streamingGyro.value) {
            // Unregister the listener to stop receiving gyroscope events (automatically stops the coroutine as well
            sensorManager.unregisterListener(this, gyroSensor)
            sensorManager.unregisterListener(this, linAccSensor)
            _streamingGyro.value = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val linAccSample = Triple(event.values[0], event.values[1], event.values[2])
            lastLinAccSample = linAccSample
            _currentLinAcc = mathFilter.calculateAngles(linAccSample)
            if (lastGyroSample != null) {
                _currentGyro = mathFilter.calculateAnglesWithGyro(linAccSample, lastGyroSample!!)
                _currentLinAcc = mathFilter.calculateAngles(linAccSample)
            }
        } else if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val gyroSample = Triple(event.values[0], event.values[1], event.values[2])
            lastGyroSample = gyroSample

            if (lastLinAccSample != null) {
                _currentGyro = mathFilter.calculateAnglesWithGyro(lastLinAccSample!!, gyroSample)
                _currentLinAcc = mathFilter.calculateAngles(lastLinAccSample!!)
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not used in this example
    }


}