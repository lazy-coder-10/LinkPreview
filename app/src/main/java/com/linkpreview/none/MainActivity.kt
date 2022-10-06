package com.linkpreview.none

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.linkpreview.none.databinding.ActivityMainBinding
import com.linkpreview.none.linkpreview.PreviewData
import com.linkpreview.none.linkpreview.extension.isUrl
import com.linkpreview.none.linkpreview.listener.LinkListener

class MainActivity : AppCompatActivity() {

    lateinit var mBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        setListener()

    }

    private fun setListener() {
        mBinding.idLinkPreview.loadListener = object : LinkListener {
            override fun onError() {
                Toast.makeText(this@MainActivity,"Error on Preview link",Toast.LENGTH_SHORT).show()
                mBinding.idLinkPreview.visibility = View.GONE
            }

            override fun onSuccess(link: PreviewData) {
                mBinding.idLinkPreview.visibility = View.VISIBLE
            }

        }

        mBinding.btnPreviewLink.setOnClickListener {
            if (mBinding.edtLink.text.toString().isNotBlank() && mBinding.edtLink.text.toString()
                    .isUrl()
            ) {
                mBinding.idLinkPreview.parseTextForLink(mBinding.edtLink.text.toString())

            }else{
                Toast.makeText(this@MainActivity,"Error on Parsing link",Toast.LENGTH_SHORT).show()
            }
        }
    }
}