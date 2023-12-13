package mobappdev.example.sensorapplication.ui.compnents


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import mobappdev.example.sensorapplication.ui.Destinations
import mobappdev.example.sensorapplication.ui.theme.Styles.blackBg
import mobappdev.example.sensorapplication.ui.theme.Styles.blackText
import mobappdev.example.sensorapplication.ui.theme.Styles.componentWidth
import mobappdev.example.sensorapplication.ui.theme.Styles.lightGray
import mobappdev.example.sensorapplication.ui.theme.Styles.yellowAppleWatch

//import com.example.mobila_applikationer_lab12.ui.Destinations
//import com.example.mobila_applikationer_lab12.ui.theme.Styles.componentWidth

@Composable
fun BottomBar(
    navController: NavHostController,
) {
    val screens = listOf(
        Destinations.HomeScreen, Destinations.Search,Destinations.Devices,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(blackBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        NavigationBar(
            modifier = Modifier
                .size(width =componentWidth, height = 80.dp).clip(shape = RoundedCornerShape(10.dp)).background(
                    blackBg)
                .background(
                    color = blackBg,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .width(componentWidth),
            containerColor = blackBg,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            screens.forEach { screen ->

                NavigationBarItem(
                    label = {
                        Text(text = screen.title!!)
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon!!,
                            contentDescription = "",
                            tint = if (currentRoute == screen.route) blackText else lightGray
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedTextColor = lightGray, selectedTextColor = Color.White
                    ),
                )
            }
        }
    }


}