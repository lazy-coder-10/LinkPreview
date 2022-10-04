package com.linkpreview.none.linkpreview

import androidx.annotation.Keep

@Keep
data class PreviewData(val siteName : String,val title: String, val imageUrl: String, val baseUrl: String) {

    fun isEmpty(): Boolean = title.isEmpty()  && baseUrl.isEmpty()

    fun isNotEmpty(): Boolean = !isEmpty()
}