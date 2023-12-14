package mobappdev.example.sensorapplication.data.model

import kotlin.math.atan2
import kotlin.math.sqrt

class MathFilter {

    private val alpha: Float = 0.99f

    private var _xPrevAngle = 0.0
    private var _yPrevAngle = 0.0
    private var _zPrevAngle = 0.0
    fun calculateAngles(v:Triple<Float,Float,Float>):Triple<Float,Float,Float>{
        val xAngleRaw = atan2(v.second, v.third) * (180 / Math.PI)
        val yAngleRaw = atan2(v.first, v.third) * (180 / Math.PI)
        val zAngleRaw = atan2(sqrt(v.first * v.first + v.second * v.second), v.third) * (180 / Math.PI)

        val xAngle = filterEWMA(xAngleRaw,_xPrevAngle)
        val yAngle = filterEWMA(yAngleRaw,_yPrevAngle)
        val zAngle = filterEWMA(zAngleRaw,_zPrevAngle)
        //todo check if this is correct
        _xPrevAngle= xAngle
        _yPrevAngle= yAngle
        _zPrevAngle= zAngle

        //Log.d("X_ANGLE","X-Angle=${xAngle}")
        //Log.d("Y_ANGLE","Y-Angle=${yAngle}")
        //Log.d("Z_ANGLE","Z-Angle=${zAngle}")

        return Triple(xAngle.toFloat(), yAngle.toFloat(), zAngle.toFloat())
    }
    fun calculateAnglesWithGyro(linSample:Triple<Float,Float,Float>,gyroSample:Triple<Float,Float,Float>):Triple<Float,Float,Float>{
        val xAngleRaw = atan2(linSample.second, linSample.third) * (180 / Math.PI)
        val yAngleRaw = atan2(linSample.first, linSample.third) * (180 / Math.PI)
        val zAngleRaw = atan2(sqrt(linSample.first * linSample.first + linSample.second * linSample.second), linSample.third) * (180 / Math.PI)

        val xGyroAngleRaw = atan2(gyroSample.second, gyroSample.third) * (180 / Math.PI)
        val yGyroAngleRaw = atan2(gyroSample.first, gyroSample.third) * (180 / Math.PI)
        val zGyroAngleRaw = atan2(sqrt(gyroSample.first * gyroSample.first + gyroSample.second * gyroSample.second), gyroSample.third) * (180 / Math.PI)

        val xAngle = filterSensorFusion(xAngleRaw,xGyroAngleRaw)
        val yAngle = filterSensorFusion(yAngleRaw,yGyroAngleRaw)
        val zAngle = filterSensorFusion(zAngleRaw,zGyroAngleRaw)

        return Triple(xAngle.toFloat(), yAngle.toFloat(), zAngle.toFloat())
    }

    private fun filterEWMA(value:Double,prevValue:Double):Double{
        return alpha * value + (1 - alpha) * prevValue
    }
    private fun filterSensorFusion(x1:Double,x2:Double):Double{
        return alpha * x1 + (1 - alpha) * x2
    }
}