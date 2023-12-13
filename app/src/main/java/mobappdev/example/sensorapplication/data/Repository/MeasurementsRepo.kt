package mobappdev.example.sensorapplication.data.Repository

import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import mobappdev.example.sensorapplication.data.model.Measurement
import java.io.IOException
import javax.inject.Inject

class MeasurementsRepo @Inject constructor(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val MEASUREMENTS_KEY = "listOfMeasurements"
        private const val TAG = "MeasurementsRepo"
    }

    private val gson = Gson()

    private val listOfMeasurements: List<List<Measurement>>
        get() {
            val jsonString = sharedPreferences.getString(MEASUREMENTS_KEY, null)
            return if (jsonString != null) {
                gson.fromJson(jsonString, object : TypeToken<List<List<Measurement>>>() {}.type)
            } else {
                emptyList()
            }
        }
    val listOfMeasurementsFlow: Flow<List<List<Measurement>>> = flow {
        emit(listOfMeasurements)
    }
    fun saveMeasurementsToList(measurementsToAdd: List<Measurement>) {
        // Retrieve existing measurements from SharedPreferences
        val existingMeasurementsJson = sharedPreferences.getString(MEASUREMENTS_KEY, null)
        val existingMeasurements = if (existingMeasurementsJson != null) {
            gson.fromJson<List<List<Measurement>>>(existingMeasurementsJson, object : TypeToken<List<List<Measurement>>>() {}.type)
        } else {
            emptyList()
        }

        // Append the new measurements to the existing list
        val updatedMeasurements = existingMeasurements.toMutableList().apply {
            add(measurementsToAdd)
        }

        // Convert the updated list to JSON and save it back to SharedPreferences
        val updatedMeasurementsJson = gson.toJson(updatedMeasurements)
        sharedPreferences.edit().putString(MEASUREMENTS_KEY, updatedMeasurementsJson).apply()
    }

}
