package me.iwf.photopicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.myb.datacollect.R
import com.myb.datacollect.common.base.BaseActivity
import com.myb.datacollect.common.ext.setStatusBarColor
import kotlinx.android.synthetic.main.common_action_bar.*
import me.iwf.photopicker.event.OnItemCheckListener
import me.iwf.photopicker.fragment.ImagePagerFragment
import me.iwf.photopicker.fragment.PhotoPickerFragment
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textResource
import java.util.*

class PhotoPickerActivity : BaseActivity() {

    private var pickerFragment: PhotoPickerFragment? = null
    private var imagePagerFragment: ImagePagerFragment? = null

    private var maxCount = PhotoPicker.DEFAULT_MAX_COUNT

    /**
     * to prevent multiple calls to inflate menu
     */
    private var menuIsInflated = false

    private var isShowGif = false
    private var originalPhotos: ArrayList<String>? = null

    val activity: PhotoPickerActivity
        get() = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showCamera = intent.getBooleanExtra(PhotoPicker.EXTRA_SHOW_CAMERA, true)
        val showGif = intent.getBooleanExtra(PhotoPicker.EXTRA_SHOW_GIF, false)
        val previewEnabled = intent.getBooleanExtra(PhotoPicker.EXTRA_PREVIEW_ENABLED, true)

        isShowGif = showGif

        setContentView(R.layout.__picker_activity_photo_picker)

        maxCount = intent.getIntExtra(PhotoPicker.EXTRA_MAX_COUNT, PhotoPicker.DEFAULT_MAX_COUNT)
        val columnNumber = intent.getIntExtra(PhotoPicker.EXTRA_GRID_COLUMN, PhotoPicker.DEFAULT_COLUMN_NUMBER)
        originalPhotos = intent.getStringArrayListExtra(PhotoPicker.EXTRA_ORIGINAL_PHOTOS)

        pickerFragment = supportFragmentManager.findFragmentByTag("tag") as PhotoPickerFragment?
        if (pickerFragment == null) {
            pickerFragment = PhotoPickerFragment
                .newInstance(showCamera, showGif, previewEnabled, columnNumber, maxCount, originalPhotos)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, pickerFragment!!, "tag")
                .commit()
            supportFragmentManager.executePendingTransactions()
        }

        pickerFragment!!.photoGridAdapter.setOnItemCheckListener(OnItemCheckListener { _, photo, selectedItemCount ->
            actionText.isEnabled = selectedItemCount > 0

            if (maxCount <= 1) {
                val photos = pickerFragment!!.photoGridAdapter.selectedPhotos
                if (!photos.contains(photo.path)) {
                    photos.clear()
                    pickerFragment!!.photoGridAdapter.notifyDataSetChanged()
                }
                return@OnItemCheckListener true
            }

            if (selectedItemCount > maxCount) {
                Toast.makeText(
                    activity, getString(R.string.__picker_over_max_count_tips, maxCount),
                    LENGTH_LONG
                ).show()
                return@OnItemCheckListener false
            }
            updateSelectedImageCount(selectedItemCount)
            true
        })

        initActionBar()
    }

    //刷新右上角按钮文案
    fun updateTitleDoneItem() {
        if (menuIsInflated) {
            if (pickerFragment != null && pickerFragment!!.isResumed) {
                val photos = pickerFragment!!.photoGridAdapter.selectedPhotos
                val size = photos?.size ?: 0
                actionText.isEnabled = size > 0
                updateSelectedImageCount(size)

            } else if (imagePagerFragment != null && imagePagerFragment!!.isResumed) {
                //预览界面 完成总是可点的，没选就把默认当前图片
                actionText.isEnabled = true
            }

        }
    }

    private fun updateSelectedImageCount(selectedItemCount: Int) {
        if (maxCount > 1) {
            actionText.text = getString(R.string.__picker_done_with_count, selectedItemCount, maxCount)
        } else {
            actionText.text = getString(R.string.__picker_done)
        }
    }

    private fun initActionBar() {
        setStatusBarColor(fakeStatusBar)
        actionBar?.hide()
        titleText.textResource = R.string.photo_pick_photos
        updateSelectedImageCount(0)

        backArea.onClick { onBackPressed() }
        actionText.onClick { returnSelectedImages() }
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    override fun onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment!!.isVisible) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            }
        } else {
            super.onBackPressed()
        }
    }


    fun addImagePagerFragment(imagePagerFragment: ImagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, this.imagePagerFragment!!)
            .addToBackStack(null)
            .commit()
    }

    private fun returnSelectedImages() {
        val intent = Intent()
        var selectedPhotos: ArrayList<String>? = null
        if (pickerFragment != null) {
            selectedPhotos = pickerFragment!!.photoGridAdapter.selectedPhotoPaths
        }
        //当在列表没有选择图片，又在详情界面时默认选择当前图片
        if (selectedPhotos!!.size <= 0) {
            if (imagePagerFragment != null && imagePagerFragment!!.isResumed) {
                // 预览界面
                selectedPhotos = imagePagerFragment!!.currentPath
            }
        }
        if (selectedPhotos != null && selectedPhotos.size > 0) {
            intent.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, selectedPhotos)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
