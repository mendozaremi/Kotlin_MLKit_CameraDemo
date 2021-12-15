package com.remi.cameraapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Build button that launches camera app
        // setup onclick listener first findviewbyid launch camera app
        findViewById<Button>(R.id.button).setOnClickListener {
            // TODO: Launch camera app

            // Create intent that launches camera app
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // try catch maybe using phone without camera
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
            // display error state to the user
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Grab bitmap from image that was taken in camera
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Set bitmap s imageview image
            findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)

            // prepare bitmap for ML Kit API
            val imageForMLKit = InputImage.fromBitmap(imageBitmap, 0)

            // Utilize image labeling
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(imageForMLKit)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    Log.i("Remi", "Successfully processed image through ML Kit")
                    // Loop through labels
                    for (label in labels) {
                        // What was detected in the image
                        val text = label.text
                        // the confidence of what was detected
                        val confidence = label.confidence
                        Log.i("Remi", "detected:" + text + "with confidence level: " + confidence)
                    }

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.i("Remi", "Error processing image")
                }
        }
    }
}