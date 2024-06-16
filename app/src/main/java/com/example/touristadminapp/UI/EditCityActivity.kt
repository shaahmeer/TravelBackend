package com.example.touristadminapp.UI

import City
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touristadminapp.R
import com.google.firebase.firestore.FirebaseFirestore

class EditCityActivity : AppCompatActivity() {

    private lateinit var editCityName: EditText
    private lateinit var editCityDescription: EditText
    private lateinit var btnUpdateCity: Button
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_city)

        editCityName = findViewById(R.id.editCityName)
        editCityDescription = findViewById(R.id.editCityDescription)
        btnUpdateCity = findViewById(R.id.btnUpdateCity)
        db = FirebaseFirestore.getInstance()

        val city = intent.getParcelableExtra<City>("city")
        city?.let { loadCityDetails(it) }

        btnUpdateCity.setOnClickListener {
            city?.let { updateCity(it) }
        }
    }

    private fun loadCityDetails(city: City) {
        editCityName.setText(city.name)
        editCityDescription.setText(city.description)
    }

    private fun updateCity(city: City) {
        val newName = editCityName.text.toString().trim()
        val newDescription = editCityDescription.text.toString().trim()

        if (newName.isEmpty() || newDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        city.documentId?.let {
            db.collection("cities")
                .document(it)
                .update(
                    mapOf(
                        "name" to newName,
                        "description" to newDescription
                    )
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "City updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to update city: $exception", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
