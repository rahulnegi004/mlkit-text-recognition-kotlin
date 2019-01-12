package com.cyberslayer.mlkittextrecognition

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityPresenter.View{
    private lateinit var presenter: MainActivityPresenter
    private val CAMERA_REQUEST_CODE: Int = 143

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.presenter = MainActivityPresenter(this)

        setUpNewImageListener()
    }

    override fun showNoTextMessage() {
        Toast.makeText(this, "No text detected", Toast.LENGTH_LONG).show()
    }

    override fun showText(text: String) {
        dateTextView.text = text
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CAMERA_REQUEST_CODE -> if( resultCode == Activity.RESULT_OK){
                dateTextView.text =""
                data?.data?.let {
                    val selectedImageBitmap = resizeImage(it)
                    imageView.setImageBitmap(selectedImageBitmap)
                    presenter.runTextRecognition(selectedImageBitmap!!)
                }
            }
        }
    }

    private fun setUpNewImageListener() {
        fab.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    private fun resizeImage(selectedImage: Uri): Bitmap? {
        return getBitmapFromUri(selectedImage)?.let {
            val scaleFactor = Math.max(
                it.width.toFloat() / imageView.width.toFloat(),
                it.height.toFloat() / imageView.height.toFloat())

            Bitmap.createScaledBitmap(it,
                (it.width / scaleFactor).toInt(),
                (it.height / scaleFactor).toInt(),
                true)
        }
    }

    private fun getBitmapFromUri(filePath: Uri): Bitmap? {
        return MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
    }
}
