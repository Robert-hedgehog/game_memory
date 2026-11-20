package com.example.game_random

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val cardFaces = mapOf(
        "card_1" to R.drawable.card_1,
        "card_2" to R.drawable.card_2,
        "card_3" to R.drawable.card_3,
        "card_4" to R.drawable.card_4,
        "card_5" to R.drawable.card_5,
        "card_6" to R.drawable.card_6,
        "card_7" to R.drawable.card_7,
        "card_8" to R.drawable.card_8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val shuffledTags = ArrayList<String>()
        for (tag in cardFaces.keys) {
            shuffledTags.add(tag)
            shuffledTags.add(tag)
        }
        shuffledTags.shuffle()

        val cardViews = ArrayList<ImageView>()
        for (i in 0 until 16) {
            cardViews.add(
                ImageView(this).apply {
                    setImageResource(R.drawable.card_back)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setPadding(12, 12, 12, 12)
                    tag = shuffledTags[i]
                    setOnClickListener(colorListener)
                }
            )
        }

        val rows = Array(4) {
            LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
                orientation = LinearLayout.HORIZONTAL
            }
        }

        for (i in cardViews.indices) {
            rows[i / 4].addView(cardViews[i])
        }
        for (row in rows) {
            rootLayout.addView(row)
        }

        setContentView(rootLayout)
    }

    private suspend fun flipCardWithDelay(card: ImageView) {
        val tagName = card.tag as? String ?: return
        val faceResId = cardFaces[tagName] ?: return

        card.isClickable = false
        card.setImageResource(faceResId)
        delay(1000)
        card.setImageResource(R.drawable.card_back)
        card.isClickable = true
    }

    private val colorListener = View.OnClickListener { view ->
        val card = view as? ImageView ?: return@OnClickListener
        lifecycleScope.launch {
            flipCardWithDelay(card)
        }
    }
}