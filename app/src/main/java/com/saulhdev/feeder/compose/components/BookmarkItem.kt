/*
 * This file is part of Neo Feed
 * Copyright (c) 2022   Saul Henriquez <henriquez.saul@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.saulhdev.feeder.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.saulhdev.feeder.R
import com.saulhdev.feeder.db.Feed
import com.saulhdev.feeder.db.FeedArticle

@Composable
fun BookmarkItem(
    modifier: Modifier = Modifier,
    article: FeedArticle,
    feed: Feed,
    onClickAction: (FeedArticle) -> Unit = {},
    onRemoveAction: (FeedArticle) -> Unit = {},
) {
    val context = LocalContext.current
    val isPinned by remember(article.pinned) {
        mutableStateOf(article.pinned)
    }
    val backgroundColor by animateColorAsState(
        targetValue = if (isPinned) MaterialTheme.colorScheme.surfaceContainerHighest
        else MaterialTheme.colorScheme.surfaceContainer
    )

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = true, onClick = {
                onClickAction(article)
            }),
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
        ),
        leadingContent = {
            AsyncImage(
                modifier = Modifier.size(24.dp),
                model = feed.feedImage.toString(),
                contentDescription = feed.title,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            )
        },
        overlineContent = {
            Text(
                text = feed.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        headlineContent = {
            Text(
                text = article.plainTitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = article.plainSnippet,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            IconButton(
                modifier = Modifier.size(54.dp),
                onClick = { onRemoveAction(article) },
                colors = IconButtonColors(Color.Gray, Color.LightGray, Color.Blue, Color.Cyan)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.delete_feed),
                )
            }
        }
    )
}
