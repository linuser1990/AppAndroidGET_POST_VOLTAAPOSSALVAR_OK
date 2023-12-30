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

class ListaActivityB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val buttonClick = findViewById<Button>(R.id.button)
        buttonClick.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
            println("chamou")
        }

        // Obtenha referência ao ListView
        val listView = findViewById<ListView>(R.id.list_view)

        // Use o GlobalScope para realizar a chamada de rede
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // URL do seu endpoint
                val endpointUrl = "https://45.237.118.118:4000"

                // Realize a chamada de rede e obtenha os resultados JSON
                val jsonString = fetchDataFromEndpoint(endpointUrl)

                // Parse JSON e obtenha os dados que você deseja exibir no ListView
                val dados = parseJsonResult(jsonString)

                // Atualize a UI principal na thread principal usando runOnUiThread
                runOnUiThread {
                    // Crie um ArrayAdapter para associar os dados ao ListView
                    val adapter = ArrayAdapter(this@ListaActivityB, android.R.layout.simple_list_item_1, dados)

                    // Defina o adaptador para o ListView
                    listView.adapter = adapter
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun parseJsonResult(jsonResult: String): Array<String> {
        val resultsList = mutableListOf<String>()

        if (jsonResult.isNotEmpty()) {
            try {
                // Attempt to parse JSON array
                val jsonArray = JSONArray(jsonResult)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val codigo = jsonObject.getInt("codigo")
                    val nome = jsonObject.getString("nome")

                    resultsList.add("Código: $codigo\nNome: $nome")
                }
            } catch (e: JSONException) {
                // Handle JSON parsing error
                e.printStackTrace()
            }
        }

        return resultsList.toTypedArray()
    }

    // Função para buscar dados JSON do endpoint especificado
    private fun fetchDataFromEndpoint(endpointUrl: String): String {
        val url = URL(endpointUrl)
        val connection = url.openConnection() as HttpURLConnection

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
}


