package com.example.switchact1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SecondActivity_NAO_DA_ERRO_HTTPS : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val buttonClick = findViewById<Button>(R.id.button)
        buttonClick.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
            println("chamou")
        }

        // Obtain reference to the ListView
        val listView = findViewById<ListView>(R.id.list_view)

        // Use the GlobalScope to perform the network call
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // URL of your endpoint
                val endpointUrl = "https://45.237.118.118:4000"

                // Make the network call and get the JSON results
                val jsonString = fetchDataFromEndpoint(endpointUrl)

                // Parse JSON and get the data you want to display in the ListView
                val data = parseJsonResult(jsonString)

                // Update the main UI on the main thread using runOnUiThread
                runOnUiThread {
                    // Create an ArrayAdapter to associate the data with the ListView
                    val adapter = ArrayAdapter(this@SecondActivity_NAO_DA_ERRO_HTTPS, android.R.layout.simple_list_item_1, data)

                    // Set the adapter for the ListView
                    listView.adapter = adapter
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    // Modify the function to accept the custom SSL socket factory
    private fun fetchDataFromEndpoint(endpointUrl: String): String {
        // Initialize SSL context with a custom TrustManager that trusts all certificates
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create the connection
        val connection = URL(endpointUrl).openConnection() as HttpsURLConnection
        connection.sslSocketFactory = sslContext.socketFactory

        // Disable hostname verification (for testing purposes)
        connection.hostnameVerifier = HostnameVerifier { _, _ -> true }
        return try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }

            result.toString()
        } finally {
            connection.disconnect()
        }
    }

    // Your existing parseJsonResult function
    private fun parseJsonResult(jsonResult: String): Array<String> {
        // Implementation of your existing parseJsonResult function
        // ...

        return emptyArray() // Replace with your actual implementation
    }
}
