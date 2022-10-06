package com.linkpreview.none.linkpreview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import coil.load
import com.linkpreview.none.linkpreview.PreviewData
import com.linkpreview.none.R
import com.linkpreview.none.databinding.LayoutLinkPreviewBinding
import com.linkpreview.none.linkpreview.listener.LinkListener
import com.linkpreview.none.linkpreview.ImageType
import com.linkpreview.none.linkpreview.extension.*
import kotlinx.coroutines.*
import java.net.URI

open class LinkPreview : FrameLayout {

    protected lateinit var binding: LayoutLinkPreviewBinding

    /** Coroutine Job */
    private val linkJob = Job()

    /** Coroutine Scope */
    private val linkScope = CoroutineScope(Dispatchers.Main + linkJob)

    /** Link Indicator to keep track of link status */
    private var linkIndicator: Int = 0

    /** Type of image to handle in specific way */
    private var imageType = ImageType.NONE

    /** Parsed URL */
    protected var url = ""

    /** Optional listener for load callbacks */
    var loadListener: LinkListener? = null


    constructor(context: Context) : super(context) {
        bindViews(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        bindViews(context)
        bindAttrs(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        bindViews(context)
        bindAttrs(attrs, defStyle)
    }


    /**
     * Convenience method to add views to layout
     *
     * @param context for inflating view
     */
    private fun bindViews(context: Context) {
        binding = LayoutLinkPreviewBinding.inflate(LayoutInflater.from(context), this, true)


        if (isInEditMode) {
            return
        }
    }

    private fun bindAttrs(attrs: AttributeSet, defStyle: Int) {
        /* context.theme.obtainStyledAttributes(attrs, R.styleable.LinkPreview, defStyle, 0).let { linkAttrs ->
             try {
                 linkAttrs.getBoolean(R.styleable.LinkPreview_roundedCorners, false).let { useRoundedCorners ->
                     binding.previewImage.shapeAppearanceModel = binding.previewImage.shapeAppearanceModel.toBuilder().setAllCornerSizes(if (useRoundedCorners) 24f else 0f).build()
                 }
             } finally {
                 linkAttrs.recycle()
             }
         }*/
    }

    /**
     * Sets the actual text of the view handling multiple types of images including the link cache
     */
    private fun setText() {
        try {
          /*  visibility = View.VISIBLE
            binding.previewText.text = url*/
            imageType = ImageType.DEFAULT
            linkScope.launch {
                if (linkIndicator == 1) {
                    linkScope.cancel()
                }

                linkIndicator = 1
                loadPreviewData(url, loadListener)
                linkIndicator = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imageType = ImageType.NONE
            visibility = View.GONE
            loadListener?.onError()
        }

    }

    /**
     * Handles loading the article image using Glide
     *
     * @param data to image url and article title
     */
    fun setPreviewData(data: PreviewData, displayBg: Boolean = false) {


        binding.previewImage.loadCoil(data.imageUrl, placeholder = R.color.purple_200)
        binding.previewImage.isVisible = data.imageUrl.isNotBlank()

        binding.previewText.text = data.title
        binding.previewText.isVisible = data.title.isNotBlank()

        binding.previewLink.text = data.baseUrl
        binding.previewText.isVisible = data.baseUrl.isNotBlank()

        binding.hostName.text = data.siteName
        binding.previewText.isVisible = data.siteName.isNotBlank()


        binding.root.setOnClickListener {
            context.launchUrlWithCustomTab(data.baseUrl.toUri())
        }

        if (!isVisible) {
            isVisible = true
        }
    }

    /*
    * get hostName */
    fun getHostName(baseUrl: String): CharSequence {

        val uri = URI(baseUrl)
        val hostname: String = uri.host
        return if (hostname.startsWith("www.")) hostname.substring(4) else hostname

    }

    /**
     * Allows easy passing of possible link text to check for links that can be handled by [LinkPreview]
     *
     * @param text entire body to search for link
     * @return if a link was found in the text
     */
    fun parseTextForLink(text: String): Boolean {
        return when {
            text.contains("youtube") && text.contains("v=") -> {
                val id = text.split("v=").getOrNull(1)?.split(" ")?.get(0)
                url = "https://www.youtube.com/watch?v=$id"
                setText()
                return  true
            }
            text.contains("youtu.be") -> {
                val id = text.split("be/").getOrNull(1)?.split(" ")?.get(0)
                url = "https://www.youtube.com/watch?v=$id"
                setText()
                return true
            }
            text.contains("http") -> {
                url = text.parseUrl()

                if (url.startsWith("http://"))
                    url = url.replace("http://", "https://")

                setText()
                true
            }
            else -> {
                imageType = ImageType.NONE
                visibility = View.GONE
                false
            }
        }
    }

    /**
     * Allows direct setting of url if already known
     *
     * @param link which contains only the url and nothing else
     */
    fun setLink(link: String) {
        if (link.isUrl()) {
            url = link
            setText()
        } else {
            throw IllegalArgumentException("String is not a valid link, if you want to parse full text use LinkPreview.parseTextForLink")
        }
    }
}