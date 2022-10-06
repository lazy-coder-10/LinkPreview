package com.linkpreview.none.linkpreview.extension

import android.os.Build
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import com.linkpreview.none.R

/**
 * Image extension to load image from URL or disk*/
fun ImageView.loadCoil(
    url: String? = null,
    placeholder: Int = R.mipmap.ic_launcher,
    error: Int = placeholder
) {

    load(url) {
        crossfade(true)
        placeholder(placeholder)
        error(error)
    }
}

