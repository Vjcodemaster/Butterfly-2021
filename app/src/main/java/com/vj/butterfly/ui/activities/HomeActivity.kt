package com.vj.butterfly.ui.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.vj.butterfly.R
import com.vj.butterfly.databinding.ActivityHomeBinding
import com.vj.butterfly.ui.base.BaseActivity
import com.vj.butterfly.viewmodels.HomeViewModel


class HomeActivity : BaseActivity() {

    private lateinit var mHomeActivityBinding: ActivityHomeBinding
    private lateinit var mHomeViewModel: HomeViewModel
    private var fExpandAvatarSize: Float = 0F
    private var fCollapseImageSize: Float = 0F
    private var isCalculated : Boolean = false
    private var avatarAnimateStartPointY: Float = 0F
    private var avatarCollapseAnimationChangeWeight: Float = 0F
    private var verticalToolbarAvatarMargin =0F
    private var horizontalToolbarAvatarMargin: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)
        mHomeActivityBinding = DataBindingUtil.setContentView(
            this@HomeActivity,
            R.layout.activity_home
        )
        mHomeViewModel = ViewModelProvider(
            this,
        ).get(HomeViewModel::class.java)
        mHomeActivityBinding.homeVM = mHomeViewModel
        mHomeActivityBinding.lifecycleOwner = this@HomeActivity

        initHome()
        initListeners()
        //showSnackBarToast()
        //hideStatusBar()
    }

    fun initHome(){
        setSupportActionBar(mHomeActivityBinding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = resources.getString(R.string.app_name)
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        fExpandAvatarSize = resources.getDimension(R.dimen._220sdp)
        fCollapseImageSize = resources.getDimension(R.dimen._40sdp)
        horizontalToolbarAvatarMargin = resources.getDimension(R.dimen._10sdp)
    }

    fun initListeners(){
        /*mHomeActivityBinding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener{ appBarLayout, verticalOffset ->
            if (isCalculated.not()) {
                avatarAnimateStartPointY = abs((appBarLayout.height - (fExpandAvatarSize + fCollapseImageSize)) / appBarLayout.totalScrollRange)
                avatarCollapseAnimationChangeWeight = 1 / (1 - avatarAnimateStartPointY)
                verticalToolbarAvatarMargin = (mHomeActivityBinding.animToolbar.height - fCollapseImageSize) * 2
                isCalculated = true
            }
            updateViews(abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()))
        })*/
    }

    /*private fun updateViews(offset: Float) {
        var avatarAnimateStartPointY: Float = 0F
        var avatarCollapseAnimationChangeWeight: Float = 0F

        var verticalToolbarAvatarMargin =0F

        *//* Collapse avatar img*//*
        mHomeActivityBinding.sivAdvImage.apply {
            when {
                offset > avatarAnimateStartPointY -> {
                    val avatarCollapseAnimateOffset = (offset - avatarAnimateStartPointY) * avatarCollapseAnimationChangeWeight
                    val avatarSize = fExpandAvatarSize - (fExpandAvatarSize - fCollapseImageSize) * avatarCollapseAnimateOffset
                    this.layoutParams.also {
                        it.height = Math.round(avatarSize)
                        it.width = Math.round(avatarSize)
                    }
                    //invisibleTextViewWorkAround.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset)

                    this.translationX = ((mHomeActivityBinding.appbarLayout.width - horizontalToolbarAvatarMargin - avatarSize) / 2) * avatarCollapseAnimateOffset
                    this.translationY = ((mHomeActivityBinding.animToolbar.height  - verticalToolbarAvatarMargin - avatarSize ) / 2) * avatarCollapseAnimateOffset
                }
                else -> this.layoutParams.also {
                    if (it.height != fExpandAvatarSize.toInt()) {
                        it.height = fExpandAvatarSize.toInt()
                        it.width = fExpandAvatarSize.toInt()
                        this.layoutParams = it
                    }
                    translationX = 0f
                }
            }
        }
    }*/
    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            if (window.insetsController != null) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                window.insetsController!!.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }*/
}