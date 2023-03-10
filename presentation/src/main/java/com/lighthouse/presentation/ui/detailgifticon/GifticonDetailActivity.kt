package com.lighthouse.presentation.ui.detailgifticon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.lighthouse.presentation.R
import com.lighthouse.presentation.databinding.ActivityGifticonDetailBinding
import com.lighthouse.presentation.extension.isOnScreen
import com.lighthouse.presentation.extension.repeatOnStarted
import com.lighthouse.presentation.extension.scrollToBottom
import com.lighthouse.presentation.extension.show
import com.lighthouse.presentation.extra.Extras
import com.lighthouse.presentation.ui.common.dialog.OriginImageDialog
import com.lighthouse.presentation.ui.detailgifticon.dialog.LargeBarcodeDialog
import com.lighthouse.presentation.ui.detailgifticon.dialog.UseGifticonDialog
import com.lighthouse.presentation.ui.edit.modifygifticon.ModifyGifticonActivity
import com.lighthouse.presentation.ui.history.HistoryBottomSheet
import com.lighthouse.presentation.ui.security.AuthCallback
import com.lighthouse.presentation.ui.security.AuthManager
import com.lighthouse.presentation.util.permission.LocationPermissionManager
import com.lighthouse.presentation.util.permission.core.permissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GifticonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGifticonDetailBinding
    private val viewModel: GifticonDetailViewModel by viewModels()

    private val standardGifticonInfo by lazy {
        supportFragmentManager.findFragmentByTag(StandardGifticonInfoFragment::class.java.name)
            ?: StandardGifticonInfoFragment()
    }
    private val cashCardGifticonInfo by lazy {
        supportFragmentManager.findFragmentByTag(CashCardGifticonInfoFragment::class.java.name)
            ?: CashCardGifticonInfoFragment()
    }

    private lateinit var useGifticonDialog: UseGifticonDialog

    private var largeBarcodeDialog: LargeBarcodeDialog? = null

    private val btnMaster by lazy { binding.btnMaster }
    private val chip by lazy { binding.chipScrollDownForUseButton }

    private val locationPermission: LocationPermissionManager by permissions()

    private val historyBottomSheet = HistoryBottomSheet()

    @Inject
    lateinit var authManager: AuthManager
    private val usageBiometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> usageAuthCallback.onAuthSuccess()
                else -> usageAuthCallback.onAuthError()
            }
        }

    private val usageAuthCallback = object : AuthCallback {
        override fun onAuthSuccess() {
            showUseGifticonDialog()
        }

        override fun onAuthCancel() {
        }

        override fun onAuthError(@StringRes stringId: Int?) {
            if (stringId != null) {
                Toast.makeText(this@GifticonDetailActivity, getString(stringId), Toast.LENGTH_SHORT).show()
            } else {
                usageAuthenticate()
            }
        }
    }

    private val editBiometricLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> editAuthCallback.onAuthSuccess()
                else -> editAuthCallback.onAuthError()
            }
        }

    private val editAuthCallback = object : AuthCallback {
        override fun onAuthSuccess() {
            gotoModifyGifticon(viewModel.gifticon.value?.id)
        }

        override fun onAuthCancel() {
        }

        override fun onAuthError(@StringRes stringId: Int?) {
            if (stringId != null) {
                Toast.makeText(this@GifticonDetailActivity, getString(stringId), Toast.LENGTH_SHORT).show()
            } else {
                usageAuthenticate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gifticon_detail)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        setSupportActionBar(binding.tbGifticonDetail)

        binding.btnMaster.viewTreeObserver.addOnDrawListener {
            chip.post {
                chip.isVisible = btnMaster.isOnScreen().not()
            }
        }
        repeatOnStarted {
            locationPermission.permissionFlow.collectLatest {
                viewModel.updateLocationPermission(it)
            }
        }
        repeatOnStarted {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted {
            viewModel.gifticon.collect { gifticon ->
                val fragment = when (gifticon?.isCashCard) {
                    true -> cashCardGifticonInfo
                    false -> standardGifticonInfo
                    else -> null
                }
                if (fragment != null && fragment.isAdded.not()) {
                    supportFragmentManager.commit {
                        replace(binding.fcvGifticonInfo.id, fragment, fragment::class.java.name)
                    }
                }
            }
        }
        repeatOnStarted {
            viewModel.failure.collect {
                showInvalidDialog()
            }
        }
        repeatOnStarted {
            viewModel.historyUiModel.collectLatest { histories ->
                historyBottomSheet.submitList(histories)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_gifticon_detail, menu)

        // Get colorOnPrimary
        val colorOnPrimary = TypedValue()
        this.theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, colorOnPrimary, true)

        // Set colorOnPrimary
        val item = menu?.findItem(R.id.menu_history)
        item?.icon = item?.icon?.apply {
            setTint(ContextCompat.getColor(this@GifticonDetailActivity, colorOnPrimary.resourceId))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                showUsageHistoryDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleEvent(event: GifticonDetailEvent) {
        when (event) {
            is GifticonDetailEvent.ScrollDownForUseButtonClicked -> {
                binding.abGifticonDetail.setExpanded(false, true)
                binding.svGifticonDetail.scrollToBottom()
            }

            is GifticonDetailEvent.EditButtonClicked -> {
                editAuthenticate()
            }

            is GifticonDetailEvent.UseGifticonButtonClicked -> {
                usageAuthenticate()
            }

            is GifticonDetailEvent.ShowAllUsedInfoButtonClicked -> {
                showUsageHistoryDialog()
            }

            is GifticonDetailEvent.UseGifticonComplete -> {
                if (::useGifticonDialog.isInitialized && useGifticonDialog.isAdded) {
                    useGifticonDialog.dismiss()
                }
            }

            is GifticonDetailEvent.ShowOriginalImage -> {
                showOriginGifticonDialog(event.origin)
            }

            is GifticonDetailEvent.ShowLargeBarcode -> {
                showLargeBarcodeDialog(event.barcode)
            }

            GifticonDetailEvent.ShareButtonClicked -> {
                // TODO 공유 기능
            }
        }
    }

    private fun gotoModifyGifticon(gifticonId: String?) {
        gifticonId ?: return
        val intent = Intent(this, ModifyGifticonActivity::class.java).apply {
            putExtra(Extras.KEY_MODIFY_GIFTICON_ID, gifticonId)
        }
        startActivity(intent)
    }

    private fun showUseGifticonDialog() {
        useGifticonDialog = UseGifticonDialog().also { dialog ->
            dialog.show(supportFragmentManager, UseGifticonDialog.TAG)
        }
    }

    private fun showUsageHistoryDialog() {
        historyBottomSheet.show(supportFragmentManager, HistoryBottomSheet.TAG)
    }

    private fun showInvalidDialog() {
        var lastSecond = INVALID_DIALOG_DEADLINE_SECOND // 자동으로 닫힐 시간(초)
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.gifticon_detail_invalid_dialog_title))
            .setNegativeButton(getString(R.string.all_close_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                finish()
            }
            .create()
        lifecycleScope.launch {
            do {
                dialog
                    .setMessage(getString(R.string.gifticon_detail_invalid_dialog_message, lastSecond))
                dialog.show()
                delay(1000)
            } while (--lastSecond > 0)

            dialog.dismiss()
            cancel()
        }
    }

    private fun usageAuthenticate() {
        authManager.auth(this, usageBiometricLauncher, usageAuthCallback)
    }

    private fun editAuthenticate() {
        authManager.auth(this, editBiometricLauncher, editAuthCallback)
    }

    private fun showOriginGifticonDialog(path: String) {
        val uri = this.getFileStreamPath(path).toUri()
        OriginImageDialog().apply {
            arguments = Bundle().apply {
                putParcelable(Extras.KEY_ORIGIN_IMAGE, uri)
            }
        }.show(supportFragmentManager)
    }

    private fun showLargeBarcodeDialog(barcode: String) {
        if (largeBarcodeDialog?.isAdded == true) {
            largeBarcodeDialog?.dismiss()
        }
        largeBarcodeDialog = LargeBarcodeDialog().apply {
            arguments = Bundle().apply {
                putString(Extras.KEY_BARCODE, barcode)
            }
        }
        largeBarcodeDialog?.show(supportFragmentManager)
    }

    companion object {
        const val INVALID_DIALOG_DEADLINE_SECOND = 5
    }
}
