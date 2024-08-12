package com.hslim.android.refreshablewebview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.webkit.WebView
import kotlin.time.DurationUnit

class RefreshableWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr:Int = 0
): WebView(context, attrs, defStyleAttr){
    private lateinit var vibrator: Vibrator
    private val animationHandler = Handler(Looper.getMainLooper())
    private var isRefreshing = false
    private var _isRefreshEnabled: Boolean = true

    fun setRefreshSetting(settings: RefreshSetting){
        initVibrate()
        initIndicatorView(settings)
        initRefreshListener(settings)
    }

    fun setRefreshEnabled(enable: Boolean) {
        _isRefreshEnabled = enable

    }

    /**
     * indicator 가 보여야할 조건
     * - 상단 여부 : scrollY == 0
     * - 하단 스와이프 동작 여부 : [MotionEvent.ACTION_DOWN]의 [MotionEvent.getY]보다 [MotionEvent.ACTION_MOVE]의 [MotionEvent.getY]가 클 때,
     * - 스와이프 동작으로 변화된 값을 indicator가 보일 수 있는 최대치의 범위로 환산 해야함. [swipeDistanceToIndicatorY]
     *
     * refresh 조건
     * -
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initRefreshListener(settings: RefreshSetting) {
        val refreshThreshold = dpToPx(context, settings.refreshThresholdDp.toFloat())
        var startY = 0f

        this.setOnTouchListener { view, event ->
            if (_isRefreshEnabled.not()) {
                settings.indicatorView.visibility = View.GONE
                return@setOnTouchListener false
            }

            if (isRefreshing) {
                return@setOnTouchListener false
            }

            when(event.action){
                MotionEvent.ACTION_DOWN -> startY = event.y
                MotionEvent.ACTION_MOVE -> {
                    if (isWebViewTopAndDownScroll(event, startY)){
                        settings.indicatorView.visibility = View.VISIBLE
                        settings.indicatorView.y = swipeDistanceToIndicatorY(
                            distance = event.y - startY,
                            indicatorMaxY = refreshThreshold.toFloat(),
                        )
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isWebViewTop() && isOverThreshold(event, startY, refreshThreshold)){
                        val refreshingTime = settings.refreshingTime.toLong(DurationUnit.MILLISECONDS)

                        isRefreshing = true
                        animationHandler.postDelayed({
                            settings.listener.onRefresh(this)
                            executeVibrate(settings)
                            indicatorHideAnimation(500, settings) { isRefreshing = false }
                        }, refreshingTime)
                    }else{
                        indicatorHideAnimation(200, settings)
                    }
                }
                else -> {}
            }

            false
        }
    }

    private fun indicatorHideAnimation(
        durationMilli: Long,
        settings: RefreshSetting,
        endAction: (() -> Unit)? = null,
    ) {
        settings.indicatorView
            .animate()
            .translationY(-settings.indicatorView.height.toFloat())
            .setDuration(durationMilli)
            .withEndAction {
                endAction?.invoke()
            }
            .start()
    }

    private fun executeVibrate(settings: RefreshSetting) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE,))
    }

    /**
     * 웹뷰의 상단이면서 하단으로 스와이프 했는가?
     */
    private fun isWebViewTopAndDownScroll(event: MotionEvent, startY: Float) : Boolean = (isWebViewTop() && event.y > startY)

    private fun isWebViewTop() : Boolean = scrollY == 0

    /**
     * 스와이프 동작으로 이동한 포인터의 거리를 indicator ui의 y값을 환산
     */
    private fun swipeDistanceToIndicatorY(distance: Float, indicatorMaxY: Float): Float = (distance * indicatorMaxY / this.height)

    private fun isOverThreshold(event: MotionEvent, startY: Float, refreshThreshold: Int): Boolean = (event.y - startY) > refreshThreshold

    private fun ViewGroup.initIndicatorView(settings: RefreshSetting) {
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        settings.indicatorView.visibility = View.GONE
        this.addView(settings.indicatorView, 0, layoutParams)
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return (dp * metrics.density + 0.5f).toInt()
    }

    private fun initVibrate() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
}