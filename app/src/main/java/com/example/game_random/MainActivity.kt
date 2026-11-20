package com.example.game_random

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    private var firstCard: ImageView? = null
    private var isBusy = false
    private var matchedPairsCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startNewGame()
    }

    private fun startNewGame() {
        matchedPairsCount = 0
        firstCard = null
        isBusy = false

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
                    setOnClickListener(cardClickListener)
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

    private val cardClickListener = View.OnClickListener { view ->
        val clickedCard = view as? ImageView ?: return@OnClickListener

        if (isBusy || clickedCard == firstCard) {
            return@OnClickListener
        }

        val tag = clickedCard.tag as? String ?: return@OnClickListener
        val faceResId = cardFaces[tag] ?: return@OnClickListener

        clickedCard.setImageResource(faceResId)

        if (firstCard == null) {
            firstCard = clickedCard
        } else {
            val secondCard = clickedCard
            lifecycleScope.launch {
                checkPair(firstCard!!, secondCard)
            }
        }
    }

    private suspend fun checkPair(card1: ImageView, card2: ImageView) {
        isBusy = true

        val tag1 = card1.tag as? String
        val tag2 = card2.tag as? String

        if (tag1 == tag2) {
            delay(500)
            card1.visibility = View.INVISIBLE
            card2.visibility = View.INVISIBLE

            matchedPairsCount++

            if (matchedPairsCount == 8) {
                showWinDialog()
            }
        } else {
            delay(1000)
            card1.setImageResource(R.drawable.card_back)
            card2.setImageResource(R.drawable.card_back)
        }

        firstCard = null
        isBusy = false
    }

    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle("Победа!")
            .setMessage("Поздравляем! Вы нашли все пары карт!")
            .setCancelable(false)
            .setPositiveButton("Сыграть снова") { _, _ ->
                startNewGame()
            }
            .setNegativeButton("Выход") { _, _ ->
                finish()
            }
            .show()
    }
}