package com.example.switchact1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val editText = findViewById<EditText>(R.id.editText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        var textFieldValue by mutableStateOf("")

        // Update Compose state when the EditText value changes
        editText.addTextChangedListener { editable ->
            textFieldValue = editable.toString()
        }

        // Handle button click
        submitButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val textToSend = textFieldValue
                    println(textToSend)
                    sendDataToEndpoint(textToSend)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun sendDataToEndpoint(textToSend: String) {
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
            val data = "nome=${URLEncoder.encode(textToSend, "UTF-8")}"

            // Write the data to the output stream
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(data.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            outputStream.close()

            // Get the response code from the server
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
                // Request was successful
                // You may want to read the response from the server if needed
            } else {
                // Handle unsuccessful response
                // You may want to throw an exception or handle the error accordingly
            }
        } catch (e: Exception) {
            // Handle exceptions, e.g., IOException
            e.printStackTrace()
        }
    }
}
