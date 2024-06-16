//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.touristadminapp.R
//import com.squareup.picasso.Picasso
//
//class CityAdapter(
//    private val cities: List<City>,
//    private val onDeleteClick: (City) -> Unit,
//    private val onEditClick: (City) -> Unit
//) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val city = cities[position]
//        holder.bind(city, onDeleteClick, onEditClick)
//    }
//
//    override fun getItemCount(): Int {
//        return cities.size
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
//        private val cityImageView: ImageView = itemView.findViewById(R.id.cityImageView)
//        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
//        private val editButton: Button = itemView.findViewById(R.id.editButton)
//
//        fun bind(city: City, onDeleteClick: (City) -> Unit, onEditClick: (City) -> Unit) {
//            cityNameTextView.text = city.name
//            // Load the city image using an image loading library like Glide or Picasso
//            // Glide.with(itemView.context).load(city.imageUrl).into(cityImageView)
//            Picasso.get().load(city.imageUrl).into(cityImageView)
//
//
//
//            deleteButton.setOnClickListener { onDeleteClick(city) }
//            editButton.setOnClickListener { onEditClick(city) }
//        }
//    }
//}

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.touristadminapp.R
import com.squareup.picasso.Picasso

class CityAdapter(
    private var cities: List<City>,
    private val onDeleteClick: (City) -> Unit,
    private val onEditClick: (City) -> Unit
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.bind(city, onDeleteClick, onEditClick)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    fun updateCities(newCities: List<City>) {
        this.cities = newCities
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        private val cityImageView: ImageView = itemView.findViewById(R.id.cityImageView)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val editButton: Button = itemView.findViewById(R.id.editButton)

        fun bind(city: City, onDeleteClick: (City) -> Unit, onEditClick: (City) -> Unit) {
            cityNameTextView.text = city.name
            // Load the city image using an image loading library like Glide or Picasso
            // Glide.with(itemView.context).load(city.imageUrl).into(cityImageView)
            Picasso.get().load(city.imageUrl).into(cityImageView)

            deleteButton.setOnClickListener { onDeleteClick(city) }
            editButton.setOnClickListener { onEditClick(city) }
        }
    }
}



//fun bind(city: City) {
//            cityNameTextView.text = city.name
//            cityDescriptionTextView.text = city.description
//            Picasso.get().load(city.imageUrl).into(cityImageView)
//        }
//    }
//}