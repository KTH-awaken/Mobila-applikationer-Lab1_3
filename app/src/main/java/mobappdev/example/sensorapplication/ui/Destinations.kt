package mobappdev.example.sensorapplication.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object HomeScreen : Destinations(
        route = "home",
        title = "Record",
        icon = Icons.Outlined.Home
    )

    object Saved : Destinations(
        route = "saved",
        title = "Saved",
        icon = Icons.Outlined.Star
    )

    object Devices : Destinations(
        route = "devices",
        title = "Devices",
        icon = Icons.Outlined.Settings
    )


}