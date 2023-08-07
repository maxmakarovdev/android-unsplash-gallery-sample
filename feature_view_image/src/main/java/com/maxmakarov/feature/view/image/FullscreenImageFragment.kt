package com.maxmakarov.feature.view.image

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.maxmakarov.base.gallery.data.PhotosRepository
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.ui.BaseFragment
import com.maxmakarov.feature.view.image.databinding.FullscreenImageFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.LazyThreadSafetyMode.NONE


class FullscreenImageFragment : BaseFragment<FullscreenImageFragmentBinding>() {

    private val viewModel by lazy(NONE){ FullscreenImageViewModel.get(this) }

    private val ctx get() = requireActivity()
    private val bottomSheet by lazy { BottomSheetBehavior.from(binding.infoBottomSheet.root) }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(ctx).inflateTransition(android.R.transition.move)
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
            back.setOnClickListener { findNavController().popBackStack() }
            share.setOnClickListener { share() }
            download.setOnClickListener { download() }
            favorite.setOnClickListener { viewModel.favoriteClicked() }
            info.setOnClickListener { toggleInfo() }

            bindFavorite()
            imageView.transitionName = photo.id
            loadImage(photo)
            bottomSheet.state = STATE_HIDDEN
            bindInfo(photo)
        }
    }

    private fun bindFavorite() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavoriteStream
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    binding.favorite.setIconResource(
                        if (it == true) R.drawable.ic_star_24 else R.drawable.ic_star_outline_24
                    )
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavoriteStream
                .drop(2)
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    if (it == true) {
                        hostActivity?.setFavouritesBadge(isVisible = true)
                    }
                }
        }
    }

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

    private fun loadImage(photo: UnsplashPhoto) {
        binding.imageView.load(photo.urls.full)
    }

    @SuppressLint("SetTextI18n")
    private fun bindInfo(photo: UnsplashPhoto) {
        binding.infoBottomSheet.apply {
            avatar.load(photo.user.profile_image.large) {
                transformations(CircleCropTransformation())
                crossfade(true)
            }
            name.text = photo.user.name
            username.setUpClickableUsername("@${photo.user.username}") {
                Intent(ACTION_VIEW, Uri.parse(photo.user.links.html)).also { startActivity(it) }
            }

            description.text = photo.description
            description.isVisible = photo.description != null
            resolution.text = "${photo.width}x${photo.height}"
            date.text = transformDate(photo.created_at)
        }
    }

    private fun toggleInfo() {
        when (bottomSheet.state) {
            STATE_EXPANDED -> bottomSheet.state = STATE_HIDDEN
            STATE_HIDDEN -> bottomSheet.state = STATE_EXPANDED
            else -> {}
        }
    }

    private inline fun TextView.setUpClickableUsername(
        text: String,
        crossinline onClick: () -> Unit
    ) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onClick()
            }
        }
        SpannableStringBuilder().apply {
            append(SpannableString(text))
            setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setText(this, TextView.BufferType.SPANNABLE)
        }
        movementMethod = LinkMovementMethod.getInstance()
    }

    private fun transformDate(date: String): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            .parse(date)
            ?.let { DateFormat.getDateFormat(ctx).format(it) } ?: ""
    }
}