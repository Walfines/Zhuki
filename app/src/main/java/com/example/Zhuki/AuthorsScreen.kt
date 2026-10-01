package com.example.Zhuki

import android.widget.ListView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AuthorsScreen() {
    val authors = listOf(
        Author("Кузнецов Владимир", R.drawable.author_vladimir),
    )

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        factory = { context ->
            ListView(context).apply {
                adapter = AuthorAdapter(context, authors)
            }
        }
    )
}
