package com.example.jetsnack.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.States
import com.example.jetsnack.model.Filter
import com.example.jetsnack.ui.theme.JetnewsShapes
import com.example.jetsnack.ui.theme.JetsnackTheme
import com.example.jetsnack.ui.theme.Shapes

@Composable
fun FilterBar(
    filters: List<Filter>,
    onShowFilters: () -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = "restoreScrollPosition") {
        listState.scrollToItem(States.filterScrollIndex, States.filterScrollOffset)
    }

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
        modifier = Modifier.heightIn(min = 46.dp),
        state = listState
    ) {
        item {
            IconButton(onClick = onShowFilters) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = stringResource(R.string.label_filters),
                    modifier = Modifier.diagonalGradientBorder(
                        colors = JetsnackTheme.colors.interactiveSecondary,
                        shape = CircleShape
                    )
                )
            }
        }
        items(filters) { filter ->
            FilterChip(filter = filter, shape = Shapes.small)
        }
    }

    DisposableEffect(key1 = "saveScrollPosition") {
        onDispose {
            States.filterScrollIndex = listState.firstVisibleItemIndex
            States.filterScrollOffset = listState.firstVisibleItemScrollOffset
        }
    }
}



@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = JetnewsShapes.small
) {
    val (selected, setSelected) = filter.enabled
    val backgroundColor by animateColorAsState(
        if (selected) JetsnackTheme.colors.brandSecondary else JetsnackTheme.colors.uiBackground,
        label = "background color"
    )
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor by animateColorAsState(
        if (selected) Color.Black else JetsnackTheme.colors.textSecondary,
        label = "text color"
    )

    JetsnackSurface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        val pressed by interactionSource.collectIsPressedAsState()
        val backgroundPressed =
            if (pressed) {
                Modifier.offsetGradientBackground(
                    JetsnackTheme.colors.interactiveSecondary,
                    200f,
                    0f
                )
            } else {
                Modifier.background(Color.Transparent)
            }
        Box(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = setSelected,
                    interactionSource = interactionSource,
                    indication = null
                )
                .then(backgroundPressed)
                .then(border),
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterDisabledPreview() {
    JetsnackTheme {
        FilterChip(Filter(name = "Demo", enabled = false), Modifier.padding(4.dp))
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterEnabledPreview() {
    JetsnackTheme {
        FilterChip(Filter(name = "Demo", enabled = true))
    }
}
