package com.linkpreview.none.linkpreview.helper

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object LinkPreviewNetwork {

    /**
     *
     */
    suspend fun loadImage(context: Context, link: String): String? {
        return try {
            withContext(Dispatchers.Default) {
                val key = link.hashCode()

                try {
                    val connection = Jsoup.connect(link)
                    val doc: Document = connection.get()
                    val imageElements = doc.select("meta[property=og:image]")

                    if (imageElements.size > 0) {
                        var it = 0
                        var chosen: String? = ""

                        while ((chosen == null || chosen.isEmpty()) && it < imageElements.size) {
                            chosen = imageElements[it].attr("content")
                            it += 1
                        }

                        chosen
                    } else {
                        ""
                    }
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                    ""
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}