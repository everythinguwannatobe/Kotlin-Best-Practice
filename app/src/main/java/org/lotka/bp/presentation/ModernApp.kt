package org.lotka.bp.presentation

import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.samples.crane.home.CraneHome
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.owl.insights.InsightsScreen
import com.example.owl.insights.InsightsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.lotka.bp.datastore.SettingsDataStore
import org.lotka.bp.presentation.navigation.BottomNavigationBar
import org.lotka.bp.presentation.navigation.NavigationItem
import org.lotka.bp.presentation.theme.navigationDarkThemeBackGroundColor
import org.lotka.bp.presentation.theme.navigationDarkThemeItemColor
import org.lotka.bp.presentation.theme.navigationLightThemeBackGroundColor
import org.lotka.bp.presentation.theme.navigationLightThemeItemColor
import org.lotka.bp.presentation.ui.dashboard.DashboardScreen
import org.lotka.bp.presentation.ui.dashboard.DashboardViewModel
import org.lotka.bp.presentation.ui.explore.ExploreScreen
import org.lotka.bp.presentation.ui.explore.ExploreViewModel
import org.lotka.bp.presentation.ui.profile.ProfileScreen
import org.lotka.bp.presentation.ui.profile.ProfileViewModel
import org.lotka.bp.presentation.ui.recipe.RecipeDetailScreen
import org.lotka.bp.presentation.ui.recipe.RecipeViewModel
import org.lotka.bp.presentation.ui.recipe_list.RecipeListScreen
import org.lotka.bp.presentation.ui.recipe_list.RecipeListViewModel
import org.lotka.bp.presentation.ui.signinsignup.SignInRoute
import org.lotka.bp.presentation.ui.signinsignup.SignUpRoute
import org.lotka.bp.presentation.ui.signinsignup.WelcomeRoute
import org.lotka.bp.presentation.ui.survey.SurveyRoute
import org.lotka.bp.presentation.util.ConnectivityManager

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalCoroutinesApi::class,
    ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class
)

