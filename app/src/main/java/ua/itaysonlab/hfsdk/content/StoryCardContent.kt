package ua.itaysonlab.hfsdk.content

import kotlinx.parcelize.Parcelize
import ua.itaysonlab.hfsdk.FeedCategory

@Parcelize
data class StoryCardContent(
    val title: String,
    val text: String,
    val background_url: String,
    val link: String,
    val plainSnippet: String,
    val source: FeedCategory
) : BaseContent() {
    override fun describeContents(): Int {
        return 0
    }
}