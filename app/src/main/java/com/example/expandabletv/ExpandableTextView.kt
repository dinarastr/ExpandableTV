package com.example.expandabletv

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView


class ExpandableTextView : AppCompatTextView, View.OnClickListener {
    private val COLLAPSED_MAX_LINES = 3

    private lateinit var animator: ValueAnimator
    private var isCollapsing = true

//можно ли обойтись без конструкторов?
    constructor(context: Context) : super(context) {
        init()
    }

    //
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        maxLines = COLLAPSED_MAX_LINES
        setOnClickListener(this)
        initAnimator()
    }

    private fun initAnimator() {
        animator = ValueAnimator.ofInt(-1, 1).setDuration(300)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation -> updateHeight(animation.animatedValue as Int) }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                if (isCollapsed()) {
                    isCollapsing = false
                    maxLines = Int.MAX_VALUE
                } else {
                    isCollapsing = true
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (!isCollapsed() && isCollapsing) {
                    maxLines = COLLAPSED_MAX_LINES
                }
                setWrapContent()
            }

        })


    }

    private fun setWrapContent() {
        val lp: ViewGroup.LayoutParams = layoutParams
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams = lp
    }

    private fun updateHeight(animatedValue: Int) {
        val lp: ViewGroup.LayoutParams = layoutParams
        lp.height = animatedValue
        layoutParams = lp
    }


    override fun onClick(v: View?) {
        if (animator.isRunning) {
            animatorReverse()
            return
        }

        val endPosition = animateTo()
        val startPosition = height

        animator.setIntValues(startPosition, endPosition)
        animator.start()
    }

    private fun animatorReverse() {
        isCollapsing = !isCollapsing
        animator.reverse()
    }

    private fun animateTo(): Int {
        return if (isCollapsed()) {
            layout.height + getPaddingHeight()
        } else {
            layout.getLineBottom(COLLAPSED_MAX_LINES - 1) + layout.bottomPadding + getPaddingHeight()
        }
    }

    private fun getPaddingHeight(): Int {
        return compoundPaddingBottom + compoundPaddingTop


    }

    private fun isCollapsed(): Boolean {
        return Int.MAX_VALUE != maxLines
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (lineCount <= COLLAPSED_MAX_LINES) {
            setOnClickListener(null)
        } else {
            setOnClickListener(this)
        }
    }
}