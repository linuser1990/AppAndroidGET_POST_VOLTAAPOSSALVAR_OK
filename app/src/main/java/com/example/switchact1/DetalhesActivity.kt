package com.example.switchact1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException
import org.json.JSONObject

class DetalhesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes)

        // Obter o código passado como extra
        val codigo = intent.getIntExtra("codigo", -1)

        // Montar a URL com base no código
        val endpointUrl = "http://45.237.118.118:4000/$codigo"

        // Use o GlobalScope para realizar a chamada de rede
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Realize a chamada de rede e obtenha os resultados JSON
                val jsonString = fetchDataFromEndpoint(endpointUrl)

                // Parse JSON e obtenha os dados que você deseja exibir
                val dados = parseJsonResult(jsonString)

                // Atualize a UI principal na thread principal usando runOnUiThread
                runOnUiThread {
                    // Atualize a UI com os detalhes do item, por exemplo, usando TextViews
                    val detalhesTextView = findViewById<TextView>(R.id.text_detalhes)
                    detalhesTextView.text = "Detalhes do Item:\n$dados"
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun parseJsonResult(jsonResult: String): String {
        var detalhes = ""

        if (jsonResult.isNotEmpty()) {
            try {
                // Parse JSON object
                val jsonObject = JSONObject(jsonResult)
                val codigo = jsonObject.getInt("codigo")
                val nome = jsonObject.getString("nome")
                val cpf = jsonObject.getString("cpf")

                detalhes = "Código: $codigo\nNome: $nome\nCPF: $cpf"
            } catch (e: JSONException) {
                // Handle JSON parsing error
                e.printStackTrace()
            }
        }

        return detalhes
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
