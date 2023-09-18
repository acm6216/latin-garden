package me.qingshu.latin.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.animation.ArgbEvaluatorCompat
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.math.roundToInt

/**
 * Linearly interpolate between two values
 */
fun interpolate(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}

/**
 * Linearly interpolate between two values
 */
fun interpolate(
    startValue: Int,
    endValue: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    return (startValue + fraction * (endValue - startValue)).roundToInt()
}

/**
 * Linearly interpolate between two values when the fraction is in a given range.
 */
fun interpolate(
    startValue: Float,
    endValue: Float,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Float {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return interpolate(startValue, endValue, (fraction - startFraction) / (endFraction - startFraction))
}

/**
 * Linearly interpolate between two values when the fraction is in a given range.
 */
fun interpolate(
    startValue: Int,
    endValue: Int,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return interpolate(startValue, endValue, (fraction - startFraction) / (endFraction - startFraction))
}

/**
 * Linearly interpolate between two colors when the fraction is in a given range.
 */
@ColorInt
fun interpolateArgb(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    @FloatRange(
        from = 0.0,
        fromInclusive = true,
        to = 1.0,
        toInclusive = false
    ) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float
): Int {
    if (fraction < startFraction) return startColor
    if (fraction > endFraction) return endColor

    return ArgbEvaluatorCompat.getInstance().evaluate(
        (fraction - startFraction) / (endFraction - startFraction),
        startColor,
        endColor
    )
}

/**
 * Coerce the receiving Float between inputMin and inputMax and linearly interpolate to the
 * outputMin to outputMax scale. This function is able to handle ranges which span negative and
 * positive numbers.
 *
 * This differs from [interpolate] as the input values are not required to be between 0 and 1.
 */
fun Float.normalize(
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float
): Float {
    if (this < inputMin) {
        return outputMin
    } else if (this > inputMax) {
        return outputMax
    }

    return outputMin * (1 - (this - inputMin) / (inputMax - inputMin)) +
        outputMax * ((this - inputMin) / (inputMax - inputMin))
}

@OptIn(DelicateCoroutinesApi::class)
fun View.fadeToVisibilityUnsafe(visible: Boolean, force: Boolean = false, gone: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main.immediate) { fadeToVisibility(visible, force, gone) }
}
suspend fun View.fadeToVisibility(visible: Boolean, force: Boolean = false, gone: Boolean = false) {
    if (visible) {
        fadeIn(force)
    } else {
        fadeOut(force, gone)
    }
}
suspend fun View.fadeIn(force: Boolean = false) {
    if (!isVisible) {
        alpha = 0f
        isVisible = true
    }
    animate().run {
        alpha(1f)
        if (!(isLaidOut || force) || (isVisible && alpha == 1f)) {
            duration = 0
        } else {
            duration = context.shortAnimTime.toLong()
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_slow_in)
        }
        start()
        awaitEnd()
    }
}
suspend fun View.fadeOut(force: Boolean = false, gone: Boolean = false) {
    animate().run {
        alpha(0f)
        if (!(isLaidOut || force) || (!isVisible || alpha == 0f)) {
            duration = 0
        } else {
            duration = context.shortAnimTime.toLong()
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_linear_in)
        }
        start()
        awaitEnd()
    }
    if (gone) {
        isGone = true
    } else {
        isInvisible = true
    }
}

suspend fun ViewPropertyAnimator.awaitEnd(): Unit =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }
        setListener(object : AnimatorListenerAdapter() {
            private var canceled = false

            override fun onAnimationCancel(animation: Animator) {
                canceled = true
            }

            override fun onAnimationEnd(animation: Animator) {
                setListener(null)
                if (continuation.isActive) {
                    if (canceled) {
                        continuation.cancel()
                    } else {
                        continuation.resume(Unit)
                    }
                }
            }
        })
    }