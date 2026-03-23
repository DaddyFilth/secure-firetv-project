package com.secure.firetv

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide

class CardPresenter : Presenter() {
    private var defaultCardImage: android.graphics.drawable.Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        if (defaultCardImage == null) {
            defaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.ic_launcher)
        }

        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(313, 176) // Standard Leanback dimensions
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = movie.title
        cardView.contentText = "Trending"

        Glide.with(cardView.context)
            .load(movie.poster)
            .centerCrop()
            .error(defaultCardImage)
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}
