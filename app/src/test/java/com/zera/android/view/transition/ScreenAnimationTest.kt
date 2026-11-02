package com.zera.android.view.transition

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenAnimationTest {
    @Test
    fun reduceAnimationsTurnsEveryAnimationIntoNone() {
        ScreenAnimation.entries.forEach { animation ->
            assertEquals(ScreenAnimation.None, animation.orNone(reduceAnimations = true))
        }
    }

    @Test
    fun withoutReduceAnimationsKeepsTheAnimation() {
        ScreenAnimation.entries.forEach { animation ->
            assertEquals(animation, animation.orNone(reduceAnimations = false))
        }
    }
}
