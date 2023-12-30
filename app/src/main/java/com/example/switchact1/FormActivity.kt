package com.example.switchact1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        //altera o nome no topo da activity
        setTitle("Cadastro")

        val editTextNome = findViewById<EditText>(R.id.editTextNome)
        val editTextCPF = findViewById<EditText>(R.id.editTextCPF)
        val submitButton = findViewById<Button>(R.id.submitButton)

        var nomeFieldValue by mutableStateOf("")
        var cpfFieldValue by mutableStateOf("")

        // Update Compose state when the EditText value changes
        editTextNome.addTextChangedListener { editable ->
            nomeFieldValue = editable.toString()
        }

        // Update Compose state when the EditText value changes
        editTextCPF.addTextChangedListener { editable ->
            cpfFieldValue = editable.toString()
        }

        // Handle button click
        submitButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    sendDataToEndpoint(nomeFieldValue, cpfFieldValue)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendDataToEndpoint(nome: String, cpf: String) {
        try {
            val url = "http://45.237.118.118:4000"
            val connection = URL(url).openConnection() as HttpURLConnection

            // Set the request method to POST
            connection.requestMethod = "POST"

            // Set the content type to "application/x-www-form-urlencoded"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            // Enable output stream to send data to the server
            connection.doOutput = true

            // Construct the data to be sent in the request body
            val data = "nome=${URLEncoder.encode(nome, "UTF-8")}&cpf=${URLEncoder.encode(cpf, "UTF-8")}"

            // Write the data to the output stream
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(data.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            outputStream.close()

            // Get the response code from the server
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                runOnUiThread {
                    Toast.makeText(
                        this@FormActivity,
                        "Salvo com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to the ListaActivity
                    val intent = Intent(this@FormActivity, ListaActivity::class.java)
                    startActivity(intent)
                }
            } else {
                // Handle unsuccessful response
                // You may want to throw an exception or handle the error accordingly
            }

        } catch (e: Exception) {
            // Handle exceptions, e.g., IOException
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                // Handle the click on the home icon
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
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
