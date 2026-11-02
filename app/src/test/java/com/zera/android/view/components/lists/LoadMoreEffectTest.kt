package com.zera.android.view.components.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.setZeraContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** A lista mostra 4 itens por vez, então o último visível é sempre o índice do topo + 3. */
@RunWith(RobolectricTestRunner::class)
class LoadMoreEffectTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val listState = LazyListState()
    private lateinit var scope: CoroutineScope
    private var itemCount by mutableIntStateOf(100)
    private var isLoadingMore by mutableStateOf(false)
    private var calls = 0

    private fun showList(threshold: Int = 4) {
        composeRule.setZeraContent {
            scope = rememberCoroutineScope()
            LoadMoreEffect(
                listState = listState,
                itemCount = itemCount,
                onEndReached = { calls++ },
                threshold = threshold,
            )
            LazyColumn(modifier = Modifier.height(200.dp), state = listState) {
                items(itemCount) { Row() }
                if (isLoadingMore) {
                    item { Row() }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Composable
    private fun Row() {
        Box(modifier = Modifier.fillMaxWidth().height(50.dp))
    }

    private fun scrollToTop(index: Int) {
        composeRule.runOnIdle {
            scope.launch { listState.scrollToItem(index) }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun farFromTheEndDoesNotCall() {
        showList()

        assertEquals(0, calls)
    }

    @Test
    fun justBeforeTheThresholdDoesNotCall() {
        showList()

        scrollToTop(92)

        assertEquals(0, calls)
    }

    @Test
    fun reachingTheThresholdCallsOnce() {
        showList()

        scrollToTop(93)

        assertEquals(1, calls)
    }

    @Test
    fun movingNearTheEndDoesNotCallAgain() {
        showList()

        scrollToTop(93)
        scrollToTop(95)
        scrollToTop(96)

        assertEquals(1, calls)
    }

    @Test
    fun newPageWithTheUserStillNearTheEndCallsAgain() {
        showList()
        scrollToTop(96)

        composeRule.runOnIdle { itemCount = 102 }
        composeRule.waitForIdle()

        assertEquals(2, calls)
    }

    @Test
    fun newPageBeyondTheThresholdWaitsForTheScroll() {
        showList()
        scrollToTop(96)

        composeRule.runOnIdle { itemCount = 120 }
        composeRule.waitForIdle()
        assertEquals(1, calls)

        scrollToTop(113)
        assertEquals(2, calls)
    }

    @Test
    fun failedPageDoesNotCallAgain() {
        showList()
        scrollToTop(96)

        composeRule.runOnIdle { isLoadingMore = true }
        composeRule.waitForIdle()
        composeRule.runOnIdle { isLoadingMore = false }
        composeRule.waitForIdle()

        assertEquals(1, calls)
    }

    @Test
    fun goingUpAndBackDownCallsAgain() {
        showList()
        scrollToTop(96)

        scrollToTop(0)
        scrollToTop(96)

        assertEquals(2, calls)
    }

    @Test
    fun shortListCallsByItself() {
        itemCount = 3

        showList()

        assertEquals(1, calls)
    }

    @Test
    fun emptyListDoesNotCall() {
        itemCount = 0

        showList()

        assertEquals(0, calls)
    }

    @Test
    fun respectsACustomThreshold() {
        showList(threshold = 1)

        scrollToTop(95)
        assertEquals(0, calls)

        scrollToTop(96)
        assertEquals(1, calls)
    }
}
