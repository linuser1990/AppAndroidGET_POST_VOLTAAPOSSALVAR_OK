package com.example.switchact1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonClick = findViewById<Button>(R.id.button_click)
        val buttonLerImagem = findViewById<Button>(R.id.btnLerImagem)

        buttonClick.setOnClickListener {
            val intent = Intent(this, ListaActivity::class.java)
            startActivity(intent)
            println("chamou")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lista_activity -> {
                startActivity(Intent(this, ListaActivity::class.java))
                return true
            }
            R.id.menu_form_activity -> {
                startActivity(Intent(this, FormActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
