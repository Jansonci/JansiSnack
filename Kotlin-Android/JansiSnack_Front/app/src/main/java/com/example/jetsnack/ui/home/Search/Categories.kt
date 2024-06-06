package com.example.jetsnack.ui.home.Search

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.jetsnack.model.Category
import com.example.jetsnack.model.Kind
import com.example.jetsnack.model.SearchCategoryCollection
import com.example.jetsnack.ui.components.SnackImage
import com.example.jetsnack.ui.components.VerticalGrid
import com.example.jetsnack.ui.home.Feed.FeedViewModel
import com.example.jetsnack.ui.theme.JetsnackTheme
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchCategories(
    categories: List<SearchCategoryCollection>,
    onCategoryClick: (String) -> Unit,
    feedViewModel: FeedViewModel,
    ) {
    val categories1: List<Kind> = feedViewModel.categories.value?:listOf(Category(0,"", ""))
    val lifestyle: List<Kind> = feedViewModel.lifestyles.value?: listOf(Category(0,"", ""))
    val kinds = listOf(categories1, lifestyle)
    Log.i("Kinds", "Response: ${feedViewModel.categories.value?: listOf(Category(0,"bb", "bb"))[0].imageUrl}")
    LazyColumn {
        itemsIndexed(kinds) { index, collection ->
            SearchCategoryCollection(collection, index, onCategoryClick)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SearchCategoryCollection(
    collection: List<Kind>,
    index: Int,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = collection[0].javaClass.simpleName ,
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .wrapContentHeight()
        )
        VerticalGrid(Modifier.padding(horizontal = 16.dp)) {
            val gradient = when (index % 2) {
                0 -> JetsnackTheme.colors.gradient2_2
                else -> JetsnackTheme.colors.gradient2_3
            }
            collection.forEach { category ->
                SearchCategory(
                    category = category,
                    gradient = gradient,
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

private val MinImageSize = 134.dp
private val CategoryShape = RoundedCornerShape(10.dp)
private const val CategoryTextProportion = 0.55f

@Composable
private fun SearchCategory(
    category: Kind,
    gradient: List<Color>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    Layout(
        modifier = modifier
            .aspectRatio(1.45f)
            .shadow(elevation = 3.dp, shape = CategoryShape)
            .clip(CategoryShape)
            .background(Brush.horizontalGradient(gradient))
            .clickable(onClick = { onCategoryClick(category.name) }),
        content = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier
                    .padding(4.dp)
                    .padding(start = 8.dp)
            )
            SnackImage(
                imageUrl = category.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    ) { measurables, constraints ->
        // Text given a set proportion of width (which is determined by the aspect ratio)
        val textWidth = (constraints.maxWidth * CategoryTextProportion).toInt()
        val textPlaceable = measurables[0].measure(Constraints.fixedWidth(textWidth))

        // Image is sized to the larger of height of item, or a minimum value
        // i.e. may appear larger than item (but clipped to the item bounds)
        val imageSize = max(MinImageSize.roundToPx(), constraints.maxHeight)
        val imagePlaceable = measurables[1].measure(Constraints.fixed(imageSize, imageSize))
        layout(
            width = constraints.maxWidth,
            height = constraints.minHeight
        ) {
            textPlaceable.placeRelative(
                x = 0,
                y = (constraints.maxHeight - textPlaceable.height) / 2 // centered
            )
            imagePlaceable.placeRelative(
                // image is placed to end of text i.e. will overflow to the end (but be clipped)
                x = textWidth,
                y = (constraints.maxHeight - imagePlaceable.height) / 2 // centered
            )
        }
    }
}


