package com.example.touristadminapp.UI

import City
import CityAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.touristadminapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class MainActivity : AppCompatActivity() {
    private lateinit var cities: List<City>

    private lateinit var etCityName: EditText
    private lateinit var etCityDescription: EditText
    private lateinit var ivCityImage: ImageView
    private var cityImageUri: Uri? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var cityRecyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCityName = findViewById(R.id.etCityName)
        etCityDescription = findViewById(R.id.etCityDescription)
        ivCityImage = findViewById(R.id.ivCityImage)
        val btnSelectCityImage: Button = findViewById(R.id.btnSelectCityImage)
        val btnUploadCity: Button = findViewById(R.id.btnUploadCity)
        val btnAddHotel: Button = findViewById(R.id.btnAddHotel)
        val btnAddPlace: Button = findViewById(R.id.btnAddPlace)
        val btnAddRestaurant: Button = findViewById(R.id.btnAddRestaurant)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("cities")
        auth = FirebaseAuth.getInstance()

        btnSelectCityImage.setOnClickListener {
            openCityImageChooser()
        }

        btnUploadCity.setOnClickListener {
            uploadCityData()
        }

        btnAddHotel.setOnClickListener {
            openAddHotelActivity()
        }

        btnAddPlace.setOnClickListener {
            openAddPlaceActivity()
        }

        btnAddRestaurant.setOnClickListener {
            openAddRestaurantActivity()
        }

        // Initialize RecyclerView and Adapter
        cityRecyclerView = findViewById(R.id.cityRecyclerView)
        cityAdapter = CityAdapter(
            ArrayList(),
            { city -> deleteCity(city) }, // onDeleteClick lambda
            { city -> editCity(city) }    // onEditClick lambda
        )

        // Set layout manager and adapter for RecyclerView
        cityRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cityAdapter
        }

        // Anonymous sign-in
//        signInAnonymously()
    }


    private fun fetchCitiesFromFirestore() {
        db.collection("cities")
            .get()
            .addOnSuccessListener { documents ->
                val cities = ArrayList<City>()
                for (document in documents) {
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val imageUrl = document.getString("imageUrl")
                    if (name != null && description != null && imageUrl != null) {
                        cities.add(City(null, name, description, imageUrl))
                    }
                }
                cityAdapter.updateCities(cities)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch cities: $exception", Toast.LENGTH_SHORT)
                    .show()
            }
    }

//    private fun signInAnonymously() {
//        auth.signInAnonymously()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show()
//                    fetchCitiesFromFirestore()
//                } else {
//                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun openCityImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_CITY_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CITY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            cityImageUri = data.data
            ivCityImage.setImageURI(cityImageUri)
        }
    }

    private fun uploadCityData() {
        val cityName = etCityName.text.toString().trim()
        val cityDescription = etCityDescription.text.toString().trim()

        if (cityName.isEmpty() || cityDescription.isEmpty() || cityImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val fileReference = storageRef.child("${System.currentTimeMillis()}.jpg")
        fileReference.putFile(cityImageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                fileReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        val city = City(
                            null,
                            cityName,
                            cityDescription,
                            downloadUri.toString()
                        )

                        db.collection("cities")
                            .document(cityName)
                            .set(city)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "City uploaded successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchCitiesFromFirestore()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to upload city", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "City image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openAddHotelActivity() {
        val intent = Intent(this, AddHotelActivity::class.java)
        val cityId = etCityName.text.toString().trim()
        intent.putExtra("cityId", cityId)
        startActivity(intent)
    }

    private fun openAddPlaceActivity() {
        val intent = Intent(this, AddPlaceActivity::class.java)
        val cityId = etCityName.text.toString().trim()
        intent.putExtra("cityId", cityId)
        startActivity(intent)
    }

    private fun openAddRestaurantActivity() {
        val intent = Intent(this, AddRestaurantActivity::class.java)
        val cityId = etCityName.text.toString().trim()
        intent.putExtra("cityId", cityId)
        startActivity(intent)
    }

    private fun deleteCity(city: City) {
        city.name?.let {
            db.collection("cities")
                .document(it)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "City deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchCitiesFromFirestore()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete city", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun editCity(city: City) {
        val intent = Intent(this, EditCityActivity::class.java)
        intent.putExtra("city", city)
        startActivity(intent)
    }

    companion object {
        private const val PICK_CITY_IMAGE_REQUEST = 1
    }
}
