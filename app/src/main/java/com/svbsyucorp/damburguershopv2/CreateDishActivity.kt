package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.util.Log
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

class CreateDishActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var etExtra: EditText
    private lateinit var etImageUrl: EditText
    private lateinit var etRating: EditText
    private lateinit var btnSave: Button

    private lateinit var backArrow: ImageView
    private lateinit var spinnerCategory: Spinner
    private var categories = mutableListOf<CategoryModel>()
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_dish)

        database = FirebaseDatabase.getInstance()

        etName = findViewById(R.id.et_name)
        etPrice = findViewById(R.id.et_price)
        etDescription = findViewById(R.id.et_description)
        etExtra = findViewById(R.id.et_extra)
        etImageUrl = findViewById(R.id.et_image_url)
        etRating = findViewById(R.id.et_rating)
        btnSave = findViewById(R.id.btn_save)

        backArrow = findViewById(R.id.back_arrow)
        spinnerCategory = findViewById(R.id.spinner_category)
        
        createCategoriesIfNotExist()
        loadCategories()

        btnSave.setOnClickListener {
            saveDish()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

    private fun loadCategories() {
        database.reference.child("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("CreateDish", "Snapshot exists: ${snapshot.exists()}")
                    Log.d("CreateDish", "Children count: ${snapshot.childrenCount}")
                    
                    categories.clear()
                    for (categorySnapshot in snapshot.children) {
                        Log.d("CreateDish", "Category key: ${categorySnapshot.key}")
                        Log.d("CreateDish", "Category value: ${categorySnapshot.value}")
                        
                        val category = categorySnapshot.getValue(CategoryModel::class.java)
                        if (category != null) {
                            Log.d("CreateDish", "Category loaded: ${category.title}")
                            categories.add(category)
                        }
                    }
                    Log.d("CreateDish", "Total categories loaded: ${categories.size}")
                    setupSpinner()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CreateDish", "Error loading categories: ${error.message}")
                    Toast.makeText(this@CreateDishActivity, "Error cargando categorías", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSpinner() {
        val categoryNames = categories.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun createCategoriesIfNotExist() {
        val categoriesRef = database.reference.child("Category")
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val defaultCategories = listOf(
                        CategoryModel(0, "Hamburguesa"),
                        CategoryModel(1, "Burrito"),
                        CategoryModel(2, "Pizza"),
                        CategoryModel(3, "Brocheta"),
                        CategoryModel(4, "Fusion")
                    )
                    
                    defaultCategories.forEach { category ->
                        categoriesRef.child(category.id.toString()).setValue(category)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
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

        val dish = ItemModel(
            title = name,
            price = price,
            description = description,
            categoryId = selectedCategory.id.toString(),
            picUrl = listOf(imageUrl),
            rating = rating,
            extra = extra
        )

        val itemsRef = database.reference.child("Items")
        val newItemRef = itemsRef.push()
        
        newItemRef.setValue(dish)
            .addOnSuccessListener {
                Toast.makeText(this, "Plato creado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear plato", Toast.LENGTH_SHORT).show()
            }
    }
}