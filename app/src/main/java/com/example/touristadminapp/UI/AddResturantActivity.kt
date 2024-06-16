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
import com.example.touristadminapp.model.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddRestaurantActivity : AppCompatActivity() {

    private lateinit var etRestaurantName: EditText
    private lateinit var etRestaurantAddress: EditText
    private lateinit var ivRestaurantImage: ImageView
    private var restaurantImageUri: Uri? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resturant)

        etRestaurantName = findViewById(R.id.etRestaurantName)
        etRestaurantAddress = findViewById(R.id.etRestaurantAddress)
        ivRestaurantImage = findViewById(R.id.ivRestaurantImage)
        val btnSelectRestaurantImage: Button = findViewById(R.id.btnSelectRestaurantImage)
        val btnUploadRestaurant: Button = findViewById(R.id.btnUploadRestaurant)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("restaurants")

        btnSelectRestaurantImage.setOnClickListener {
            openRestaurantImageChooser()
        }

        btnUploadRestaurant.setOnClickListener {
            uploadRestaurantData()
        }
    }

    private fun openRestaurantImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_RESTAURANT_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_RESTAURANT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            restaurantImageUri = data.data
            ivRestaurantImage.setImageURI(restaurantImageUri)
        }
    }

    private fun uploadRestaurantData() {
        val restaurantName = etRestaurantName.text.toString().trim()
        val restaurantAddress = etRestaurantAddress.text.toString().trim()

        if (restaurantName.isEmpty() || restaurantAddress.isEmpty() || restaurantImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val fileReference = storageRef.child("${System.currentTimeMillis()}.jpg")
        fileReference.putFile(restaurantImageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                fileReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        val restaurant = Restaurant(
                            imageUrl = downloadUri.toString(),
                            name = restaurantName,
                            address = restaurantAddress
                        )

                        val cityId = intent.getStringExtra("cityId") ?: return@addOnCompleteListener

                        db.collection("cities")
                            .document(cityId)
                            .update("whatToEat", FieldValue.arrayUnion(restaurant))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Restaurant added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add restaurant", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Restaurant image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_RESTAURANT_IMAGE_REQUEST = 4
    }
}
