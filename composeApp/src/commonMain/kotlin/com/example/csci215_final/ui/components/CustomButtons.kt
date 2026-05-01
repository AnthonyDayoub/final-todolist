package com.example.csci215_final.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csci215_final.ui.screens.CustomRed
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun PurpleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = CustomRed,
            contentColor = Color.White,
            disabledContainerColor = CustomRed.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (isLoading) "Saving..." else text,
            style = TextStyle(
                fontFamily = FontFamily(Font(Res.font.Brownist)),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}