@Composable
fun ModernApp(
    activity: MainActivity,
    connectivityManager: ConnectivityManager,
    settingsDataStore: SettingsDataStore
) {

    val widthSizeClass = calculateWindowSizeClass(activity).widthSizeClass

    //todo use compose new method to set bottom bar color

    activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    if (settingsDataStore.isDark.value){
        activity.window.navigationBarColor = navigationDarkThemeBackGroundColor.toArgb()
        ViewCompat.getWindowInsetsController(activity.window.decorView)?.isAppearanceLightNavigationBars = false
    }else{
        activity.window.navigationBarColor = navigationLightThemeBackGroundColor.toArgb()
        ViewCompat.getWindowInsetsController(activity.window.decorView)?.isAppearanceLightNavigationBars = true
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(
                    navController = navController,
                    isDarkTheme = settingsDataStore.isDark.value,
                    onToggleTheme = settingsDataStore::toggleTheme,
                    currentRoute = currentRoute ,
                )
            }

        },
        content = { scaffoldPadding ->

            NavHost(navController, startDestination = NavigationItem.Dashboard.route) {

                //welcome
                composable(
                    route = NavigationItem.Welcome.route) {
                    WelcomeRoute(
                        onNavigateToSignIn = {
                            navController.navigate("signin/$it")
                        },
                        onNavigateToSignUp = {
                            navController.navigate("signup/$it")
                        },
                        onSignInAsGuest = {
                            navController.navigate(NavigationItem.Survey.route)
                        },
                    )
                }


                //survey
                composable(
                    route = NavigationItem.Survey.route
                ) { navBackStackEntry ->
                    SurveyRoute(
                        onNavUp = {},
                        onSurveyComplete = {navController.navigate(NavigationItem.Dashboard.route)},
                        fragmentManager = (activity as AppCompatActivity).supportFragmentManager
                    )
                }


                //signin
                composable(NavigationItem.SignIn.route) {
                    val startingEmail = it.arguments?.getString("email")
                    SignInRoute(
                        email = startingEmail,
                        onSignInSubmitted = {
                            navController.navigate(NavigationItem.Survey.route)
                        },
                        onSignInAsGuest = {
                            navController.navigate(NavigationItem.Survey.route)
                        },
                        onNavUp = navController::navigateUp,
                    )
                }

                //signup
                composable(NavigationItem.SignUp.route) {
                    val startingEmail = it.arguments?.getString("email")
                    SignUpRoute(
                        email = startingEmail,
                        onSignUpSubmitted = {
                            navController.navigate(NavigationItem.Survey.route)
                        },
                        onSignInAsGuest = {
                            navController.navigate(NavigationItem.Survey.route)
                        },
                        onNavUp = navController::navigateUp,
                    )
                }

                //dashboard
                composable(
                    route = NavigationItem.Dashboard.route
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: DashboardViewModel =
                        viewModel(activity, "DashboardViewModel", factory)

                    DashboardScreen(
                        isDarkTheme = settingsDataStore.isDark.value,
                        isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                        onToggleTheme = settingsDataStore::toggleTheme,
                        onNavigateToRecipeDetailScreen = navController::navigate,
                        viewModel = viewModel,
                        scaffoldPadding= scaffoldPadding
                    )
                }

                //insights
                composable(
                    route = NavigationItem.Insights.route
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: InsightsViewModel =
                        viewModel(activity, "InsightsViewModel", factory)

                    InsightsScreen(
                        isDarkTheme = settingsDataStore.isDark.value,
                        isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                        onToggleTheme = settingsDataStore::toggleTheme,
                        onNavigateToRecipeDetailScreen = navController::navigate,
                        viewModel = viewModel,
                        scaffoldPadding=scaffoldPadding
                    )
                }



                //list
                composable(
                    route = NavigationItem.List.route
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: RecipeListViewModel =
                        viewModel(activity, "RecipeListViewModel", factory)

                    RecipeListScreen(
                        isDarkTheme = settingsDataStore.isDark.value,
                        isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                        onToggleTheme = settingsDataStore::toggleTheme,
                        onNavigateToRecipeDetailScreen = navController::navigate,
                        viewModel = viewModel,
                        scaffoldPadding=scaffoldPadding
                    )
                }

                composable(
                    route = NavigationItem.RecipeDetail.route + "/{recipeId}",
                    arguments = listOf(navArgument("recipeId") {
                        type = NavType.IntType
                    })
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: RecipeViewModel =
                        viewModel(activity, "RecipeDetailViewModel", factory)
                    RecipeDetailScreen(
                        isDarkTheme = settingsDataStore.isDark.value,
                        isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                        recipeId = navBackStackEntry.arguments?.getInt("recipeId"),
                        viewModel = viewModel,
                        scaffoldPadding = scaffoldPadding

                    )
                }







                //Explore
                composable(
                    route = NavigationItem.Explore.route
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: ExploreViewModel =
                        viewModel(activity, "ExploreViewModel", factory)
                    ExploreScreen(
                        isDarkTheme = settingsDataStore.isDark.value,
                        isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                        onToggleTheme = settingsDataStore::toggleTheme,
                        onNavigateToRecipeDetailScreen = navController::navigate,
                        viewModel = viewModel,
                        scaffoldPadding = scaffoldPadding
                    )
                }



                //Profile
                composable(
                    route = NavigationItem.Profile.route
                ) { navBackStackEntry ->
                    val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
                    val viewModel: ProfileViewModel =
                        viewModel(activity, "ProfileViewModel", factory)
                    ProfileScreen(
                        isDarkTheme = settingsDataStore.isDark.value,
                        isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                        onToggleTheme = settingsDataStore::toggleTheme,
                        onNavigateToRecipeDetailScreen = navController::navigate,
                        viewModel = viewModel,
                        scaffoldPadding= scaffoldPadding
                    )
                }


                composable(NavigationItem.Dashboard.route) {
                    val mainViewModel = hiltViewModel<MainViewModel>()
                    CraneHome(
                        widthSize = widthSizeClass,
                        onExploreItemClicked = {  },
                        onDateSelectionClicked = {},
                        viewModel = mainViewModel
                    )

                }



            }

        },
    )

}

private fun shouldShowBottomBar(route: String?): Boolean {
    val bottomBarRoutes = setOf(
        NavigationItem.Dashboard.route,
        NavigationItem.Insights.route,
        NavigationItem.List.route,
        NavigationItem.Explore.route,
        NavigationItem.Profile.route
    )
    return route in bottomBarRoutes
}


