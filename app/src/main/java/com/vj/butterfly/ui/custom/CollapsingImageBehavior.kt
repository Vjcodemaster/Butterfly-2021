package com.vj.butterfly.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.vj.butterfly.R

class CollapsingImageBehavior : CoordinatorLayout.Behavior<View?> {
    private var mTargetId = 0
    private var mView: IntArray? = null
    private lateinit var mTarget: IntArray

    constructor(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CollapsingImageBehavior)
            mTargetId = a.getResourceId(R.styleable.CollapsingImageBehavior_collapsedTarget, 0)
            a.recycle()
        }
        check(mTargetId != 0) { "collapsedTarget attribute not specified on view for behavior" }
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        setup(parent, child)
        val appBarLayout: AppBarLayout = dependency as AppBarLayout
        val range: Int = appBarLayout.getTotalScrollRange()
        val factor: Float = -appBarLayout.getY() / range
        val left = mView!![X] + (factor * (mTarget[X] - mView!![X])).toInt()
        val top = mView!![Y] + (factor * (mTarget[Y] - mView!![Y])).toInt()
        val width = mView!![WIDTH] + (factor * (mTarget[WIDTH] - mView!![WIDTH])).toInt()
        val height = mView!![HEIGHT] + (factor * (mTarget[HEIGHT] - mView!![HEIGHT])).toInt()
        val lp: CoordinatorLayout.LayoutParams =
            child.getLayoutParams() as CoordinatorLayout.LayoutParams
        lp.width = width
        lp.height = height
        child.setLayoutParams(lp)
        child.setX(left.toFloat())
        child.setY(top.toFloat())
        return true
    }

    private fun setup(parent: CoordinatorLayout, child: View) {
        if (mView != null) return
        mView = IntArray(4)
        mTarget = IntArray(4)
        mView!![X] = child.getX().toInt()
        mView!![Y] = child.getY().toInt()
        mView!![WIDTH] = child.getWidth()
        mView!![HEIGHT] = child.getHeight()
        val target: View = parent.findViewById(mTargetId)
            ?: throw IllegalStateException("target view not found")
        mTarget[WIDTH] += target.getWidth()
        mTarget[HEIGHT] += target.getHeight()
        var view: View = target
        while (view !== parent) {
            mTarget[X] += view.getX().toInt()
            mTarget[Y] += view.getY().toInt()
            view = view.getParent() as View
        }
    }

    companion object {
        private const val X = 0
        private const val Y = 1
        private const val WIDTH = 2
        private const val HEIGHT = 3
    }
}