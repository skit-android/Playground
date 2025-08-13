package com.example.playground

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playground.ui.theme.PlaygroundTheme
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImagePickerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ImagePickerScreen(modifier: Modifier = Modifier) {
    var selectedMedia by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                TedImagePicker.with(context)
                    .mediaType(MediaType.IMAGE_AND_VIDEO)
                    .showCameraTile(true)
                    .buttonText("선택")
                    .errorListener { message -> }
                    .selectedUri(selectedMedia)
                    .max(20, "최대 20개까지 선택할 수 있습니다.")
                    .startMultiImage { uriList ->
                        selectedMedia = uriList
                    }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("미디어 선택 (이미지 + 동영상)")
        }

        if (selectedMedia.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(selectedMedia) { index, mediaUri ->
                    MediaItem(
                        uri = mediaUri,
                        index = index + 1
                    )
                }
            }
        } else {
            Text(
                text = "선택된 미디어가 없습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MediaItem(
    uri: Uri,
    index: Int,
    modifier: Modifier = Modifier
) {
    val isVideo = remember(uri) {
        val mimeType = uri.toString()
        mimeType.contains(".mp4") || mimeType.contains(".mov") || 
        mimeType.contains(".3gp") || mimeType.contains(".webm") ||
        mimeType.contains("video")
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = if (isVideo) "선택된 동영상 $index" else "선택된 이미지 $index",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 동영상인 경우 재생 아이콘 표시
        if (isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "동영상 재생",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // 순서 번호 표시
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "$index",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImagePickerScreenPreview() {
    PlaygroundTheme {
        ImagePickerScreen()
    }
}