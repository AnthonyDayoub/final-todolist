package com.example.csci215_final

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.example.csci215_final.ui.navigation.AppNavigation
import com.example.csci215_final.ui.theme.TodoAppTheme
import csci215final.composeapp.generated.resources.Res
import csci215final.composeapp.generated.resources.looseLeafPaperBKGD
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    TodoAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.looseLeafPaperBKGD),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            AppNavigation()
        }
    }
}
