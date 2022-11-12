package com.debduttapanda.snappinglazyrow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debduttapanda.snappinglazyrow.snapper.ExperimentalSnapperApi
import com.debduttapanda.snappinglazyrow.snapper.rememberLazyListSnapperLayoutInfo
import com.debduttapanda.snappinglazyrow.snapper.rememberSnapperFlingBehavior
import kotlin.math.abs


/**
 *Using this composable we can snap items of a dynamic scrollable list to the center of the screen easily.
 *
 *The function takes 12 parameter,in between them 7 parameters are optional.
 *
 *@param items [List] list items of data type T to be rendered
 *
 *@param itemWidth [Dp] Each item width in Float value
 *
 *@param modifier [Modifier] Modifier of the Composable
 *
 *@param listState [LazyListState] this is the state of Lazy list, by default it is rememberLazyListState()
 *
 *@param reverseLayout [Boolean] by default it is false,If true then list will appear left side of center
 *
 *@param horizontalArrangement [Arrangement.Horizontal] Horizontal arrangement of the list items, by default it is set to Arrangement.Start when **reverseLayout** is true and Arrangement.End when **reverseLayout** is false
 *
 *@param verticalAlignment [Alignment.Vertical] Vertical alignment of the list items,by default it is Alignment.Top
 *
 *@param userScrollEnabled [Boolean] by default it is true, if false scrolling will not work
 *
 *@param  key a factory of stable and unique keys representing the item. Using the same key for multiple items in the list is not allowed. Type of the key should be saveable via Bundle on Android. If null is passed the position in the list will represent the key. When you specify the key the scroll position will be maintained based on the key, which means if you add/remove items before the current visible item the item with the given key will be kept as the first visible one.
 contentType - a factory of the content types for the item. The item compositions of the same type could be reused more efficiently. Note that null is a valid type and items of such type will be considered compatible.
 *
 *@param scaleCalculator this is a lambda function which gives  main axis offset of the item in pixels and half row width in Float value,using these two value we can calculate opacity and can return in float value by default the value is set to (1f - minOf(1f, abs(offset).toFloat() / halfRowWidth)*0.5f)
 *
 *@param onSelect  this is a lambda, in this function we will get centered item index and using that we can perform any action when it is in center
 *
 *@param item [@Composable] this is the trailing lambda,where we put our single item composable,this function gives respectively item index,the item itself and opacity of that item in Float value
 *
 */

@OptIn(ExperimentalFoundationApi::class, ExperimentalSnapperApi::class)
@Composable
fun <T> SnappingLazyRow(
    items: List<T>,
    itemWidth: Dp,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout)
            Arrangement.Start
        else
            Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    userScrollEnabled: Boolean = true,
    key: ((index: Int, item: T) -> Any)? = null,
    scaleCalculator: (Int, Float) -> Float = { offset, halfRowWidth ->
        (1f - minOf(1f, abs(offset).toFloat() / halfRowWidth) * 0.5f)
    },
    onSelect: (Int) -> Unit,
    item: @Composable (Int, T, Float) -> Unit
) {
    val layoutInfo = rememberLazyListSnapperLayoutInfo(listState)
    var apparentCurrentItem by remember {
        mutableStateOf(-1)
    }
    LaunchedEffect(listState.isScrollInProgress, apparentCurrentItem) {
        if (!listState.isScrollInProgress) {
            val d = layoutInfo.currentItem
            d?.let {
                if (apparentCurrentItem > -1) {
                    onSelect(apparentCurrentItem)
                }
            }
        }
    }
    BoxWithConstraints(
        modifier = modifier
    ) {
        val full = LocalConfiguration.current.screenWidthDp.dp
        val pad = (full - itemWidth) / 2
        val halfRowWidth = constraints.maxWidth / 2f
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            LazyRow(
                userScrollEnabled = userScrollEnabled,
                horizontalArrangement = horizontalArrangement,
                reverseLayout = reverseLayout,
                verticalAlignment = verticalAlignment,
                state = listState,
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = pad),
                flingBehavior = rememberSnapperFlingBehavior(listState)
            ) {
                itemsIndexed(
                    items,
                    key = key
                ) { i, item ->
                    val opacity by remember {
                        derivedStateOf {
                            val currentItemInfo = listState
                                .layoutInfo.visibleItemsInfo
                                .firstOrNull { it.index == i }
                                ?: return@derivedStateOf 0f
                            val offset = currentItemInfo.offset
                            scaleCalculator(offset, halfRowWidth)

                        }
                    }
                    LaunchedEffect(key1 = opacity) {
                        if (1f - opacity <= 0.1f) {
                            apparentCurrentItem = i
                        }
                    }
                    item(i, item, opacity)
                }
            }
        }
    }
}