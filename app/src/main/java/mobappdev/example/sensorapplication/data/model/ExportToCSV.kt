package mobappdev.example.sensorapplication.data.model

import android.Manifest
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object CSVHelper{
    fun exportToCSV(context: Context, listOfMeasurements: List<Measurement>) {
        if (listOfMeasurements.isNullOrEmpty())
            throw IOException("Measurements is empty or null")

        val fileName = "your_file.csv"

        // Permission granted, proceed with file creation
        val externalFilesDir = context.getExternalFilesDir(null)
        val filePath = File(externalFilesDir, fileName)

        try {
            FileWriter(filePath).use { fileWriter ->
                fileWriter.append("Angle, Time\n") // Adding header
                for (measurement in listOfMeasurements) {
                    fileWriter.append("${measurement.angle},${measurement.time}\n")
                }
            }
            Log.d("EXPORT", "CSV file exported successfully to: ${filePath.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
