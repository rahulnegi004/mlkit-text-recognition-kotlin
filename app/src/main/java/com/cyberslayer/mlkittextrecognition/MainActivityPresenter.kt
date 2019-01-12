package com.cyberslayer.mlkittextrecognition
import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

class MainActivityPresenter(val view: View) {

    fun runTextRecognition(selectedImage: Bitmap) {
        view.showProgress()
        val image = FirebaseVisionImage.fromBitmap(selectedImage)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                e.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        view.hideProgress()
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            view.showNoTextMessage()
            return
        }
        var dateText = ""
        blocks.forEach { block ->
            block.lines.forEach { line ->
                line.elements.forEach { element ->
                    dateText += "\n"+ element.text
                }
            }
        }
        view.showText(dateText)
    }

    interface View {
        fun showNoTextMessage()
        fun showText(text: String)
        fun showProgress()
        fun hideProgress()
    }
}