package com.hashem.mousavi.heartgraph

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun Heart(
    modifier: Modifier,
    color: Color = Color.Red,
) {

    var width by remember {
        mutableStateOf(0f)
    }
    var height by remember {
        mutableStateOf(0f)
    }


    val points = remember {
        mutableStateListOf<Offset>()
    }

    val path = remember {
        Path()
    }

    var fill by remember {
        mutableStateOf(false)
    }

    var radius by remember {
        mutableStateOf(0f)
    }

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse)
    )

    val animatable = remember {
        Animatable(initialValue = 1f)
    }


    LaunchedEffect(Unit) {
        var angle = 0f
        val step = (PI / 720).toFloat()
        val normalizationCoefficient = (width / 2 - 20) / 16f
        val totalIterations = (PI / step).toInt()

        while (angle <= 2 * PI) {
            val x1 = 16 * sin(angle).pow(3) * normalizationCoefficient
            val y1 =
                -(13 * cos(angle) - 5 * cos(2 * angle) - 2 * cos(3 * angle) - cos(4 * angle)) * normalizationCoefficient
            angle += step
            val x2 = 16 * sin(angle).pow(3) * normalizationCoefficient
            val y2 =
                -(13 * cos(angle) - 5 * cos(2 * angle) - 2 * cos(3 * angle) - cos(4 * angle)) * normalizationCoefficient

            points.add(Offset(x = x1, y = y1))
            points.add(Offset(x = x2, y = y2))

            delay(5000L / totalIterations)
        }

        fill = true
        radius = 0f
        while (radius < width / 2 + 20) {
            radius += 10f
            delay(10)
        }

        animatable.animateTo(
            targetValue = 0.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400),
                repeatMode = RepeatMode.Reverse
            )
        )

    }

    Canvas(
        modifier = modifier
    ) {
        width = this.size.width
        height = this.size.height

        translate(left = width / 2, top = height / 2) {
            drawLine(
                color = Color.Gray,
                start = Offset(x = 0f, y = -height / 2),
                end = Offset(x = 0f, y = height / 2)
            )

            drawLine(
                color = Color.Gray,
                start = Offset(x = -width / 2, y = 0f),
                end = Offset(x = width / 2, y = 0f)
            )

            scale(animatable.value, pivot = Offset.Zero) {
                points.forEachIndexed { index, offset ->
                    if (index == 0) {
                        path.reset()
                        path.moveTo(x = offset.x, y = offset.y)
                    }
                    if (index < points.size - 1) {
                        path.lineTo(
                            x = offset.x,
                            y = offset.y
                        )
                    }
                }

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 10f)
                )

                clipPath(path = path, clipOp = ClipOp.Intersect) {
                    if (fill) {
                        drawCircle(
                            color = color,
                            radius = radius,
                            center = Offset.Zero
                        )
                    }
                }
            }

        }

    }

}