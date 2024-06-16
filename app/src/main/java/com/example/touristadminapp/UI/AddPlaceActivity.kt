package com.example.touristadminapp.UI


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touristadminapp.R
import com.example.touristadminapp.model.Place
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddPlaceActivity : AppCompatActivity() {

    private lateinit var etPlaceName: EditText
    private lateinit var etPlaceDescription: EditText
    private lateinit var ivPlaceImage: ImageView
    private var placeImageUri: Uri? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_place_activity)

        etPlaceName = findViewById(R.id.etPlaceName)
        etPlaceDescription = findViewById(R.id.etPlaceDescription)
        ivPlaceImage = findViewById(R.id.ivPlaceImage)
        val btnSelectPlaceImage: Button = findViewById(R.id.btnSelectPlaceImage)
        val btnUploadPlace: Button = findViewById(R.id.btnUploadPlace)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("places")

        btnSelectPlaceImage.setOnClickListener {
            openPlaceImageChooser()
        }

        btnUploadPlace.setOnClickListener {
            uploadPlaceData()
        }
    }

    private fun openPlaceImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_PLACE_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PLACE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            placeImageUri = data.data
            ivPlaceImage.setImageURI(placeImageUri)
        }
    }

    private fun uploadPlaceData() {
        val placeName = etPlaceName.text.toString().trim()
        val placeDescription = etPlaceDescription.text.toString().trim()

        if (placeName.isEmpty() || placeDescription.isEmpty() || placeImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val fileReference = storageRef.child("${System.currentTimeMillis()}.jpg")
        fileReference.putFile(placeImageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                fileReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        val place = Place(
                            imageUrl = downloadUri.toString(),
                            name = placeName,
                            description = placeDescription
                        )

                        val cityId = intent.getStringExtra("cityId") ?: return@addOnCompleteListener

                        db.collection("cities")
                            .document(cityId)
                            .update("whatToDo", FieldValue.arrayUnion(place))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Place added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add place", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Place image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_PLACE_IMAGE_REQUEST = 3
    }
}
