# Refreshable-WebView

## Introduction
This code allows you to implement a custom pull-to-refresh (swipe refresh) feature. It enables you to display an Android View in an overlay form on top of a WebView.

## Usage
```kotlin
val listener = object : RefreshListener {
    fun onRefresh(view: View){
        Log.d(TAG, "on refresh")
    }
}

val indicator = layoutInflater
    .inflate(R.layout.view_indicator, null)
    .findViewById<ImageView>(R.id.image_view)
    .apply { if (background is AnimationDrawable) (background as AnimationDrawable).start() }

val setting = RefreshSetting
    .Builder(this)                          //context
    .setLoadingIndicatorView(indicator)     //any view that extends View can be used. example: res/layout/view_indicator.xml
    .setOnRefreshListener(listener)
    .setRefreshThresholdDp(200)             //conditions for refresh to trigger
    .setRefreshingTime(1.seconds)           //refresh ui display duration
    .setRefreshVibrate(true)                //vibration effect on refresh
    .build()

webView.setRefreshSetting(setting)


//flag enabled
webView.setRefreshEnabled(false)             //default is true
```