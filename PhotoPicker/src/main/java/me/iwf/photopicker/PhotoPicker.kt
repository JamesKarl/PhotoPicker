package me.iwf.photopicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import me.iwf.photopicker.utils.PermissionsUtils
import java.util.*

object PhotoPicker {

    const val REQUEST_CODE = 233

    const val DEFAULT_MAX_COUNT = 9
    const val DEFAULT_COLUMN_NUMBER = 3

    const val KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS"

    const val EXTRA_MAX_COUNT = "MAX_COUNT"
    const val EXTRA_SHOW_CAMERA = "SHOW_CAMERA"
    const val EXTRA_SHOW_GIF = "SHOW_GIF"
    const val EXTRA_GRID_COLUMN = "column"
    const val EXTRA_ORIGINAL_PHOTOS = "ORIGINAL_PHOTOS"
    const val EXTRA_PREVIEW_ENABLED = "PREVIEW_ENABLED"

    fun builder(): PhotoPickerBuilder {
        return PhotoPickerBuilder()
    }

    class PhotoPickerBuilder {
        private val mPickerOptionsBundle: Bundle = Bundle()
        private val mPickerIntent: Intent = Intent()

        /**
         * Send the Intent from an Activity with a custom request code
         *
         * @param activity    Activity to receive result
         * @param requestCode requestCode for result
         */
        @JvmOverloads
        fun start(activity: Activity, requestCode: Int = REQUEST_CODE) {
            if (PermissionsUtils.checkReadStoragePermission(activity)) {
                activity.startActivityForResult(getIntent(activity), requestCode)
            }
        }

        /**
         * @param fragment    Fragment to receive result
         * @param requestCode requestCode for result
         */
        fun start(
            context: Context,
            fragment: Fragment, requestCode: Int
        ) {
            if (PermissionsUtils.checkReadStoragePermission(fragment.activity)) {
                fragment.startActivityForResult(getIntent(context), requestCode)
            }
        }

        /**
         * Send the Intent with a custom request code
         *
         * @param fragment Fragment to receive result
         */
        fun start(
            context: Context,
            fragment: Fragment
        ) {
            if (PermissionsUtils.checkReadStoragePermission(fragment.activity)) {
                fragment.startActivityForResult(getIntent(context), REQUEST_CODE)
            }
        }

        /**
         * Get Intent to start [PhotoPickerActivity]
         *
         * @return Intent for [PhotoPickerActivity]
         */
        fun getIntent(context: Context): Intent {
            mPickerIntent.setClass(context, PhotoPickerActivity::class.java)
            mPickerIntent.putExtras(mPickerOptionsBundle)
            return mPickerIntent
        }

        fun setPhotoCount(photoCount: Int): PhotoPickerBuilder {
            mPickerOptionsBundle.putInt(EXTRA_MAX_COUNT, photoCount)
            return this
        }

        fun setGridColumnCount(columnCount: Int): PhotoPickerBuilder {
            mPickerOptionsBundle.putInt(EXTRA_GRID_COLUMN, columnCount)
            return this
        }

        fun setShowGif(showGif: Boolean): PhotoPickerBuilder {
            mPickerOptionsBundle.putBoolean(EXTRA_SHOW_GIF, showGif)
            return this
        }

        fun setShowCamera(showCamera: Boolean): PhotoPickerBuilder {
            mPickerOptionsBundle.putBoolean(EXTRA_SHOW_CAMERA, showCamera)
            return this
        }

        fun setSelected(imagesUri: ArrayList<String>): PhotoPickerBuilder {
            mPickerOptionsBundle.putStringArrayList(EXTRA_ORIGINAL_PHOTOS, imagesUri)
            return this
        }

        fun setPreviewEnabled(previewEnabled: Boolean): PhotoPickerBuilder {
            mPickerOptionsBundle.putBoolean(EXTRA_PREVIEW_ENABLED, previewEnabled)
            return this
        }
    }
}
