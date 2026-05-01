package com.example.csci215_final.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.csci215_final.ui.screens.CustomRed
import csci215final.composeapp.generated.resources.Bobby
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Res

@Composable
fun UniversalItemCard(
    title: String,
    time: String,
    description: String,
    isTask: Boolean, // True for Task, False for Reminder
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = if (isTask) Color.Blue else CustomRed
                )
            )

            Column(modifier = Modifier.weight(1f)) {
                // 1. TITLE
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    fontFamily = FontFamily(Font(Res.font.Brownist)),
                    color = if (isCompleted) Color.Gray else Color.Unspecified
                )

                // 2. TIME
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 3. DESCRIPTION
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily(Font(Res.font.Bobby)),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}