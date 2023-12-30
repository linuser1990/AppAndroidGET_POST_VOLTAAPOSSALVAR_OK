package com.example.switchact1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

        setTitle("Editar")

        // Obter o código passado como extra
        val codigo = intent.getIntExtra("codigo", -1)

        // Associar elementos do layout a variáveis
        val codigoTextView = findViewById<TextView>(R.id.text_codigo)
        val nomeEditText = findViewById<EditText>(R.id.edit_nome)
        val cpfEditText = findViewById<EditText>(R.id.edit_cpf)
        val updateButton = findViewById<Button>(R.id.btn_update)

        // Definir o código na TextView
        codigoTextView.text = "Código: $codigo"

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
                    // Atualize a UI com os detalhes do item
                    nomeEditText.setText(dados.nome)
                    cpfEditText.setText(dados.cpf)
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }

        // Configurar o clique do botão Update
        updateButton.setOnClickListener {
            // Obter o nome e o CPF dos EditTexts
            val nomeAtualizado = nomeEditText.text.toString()
            val cpfAtualizado = cpfEditText.text.toString()

            // Executar a solicitação PUT usando a URL do endpoint
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val putUrl = URL(endpointUrl)
                    val connection = putUrl.openConnection() as HttpURLConnection
                    connection.requestMethod = "PUT"
                    connection.doOutput = true

                    // Construir os parâmetros para a solicitação PUT
                    val params = "nome=$nomeAtualizado&cpf=$cpfAtualizado"
                    connection.outputStream.write(params.toByteArray())

                    // Obter a resposta do servidor (opcional)
                    val responseCode = connection.responseCode
                    // Você pode verificar o responseCode para garantir que a atualização foi bem-sucedida


                    // Fechar a conexão
                    connection.disconnect()

                    // Verificar se a atualização foi bem-sucedida
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Atualizado com sucesso! Exibir Toast
                        runOnUiThread {
                            Toast.makeText(
                                this@DetalhesActivity,
                                "Atualizado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Redirecionar para a ListaActivity
                            val intent = Intent(this@DetalhesActivity, ListaActivity::class.java)
                            startActivity(intent)
                            finish() // opcional, dependendo do comportamento desejado
                        }
                    } else {
                        // Handle error - exibir Toast ou tomar ação apropriada
                    }

                } catch (e: Exception) {
                    // Handle error
                    e.printStackTrace()
                }
            }
        }
    }

    private fun parseJsonResult(jsonResult: String): Detalhes {
        var detalhes = Detalhes("", "", "")

        if (jsonResult.isNotEmpty()) {
            try {
                // Parse JSON object
                val jsonObject = JSONObject(jsonResult)
                val codigo = jsonObject.getInt("codigo")
                val nome = jsonObject.getString("nome")
                val cpf = jsonObject.getString("cpf")

                detalhes = Detalhes(codigo.toString(), nome, cpf)
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

    data class Detalhes(val codigo: String, val nome: String, val cpf: String)


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
