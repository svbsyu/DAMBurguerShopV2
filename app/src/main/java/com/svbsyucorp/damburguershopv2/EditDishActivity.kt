package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.svbsyucorp.damburguershopv2.domain.CategoryModel
import com.svbsyucorp.damburguershopv2.domain.ItemModel

class EditDishActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var etExtra: EditText
    private lateinit var etImageUrl: EditText
    private lateinit var etRating: EditText
    private lateinit var btnSave: Button
    private lateinit var btnVolver: ImageView
    private lateinit var backArrow: ImageView
    private lateinit var spinnerCategory: Spinner
    private var categories = mutableListOf<CategoryModel>()
    private lateinit var database: FirebaseDatabase
    private lateinit var dishId: String
    private lateinit var currentDish: ItemModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dish)

        database = FirebaseDatabase.getInstance()
        
        dishId = intent.getStringExtra("DISH_ID") ?: ""
        val dishTitle = intent.getStringExtra("DISH_TITLE") ?: ""
        val dishPrice = intent.getDoubleExtra("DISH_PRICE", 0.0)
        val dishDescription = intent.getStringExtra("DISH_DESCRIPTION") ?: ""
        val dishExtra = intent.getStringExtra("DISH_EXTRA") ?: ""
        val dishImageUrl = intent.getStringExtra("DISH_IMAGE_URL") ?: ""
        val dishRating = intent.getDoubleExtra("DISH_RATING", 0.0)
        val dishCategoryId = intent.getStringExtra("DISH_CATEGORY_ID") ?: ""

        currentDish = ItemModel(
            id = dishId,
            title = dishTitle,
            price = dishPrice,
            description = dishDescription,
            extra = dishExtra,
            picUrl = listOf(dishImageUrl),
            rating = dishRating,
            categoryId = dishCategoryId
        )

        etName = findViewById(R.id.et_name)
        etPrice = findViewById(R.id.et_price)
        etDescription = findViewById(R.id.et_description)
        etExtra = findViewById(R.id.et_extra)
        etImageUrl = findViewById(R.id.et_image_url)
        etRating = findViewById(R.id.et_rating)
        btnSave = findViewById(R.id.btn_save)
        btnVolver = findViewById(R.id.btn_volver)
        backArrow = findViewById(R.id.back_arrow)
        spinnerCategory = findViewById(R.id.spinner_category)

        loadCategories()
        fillFields()

        btnSave.setOnClickListener {
            saveDish()
        }

        btnVolver.setOnClickListener {
            finish()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

    private fun fillFields() {
        etName.setText(currentDish.title)
        etPrice.setText(currentDish.price.toString())
        etDescription.setText(currentDish.description)
        etExtra.setText(currentDish.extra)
        if (currentDish.picUrl.isNotEmpty()) {
            etImageUrl.setText(currentDish.picUrl[0])
        }
        etRating.setText(currentDish.rating.toString())
    }

    private fun loadCategories() {
        database.reference.child("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categories.clear()
                    for (categorySnapshot in snapshot.children) {
                        val category = categorySnapshot.getValue(CategoryModel::class.java)
                        if (category != null) {
                            categories.add(category)
                        }
                    }
                    setupSpinner()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditDishActivity, "Error cargando categorías", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSpinner() {
        val categoryNames = categories.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Seleccionar la categoría actual
        val currentCategoryIndex = categories.indexOfFirst { it.id.toString() == currentDish.categoryId }
        if (currentCategoryIndex != -1) {
            spinnerCategory.setSelection(currentCategoryIndex)
        }
    }

    private fun saveDish() {
        val name = etName.text.toString().trim()
        val priceText = etPrice.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val extra = etExtra.text.toString().trim()
        val imageUrl = etImageUrl.text.toString().trim()
        val ratingText = etRating.text.toString().trim()

        val selectedPosition = spinnerCategory.selectedItemPosition
        if (selectedPosition == -1) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedCategory = categories[selectedPosition]

        if (name.isEmpty() || priceText.isEmpty() || description.isEmpty() || imageUrl.isEmpty() || ratingText.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val rating = ratingText.toDoubleOrNull()
        if (rating == null || rating < 0.0 || rating > 5.0) {
            Toast.makeText(this, "Rating debe ser entre 0.0 y 5.0", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedDish = ItemModel(
            id = dishId,
            title = name,
            price = price,
            description = description,
            categoryId = selectedCategory.id.toString(),
            picUrl = listOf(imageUrl),
            rating = rating,
            extra = extra
        )

        database.reference.child("Items").child(dishId).setValue(updatedDish)
            .addOnSuccessListener {
                Toast.makeText(this, "Plato actualizado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar plato", Toast.LENGTH_SHORT).show()
            }
    }
}