package com.maxmakarov.feature.view.image

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.content.IntentFilter
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.maxmakarov.base.gallery.data.PhotosRepository
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.ui.BaseFragment
import com.maxmakarov.core.ui.BlurHashDecoder
import com.maxmakarov.feature.view.image.databinding.FullscreenImageFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE


class FullscreenImageFragment : BaseFragment<FullscreenImageFragmentBinding>() {

    private val viewModel by lazy(NONE){ FullscreenImageViewModel.get(this) }

    private val ctx get() = requireActivity()

    private val onDownloadComplete = object : BroadcastReceiver() {
        @SuppressLint("ShowToast")
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val uri = getSystemService(ctx, DownloadManager::class.java)?.getUriForDownloadedFile(id)

            if (uri != null) {
                openDownloadedImage(uri)
            } else {
                Snackbar.make(binding.root, R.string.download_failed, LENGTH_LONG).apply {
                    setAction(R.string.retry) { download() }
                    showAboveNavBar()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        context?.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(onDownloadComplete)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FullscreenImageFragmentBinding {
        return FullscreenImageFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photo: UnsplashPhoto = PhotosRepository.photoToView!!
        viewModel.init(photo)
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            share.setOnClickListener {
                share()
            }
            download.setOnClickListener {
                download()
            }
            favorite.setOnClickListener {
                viewModel.favoriteClicked()
            }
            info.setOnClickListener {
                viewModel.infoClicked()
            }

            lifecycleScope.launch {
                viewModel.isFavoriteStream.collectLatest {
                    favorite.setIconResource(if (it == true) R.drawable.ic_star_24 else R.drawable.ic_star_outline_24)
                }
            }

            lifecycleScope.launch {
                viewModel.isFavoriteStream.drop(2).collectLatest {
                    if (it == true) {
                        hostActivity?.setFavouritesBadge(isVisible = true)
                    }
                }
            }

//            initDragListener()

            imageView.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
            val bitmap = BlurHashDecoder.decode(photo.blur_hash, 50, 50)
            imageView.load(photo.urls.full) {
                placeholder(BitmapDrawable(resources, bitmap))
                crossfade(true)
            }
        }
    }

//    private fun FullscreenImageFragmentBinding.initDragListener(){
        //todo uncomment this
        /*imageView.dragDownListener = object : ZoomableImageView.DragDownListener {
            private val edge = resources.getDimension(R.dimen.image_drag_threshold)
            private val toolbarViews = arrayOf(back, share, download, favorite, info)

            override fun onDragDown(distance: Float) {
                toolbarViews.forEach { it.translationY = -distance }
                root.background.alpha = (255 * (1 - distance.norm(edge))).toInt()
            }

            private fun Float.norm(value: Float): Float {
                val result = this / value
                return when {
                    result > 1 -> 1f
                    result < 0 -> 0f
                    else -> result
                }
            }

            override fun onDragEnded(distance: Float) {
                if (distance >= edge) {
                    findNavController().popBackStack()
                } else {
                    imageView.animate().translationY(0f).start()
                    toolbarViews.forEach { it.animate().translationY(0f).start() }
                    ObjectAnimator.ofInt(root.background.alpha, 255).apply {
                        addUpdateListener {
                            root.background.alpha = it.animatedValue as Int
                        }
                        start()
                    }
                }
            }
        }*/
//    }

    @SuppressLint("ShowToast")
    private fun download() {
        Snackbar.make(binding.root, R.string.downloading, BaseTransientBottomBar.LENGTH_SHORT)
            .showAboveNavBar()

        viewModel.trackDownloads()

        val uri = Uri.parse(viewModel.photo.urls.full)
        getSystemService(ctx, DownloadManager::class.java)?.enqueue(
            DownloadManager.Request(uri)
                .setDestinationInExternalFilesDir(
                    ctx,
                    Environment.DIRECTORY_PICTURES,
                    uri.lastPathSegment
                )
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        )
    }

    private fun share() {
        Intent(ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title))
            putExtra(Intent.EXTRA_TEXT, viewModel.photo.urls.raw)
            startActivity(Intent.createChooser(this, "choose one"))
        }
    }

    private fun openDownloadedImage(uri: Uri) {
        Intent(ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(this)
        }
    }
}