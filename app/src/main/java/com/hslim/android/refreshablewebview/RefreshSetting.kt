package com.hslim.android.refreshablewebview

import android.content.Context
import android.view.View
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RefreshSetting private constructor(
    val context: Context,
    val listener: RefreshListener,
    val indicatorView: View,
    val refreshThresholdDp: Int,
    val refreshingTime: Duration,
    val isVibrate: Boolean,
){
    data class Builder(
        val context: Context,
    ) {
        private var _listener: RefreshListener? = null
        private var _indicatorView: View? = null
        private var _refreshThresholdDp: Int = 200
        private var _refreshingTime: Duration = 2.seconds
        private var _isVibrate: Boolean = false

        fun setOnRefreshListener(listener: RefreshListener) = apply { this._listener = listener }
        fun setLoadingIndicatorView(view: View) = apply { this._indicatorView = view }
        fun setRefreshThresholdDp(dp: Int) = apply { this._refreshThresholdDp = dp }
        fun setRefreshingTime(duration: Duration) = apply { this._refreshingTime = duration }
        fun setRefreshVibrate(isVibrate: Boolean) = apply { this._isVibrate = isVibrate }

        fun build(): RefreshSetting {
            return RefreshSetting(
                context,
                checkNotNull(_listener),
                checkNotNull(_indicatorView),
                _refreshThresholdDp,
                _refreshingTime,
                _isVibrate,
            )
        }
    }
}