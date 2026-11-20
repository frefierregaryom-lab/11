package com.noor.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/** مسار نجمة ثمانية الرؤوس (الخاتم السليماني) بنصف قطر خارجي R وداخلي r. */
internal fun starPath(center: Offset, outer: Float, inner: Float, points: Int = 8): Path {
    val path = Path()
    val steps = points * 2
    for (i in 0 until steps) {
        val angle = -PI / 2 + i * PI / points
        val radius = if (i % 2 == 0) outer else inner
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

/** زخرفة هندسية متكررة: نجوم ثمانية ومعيّنات بينها، بخطوط رفيعة. */
@Composable
fun IslamicPattern(
    modifier: Modifier = Modifier,
    color: Color,
    cell: Dp = 64.dp,
    strokeWidth: Dp = 0.9.dp
) {
    Canvas(modifier = modifier) {
        val cellPx = cell.toPx()
        val sw = strokeWidth.toPx()
        val cols = ceil(size.width / cellPx).toInt() + 1
        val rows = ceil(size.height / cellPx).toInt() + 1
        for (r in 0..rows) {
            for (c in 0..cols) {
                val center = Offset(c * cellPx, r * cellPx)
                drawPath(starPath(center, cellPx * 0.42f, cellPx * 0.27f), color, style = Stroke(sw))
                drawCircle(color, radius = cellPx * 0.07f, center = center, style = Stroke(sw))
                // معيّن بين النجوم
                val mid = Offset(center.x + cellPx / 2, center.y + cellPx / 2)
                drawDiamond(mid, cellPx * 0.12f, color, sw)
            }
        }
    }
}

private fun DrawScope.drawDiamond(center: Offset, half: Float, color: Color, sw: Float) {
    val p = Path().apply {
        moveTo(center.x, center.y - half)
        lineTo(center.x + half, center.y)
        lineTo(center.x, center.y + half)
        lineTo(center.x - half, center.y)
        close()
    }
    drawPath(p, color, style = Stroke(sw))
}

/** شعار النجمة الذهبية المصمتة مع حلقة داخلية، يُستخدم في الشاشة الافتتاحية والرأس. */
@Composable
fun NoorStarMark(
    modifier: Modifier = Modifier,
    color: Color,
    holeColor: Color,
    coreColor: Color = color
) {
    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2, size.height / 2)
        val outer = size.minDimension / 2
        drawPath(starPath(c, outer, outer * 0.63f), color, style = Fill)
        drawCircle(holeColor, radius = outer * 0.42f, center = c)
        drawCircle(coreColor, radius = outer * 0.17f, center = c)
    }
}
