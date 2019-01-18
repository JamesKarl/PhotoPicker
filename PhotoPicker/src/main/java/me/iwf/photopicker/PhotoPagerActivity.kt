package me.iwf.photopicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.myb.common.base.BaseActivity
import com.myb.shop.R
import me.iwf.photopicker.PhotoPreview.EXTRA_CURRENT_ITEM
import me.iwf.photopicker.PhotoPreview.EXTRA_PHOTOS
import me.iwf.photopicker.PhotoPreview.EXTRA_SHOW_DELETE
import me.iwf.photopicker.fragment.ImagePagerFragment

class PhotoPagerActivity : BaseActivity() {

    private var pagerFragment: ImagePagerFragment? = null

    private var actionBar: ActionBar? = null
    private var showDelete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.__picker_activity_photo_pager)

        val currentItem = intent.getIntExtra(EXTRA_CURRENT_ITEM, 0)
        val paths = intent.getStringArrayListExtra(EXTRA_PHOTOS)
        showDelete = intent.getBooleanExtra(EXTRA_SHOW_DELETE, true)

        if (pagerFragment == null) {
            pagerFragment = supportFragmentManager.findFragmentById(R.id.photoPagerFragment) as ImagePagerFragment?
        }
        pagerFragment!!.setPhotos(paths, currentItem)
        pagerFragment!!.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                updateActionBarTitle()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (showDelete) {
            menuInflater.inflate(R.menu.__picker_menu_preview, menu)
        }
        return true
    }


    override fun onBackPressed() {

        val intent = Intent()
        intent.putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, pagerFragment!!.paths)
        setResult(Activity.RESULT_OK, intent)
        finish()

        super.onBackPressed()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        if (item.itemId == R.id.delete) {
            val index = pagerFragment!!.currentItem

            val deletedPath = pagerFragment!!.paths[index]

            val snackbar = Snackbar.make(
                pagerFragment!!.view!!, R.string.__picker_deleted_a_photo,
                Snackbar.LENGTH_LONG
            )

            if (pagerFragment!!.paths.size <= 1) {

                // show confirm dialog
                AlertDialog.Builder(this)
                    .setTitle(R.string.__picker_confirm_to_delete)
                    .setPositiveButton(R.string.__picker_yes) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        pagerFragment!!.paths.removeAt(index)
                        pagerFragment!!.viewPager.adapter!!.notifyDataSetChanged()
                        onBackPressed()
                    }
                    .setNegativeButton(R.string.__picker_cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()

            } else {

                snackbar.show()

                pagerFragment!!.paths.removeAt(index)
                pagerFragment!!.viewPager.adapter!!.notifyDataSetChanged()
            }

            snackbar.setAction(R.string.__picker_undo) {
                if (pagerFragment!!.paths.size > 0) {
                    pagerFragment!!.paths.add(index, deletedPath)
                } else {
                    pagerFragment!!.paths.add(deletedPath)
                }
                pagerFragment!!.viewPager.adapter!!.notifyDataSetChanged()
                pagerFragment!!.viewPager.setCurrentItem(index, true)
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun updateActionBarTitle() {
        if (actionBar != null)
            actionBar!!.title = getString(
                R.string.__picker_image_index, pagerFragment!!.viewPager.currentItem + 1,
                pagerFragment!!.paths.size
            )
    }
}
