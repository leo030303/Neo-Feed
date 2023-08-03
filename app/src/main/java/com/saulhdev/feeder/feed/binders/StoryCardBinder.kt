package com.saulhdev.feeder.feed.binders

import android.os.Build
import android.text.Html
import android.util.SparseIntArray
import android.view.View
import coil.load
import com.google.android.material.button.MaterialButton
import com.saulhdev.feeder.ComposeActivity
import com.saulhdev.feeder.R
import com.saulhdev.feeder.databinding.FeedCardStoryLargeBinding
import com.saulhdev.feeder.db.FeedRepository
import com.saulhdev.feeder.preference.FeedPreferences
import com.saulhdev.feeder.theme.Theming
import com.saulhdev.feeder.utils.RelativeTimeHelper
import com.saulhdev.feeder.utils.isDark
import com.saulhdev.feeder.utils.launchView
import com.saulhdev.feeder.utils.urlEncode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.StoryCardContent

object StoryCardBinder : FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        val context = view.context
        val content = item.content as StoryCardContent
        val binding = FeedCardStoryLargeBinding.bind(view)
        val prefs = FeedPreferences(context)
        val repository = FeedRepository(context)
        var bookmarked = item.bookmarked
        if (content.source.isSocial){
            binding.cardStory.setBackgroundColor(context.getColor(R.color.socialCard))
            binding.storyTitle.text = content.plainSnippet
        } else {
            binding.storyTitle.text = content.title
        }
        binding.storySource.text = content.source.title
        binding.storyDate.text =
            RelativeTimeHelper.getDateFormattedRelative(view.context, (item.time / 1000) - 1000)

        if (content.text.isEmpty()) {
            binding.storySummary.visibility = View.GONE
        } else {
            binding.storySummary.text = Html.fromHtml(content.text, 0).toString()
        }

        if (
            content.background_url.isEmpty() ||
            content.background_url == "null" ||
            content.background_url.contains(".rss")
        ) {
            binding.storyPic.visibility = View.GONE
        } else {
            binding.storyPic.visibility = View.VISIBLE
            binding.storyPic.load(content.background_url) {
                crossfade(true)
                crossfade(500)
            }
        }
        if (prefs.showBookmarkButton.onGetValue()){
            binding.saveButton.visibility = View.GONE
        }
        else {
            binding.saveButton.visibility = View.VISIBLE
        }
        binding.saveButton.updateBookmark(bookmarked)
        if(bookmarked){
            binding.cardStory.setBackgroundColor(context.getColor(R.color.colorCardM3Bookmarked))
        }else{
            binding.cardStory.setBackgroundColor(context.getColor(R.color.colorCardM3Primary))
        }

        binding.saveButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                repository.bookmarkArticle(item.id, !bookmarked)
                binding.saveButton.updateBookmark(!bookmarked)
                bookmarked = !bookmarked
                if(bookmarked){
                    binding.cardStory.setBackgroundColor(context.getColor(R.color.colorCardM3Bookmarked))
                }else{
                    binding.cardStory.setBackgroundColor(context.getColor(R.color.colorCardM3Primary))
                }
            }
        }
        binding.root.setOnLongClickListener() {
            CoroutineScope(Dispatchers.Main).launch {
                repository.bookmarkArticle(item.id, !bookmarked)
                binding.saveButton.updateBookmark(!bookmarked)
                bookmarked = !bookmarked
                if(bookmarked){
                    binding.cardStory.setBackgroundColor(context.getColor(R.color.colorCardM3Bookmarked))
                }else{
                    binding.cardStory.setBackgroundColor(context.getColor(R.color.colorCardM3Primary))
                }
            }
            return@setOnLongClickListener true
        }

        binding.root.setOnClickListener {
            if (prefs.openInBrowser.onGetValue()) {
                view.context.launchView(content.link)
            } else {
                val scope = CoroutineScope(Dispatchers.Main)

                scope.launch {
                    if (prefs.offlineReader.onGetValue()) {
                        view.context.startActivity(
                            ComposeActivity.createIntent(
                                view.context,
                                "article_page/${item.id}/"
                            )
                        )
                    } else {
                        view.context.startActivity(
                            ComposeActivity.createIntent(
                                view.context,
                                "web_view/${content.link.urlEncode()}/"
                            )
                        )
                    }
                }
            }
        }

        theme ?: return
        //binding.cardStory.setBackgroundColor(theme.get(Theming.Colors.CARD_BG.ordinal))
        val themeCard = if (theme.get(Theming.Colors.CARD_BG.ordinal)
                .isDark()
        ) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        binding.storyTitle.setTextColor(context.getColor(R.color.colorCardM3TextPrimary))
        binding.storySource.setTextColor(context.getColor(R.color.colorCardM3TextSecondary))
        binding.storyDate.setTextColor(context.getColor(R.color.colorCardM3TextSecondary))
        binding.storySummary.setTextColor(context.getColor(R.color.colorCardM3TextSecondary))
    }
    private fun MaterialButton.updateBookmark(bookmarked: Boolean) = if (bookmarked) {
        text = context.getString(R.string.bookmark_remove)
        setIconResource(R.drawable.ic_trash_simple)
        if (Build.VERSION.SDK_INT > 30) {
            setBackgroundColor(context.getColor(com.google.android.material.R.color.m3_sys_color_secondary_fixed))
        } else {
            setBackgroundColor(context.getColor(R.color.colorSecondary))
        }
    } else {
        text = context.getString(R.string.bookmark)
        setIconResource(R.drawable.ic_archive_tray)
        if (Build.VERSION.SDK_INT > 30) {
            setBackgroundColor(context.getColor(com.google.android.material.R.color.m3_sys_color_dynamic_primary_fixed))
        } else {
            setBackgroundColor(context.getColor(R.color.textColorPrimary))
        }
    }

}