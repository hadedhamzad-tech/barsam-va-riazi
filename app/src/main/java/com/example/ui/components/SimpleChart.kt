package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary
import com.example.util.PersianUtils

@Composable
fun SimpleLineChart(
    percentages: List<Double>,
    modifier: Modifier = Modifier
) {
    if (percentages.isEmpty()) {
        Box(
            modifier = modifier.height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "هنوز آزمونی ثبت نشده است.",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
        return
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val width = size.width
            val height = size.height

            // خطوط شبکه پس‌زمینه
            val gridColor = Color.LightGray.copy(alpha = 0.4f)
            drawLine(gridColor, Offset(0f, 0f), Offset(width, 0f), strokeWidth = 1.dp.toPx())
            drawLine(gridColor, Offset(0f, height * 0.5f), Offset(width, height * 0.5f), strokeWidth = 1.dp.toPx())
            drawLine(gridColor, Offset(0f, height), Offset(width, height), strokeWidth = 1.dp.toPx())

            if (percentages.size == 1) {
                val y = height - (height * (percentages[0] / 100.0).toFloat().coerceIn(0f, 1f))
                drawCircle(color = JadePrimary, radius = 6.dp.toPx(), center = Offset(width / 2f, y))
                return@Canvas
            }

            val stepX = width / (percentages.size - 1)
            val path = Path()

            percentages.forEachIndexed { i, p ->
                // در چیدمان راست‌چین (RTL)، قدیمی‌ترین آزمون (اندیس ۰) در سمت راست و جدیدترین در سمت چپ رسم می‌شود
                val x = width - (i * stepX)
                val normalizedY = (p / 100.0).toFloat().coerceIn(0f, 1f)
                val y = height - (height * normalizedY)

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                drawCircle(
                    color = AmberPrimary,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = JadePrimary,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("اولین آزمون", fontSize = 11.sp, color = Color.Gray)
            Text(
                "آخرین: ${PersianUtils.formatPercentage(percentages.lastOrNull())}",
                fontSize = 11.sp,
                color = JadePrimary
            )
            Text("جدیدترین", fontSize = 11.sp, color = Color.Gray)
        }
    }
}
