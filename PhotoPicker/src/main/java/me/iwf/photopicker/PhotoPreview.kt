package me.iwf.photopicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*


object PhotoPreview {

    const val REQUEST_CODE = 666

    const val EXTRA_CURRENT_ITEM = "current_item"
    const val EXTRA_PHOTOS = "photos"
    const val EXTRA_SHOW_DELETE = "show_delete"


    fun builder(): PhotoPreviewBuilder {
        return PhotoPreviewBuilder()
    }


    class PhotoPreviewBuilder {
        private val mPreviewOptionsBundle: Bundle = Bundle()
        private val mPreviewIntent: Intent = Intent()

        /**
         * Send the Intent from an Activity with a custom request code
         *
         * @param activity    Activity to receive result
         * @param requestCode requestCode for result
         */
        @JvmOverloads
        fun start(activity: Activity, requestCode: Int = REQUEST_CODE) {
            activity.startActivityForResult(getIntent(activity), requestCode)
        }

        /**
         * Send the Intent with a custom request code
         *
         * @param fragment    Fragment to receive result
         * @param requestCode requestCode for result
         */
        fun start(context: Context, fragment: Fragment, requestCode: Int) {
            fragment.startActivityForResult(getIntent(context), requestCode)
        }

        /**
         * Send the Intent with a custom request code
         *
         * @param fragment Fragment to receive result
         */
        fun start(context: Context, fragment: Fragment) {
            fragment.startActivityForResult(getIntent(context), REQUEST_CODE)
        }

        /**
         * Get Intent to start [PhotoPickerActivity]
         *
         * @return Intent for [PhotoPickerActivity]
         */
        fun getIntent(context: Context): Intent {
            mPreviewIntent.setClass(context, PhotoPagerActivity::class.java)
            mPreviewIntent.putExtras(mPreviewOptionsBundle)
            return mPreviewIntent
        }

        fun setPhotos(photoPaths: ArrayList<String>): PhotoPreviewBuilder {
            mPreviewOptionsBundle.putStringArrayList(EXTRA_PHOTOS, photoPaths)
            return this
        }

        fun setCurrentItem(currentItem: Int): PhotoPreviewBuilder {
            mPreviewOptionsBundle.putInt(EXTRA_CURRENT_ITEM, currentItem)
            return this
        }

        fun setShowDeleteButton(showDeleteButton: Boolean): PhotoPreviewBuilder {
            mPreviewOptionsBundle.putBoolean(EXTRA_SHOW_DELETE, showDeleteButton)
            return this
        }
    }
}
