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
import com.example.touristadminapp.model.Hotel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddHotelActivity : AppCompatActivity() {

    private lateinit var etHotelName: EditText
    private lateinit var etHotelDescription: EditText
    private lateinit var etHotelContact: EditText
    private lateinit var ivHotelImage: ImageView
    private var hotelImageUri: Uri? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_hotel)

        etHotelName = findViewById(R.id.etHotelName)
        etHotelDescription = findViewById(R.id.etHotelDescription)
        etHotelContact = findViewById(R.id.etHotelContact)
        ivHotelImage = findViewById(R.id.ivHotelImage)
        val btnSelectHotelImage: Button = findViewById(R.id.btnSelectHotelImage)
        val btnUploadHotel: Button = findViewById(R.id.btnUploadHotel)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("hotels")

        btnSelectHotelImage.setOnClickListener {
            openHotelImageChooser()
        }

        btnUploadHotel.setOnClickListener {
            uploadHotelData()
        }

    }

    private fun openHotelImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_HOTEL_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_HOTEL_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            hotelImageUri = data.data
            ivHotelImage.setImageURI(hotelImageUri)
        }
    }

    private fun uploadHotelData() {
        val hotelName = etHotelName.text.toString().trim()
        val hotelDescription = etHotelDescription.text.toString().trim()
        val hotelContact = etHotelContact.text.toString().trim()

        if (hotelName.isEmpty() || hotelDescription.isEmpty() || hotelContact.isEmpty() || hotelImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val fileReference = storageRef.child("${System.currentTimeMillis()}.jpg")
        fileReference.putFile(hotelImageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                fileReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        val hotel = Hotel(
                            imageUrl = downloadUri.toString(),
                            name = hotelName,
                            description = hotelDescription,
                            contactNumber = hotelContact
                        )

                        val cityId = intent.getStringExtra("cityId") ?: return@addOnCompleteListener

                        db.collection("cities")
                            .document(cityId)
                            .update("whereToStay", FieldValue.arrayUnion(hotel))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Hotel added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add hotel", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Hotel image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_HOTEL_IMAGE_REQUEST = 2
    }
}
