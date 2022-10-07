# Android Link Preview

An Android Project with demo application, to fetch meta-data from url, like facebook, youtube and other websites.

# Usage
You can check out the sample project. Following libs is added for parsing the URL 

Add the Jsoup dependency - Jsoup is an open-source Java library designed to parse, extract, and manipulate data stored in HTML documents.

   dependencies {
     implementation 'org.jsoup:jsoup:1.15.2'
   }

### If using ProGuard add this line to your proguard-rules.pro:

    -keep public class org.jsoup.** {  
	    public *;  
    }

> if you want to set obtained meta data to view
~~~java
    mBinding.idLinkPreview.parseTextForLink(mBinding.edtLink.text.toString())
~~~

> Set your own CickListener

~~~java
    //set your own click listener
    mBinding.idLinkPreview.loadListener = object : LinkListener {
          override fun onError() {
          Toast.makeText(this@MainActivity,"Error on Preview link",Toast.LENGTH_SHORT).show()
        }

        override fun onSuccess(link: PreviewData) {
              // do your stuff
        }
 }
~~~


