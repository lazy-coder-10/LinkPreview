package com.linkpreview.none.linkpreview.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import com.linkpreview.none.linkpreview.PreviewData
import com.linkpreview.none.linkpreview.listener.LinkListener
import com.linkpreview.none.linkpreview.view.LinkPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URI

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun LinkPreview.loadPreviewData(
    link: String,
    listener: LinkListener?
) = withContext(Dispatchers.Default) {
    try {
        val result = try {
            val connection = Jsoup.connect(link).referrer("http://www.google.com")

            val doc: Document = connection.get()
            val imageElements = doc.select("meta[property=og:image]")

            //Get description from document object.
            val dataLink : Element? = doc.select("a").first()

            val siteName: String =getHostName(link).toString()


            if (imageElements.size > 0) {
                var it = 0
                var chosen: String? = ""

                while ((chosen == null || chosen.isEmpty()) && it < imageElements.size) {
                    chosen = imageElements[it].attr("content")
                    it += 1
                }

                PreviewData(siteName,doc.title(), chosen ?: "", link)
            } else {
                PreviewData("","", "", "")
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            PreviewData("","", "", "")
        } catch (e: Exception) {
            e.printStackTrace()
            PreviewData("","", "", "")
        }

        launch(Dispatchers.Main) {
            try {
                if (result.isNotEmpty()) {
                    setPreviewData(result)
                    listener?.onSuccess(result)
                } else {
                    Log.d("Article Request", "Image url is empty")
                    visibility = View.GONE
                    listener?.onError()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onError()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                listener?.onError()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



fun Context.launchUrlWithCustomTab(uri: Uri) {
    try{
        val browserIntent = Intent(Intent.ACTION_VIEW,uri)
        startActivity(browserIntent)
    }catch (e : Exception){
        e.printStackTrace()
    }
}

/*
    * get hostName */
fun getHostName(baseUrl: String): CharSequence {

    val uri = URI(baseUrl)
    val hostname: String = uri.host
    return if (hostname.startsWith("www.")) hostname.substring(4) else hostname

}