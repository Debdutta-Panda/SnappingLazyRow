package com.debduttapanda.snappinglazyrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debduttapanda.snappinglazyrow.ui.theme.SnappingLazyRowTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnappingLazyRowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ){
                        var selected by remember{
                            mutableStateOf(0)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                "$selected",
                                fontSize = 24.sp
                            )
                            val listState = rememberLazyListState()
                            val scope = rememberCoroutineScope()
                            SnappingLazyRow(
                                scaleCalculator = { offset, halfRowWidth ->
                                    (1f - minOf(1f, abs(offset).toFloat() / halfRowWidth)*0.5f )
                                },
                                key = {index, item ->
                                      item//or any id
                                },
                                items = MutableList(20){it},
                                itemWidth = 68.dp,
                                onSelect = {
                                    selected = it
                                },
                                listState = listState
                            ) {index,item,scale->
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clickable {
                                            scope.launch {
                                                listState.animateScrollToItem(index)
                                            }
                                        }
                                        .scale(scale.coerceAtLeast(0.8f))
                                        .alpha(scale.coerceAtLeast(0.8f))
                                        .border(
                                            width = 3.dp,
                                            color = Color.Black
                                        ),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        "$item"
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}