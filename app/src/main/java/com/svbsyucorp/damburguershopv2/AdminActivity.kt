package com.svbsyucorp.damburguershopv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        auth = FirebaseAuth.getInstance()

        val btnPedidos = findViewById<Button>(R.id.btn_pedidos)
        val btnVerPlatos = findViewById<Button>(R.id.btn_ver_platos)
        val btnCerrarSesion = findViewById<Button>(R.id.btn_cerrar_sesion)
        val backArrow = findViewById<ImageView>(R.id.back_arrow)

        btnPedidos.setOnClickListener {
            Toast.makeText(this, "Pedidos", Toast.LENGTH_SHORT).show()
        }

        btnVerPlatos.setOnClickListener {
            val intent = Intent(this, ManageDishesActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        backArrow.setOnClickListener {
            onBackPressed()
        }
    }
}