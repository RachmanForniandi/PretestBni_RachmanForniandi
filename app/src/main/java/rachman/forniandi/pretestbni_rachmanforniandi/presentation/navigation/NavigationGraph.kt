package rachman.forniandi.pretestbni_rachmanforniandi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.home.HomeScreen
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.receipt.ReceiptScreen
import rachman.forniandi.pretestbni_rachmanforniandi.presentation.transaction.GatheringScreen

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable(
            "transaction/{type}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "TRANSFER"
            GatheringScreen(
                navController = navController,
                transactionType = type
            )
        }

        composable(
            "receipt/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            ReceiptScreen(
                navController = navController,
                transactionId = transactionId
            )
        }
    }
}