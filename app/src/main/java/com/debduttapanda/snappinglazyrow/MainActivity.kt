package com.debduttapanda.snappinglazyrow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.debduttapanda.snappinglazyrow.ui.theme.SnappingLazyRowTheme

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
                        SnappingLazyRow(
                            items = MutableList(20){it},
                            itemWidth = 68.dp,
                            onSelect = {}
                        ) {index,item,scale->
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .scale(scale)
                                    .alpha(scale)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black
                                    ),
                                contentAlignment = Alignment.Center
                            ){
                                Text("$item")
                            }
                        }
                    }
                }
            }
        }
    }
}