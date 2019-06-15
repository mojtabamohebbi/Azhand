package ir.elevin.azhand

import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_image_viewer.*
import ooo.oxo.library.widget.PullBackLayout


class ImageViewerActivity : AppCompatActivity(), PullBackLayout.Callback{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.allowEnterTransitionOverlap = true
            window.allowReturnTransitionOverlap = true
        }

        Glide.with(this).load(intent?.extras?.getString("image")).listener(object : RequestListener<Drawable> {
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition()
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }
        }).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(posterIv)

        puller.setCallback(this)
    }

    override fun onPullStart() {
        // fade out Action Bar ...
        // show Status Bar ...
    }

    override fun onPull(progress: Float) {
        // set the opacity of the window's background
    }

    override fun onPullCancel() {
        // fade in Action Bar
    }

    override fun onPullComplete() {
        supportFinishAfterTransition()
    }
}
