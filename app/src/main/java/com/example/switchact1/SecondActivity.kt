package com.example.switchact1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
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

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        //altera o nome no topo da activity
        setTitle("Lista")

        val buttonClick = findViewById<Button>(R.id.button)
        buttonClick.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
            println("chamou")
        }

        // Obtenha referência ao ListView
        val listView = findViewById<ListView>(R.id.list_view)

        // Ouvinte de clique para itens do ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = listView.getItemAtPosition(position) as String

            // Extrair o código do item selecionado
            val codigo = selectedItem.substringAfter("Código: ").substringBefore("\n").toInt()

            // Criar uma Intent para a nova atividade e passar o código como extra
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("codigo", codigo)
            startActivity(intent)
        }

        // Ouvinte de clique longo para itens do ListView
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedItem = listView.getItemAtPosition(position) as String

            // Extrair o código do item selecionado
            val codigo = selectedItem.substringAfter("Código: ").substringBefore("\n").toInt()

            // Exibir diálogo de confirmação para exclusão
            showDeleteConfirmationDialog(codigo)

            // Indicar que o evento de clique longo foi manipulado
            true
        }

        // Use o GlobalScope para realizar a chamada de rede
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // URL do seu endpoint
                val endpointUrl = "http://45.237.118.118:4000/"

                // Realize a chamada de rede e obtenha os resultados JSON
                val jsonString = fetchDataFromEndpoint(endpointUrl)

                // Parse JSON e obtenha os dados que você deseja exibir no ListView
                val dados = parseJsonResult(jsonString)

                // Atualize a UI principal na thread principal usando runOnUiThread
                runOnUiThread {
                    // Crie um ArrayAdapter para associar os dados ao ListView
                    val adapter = ArrayAdapter(this@SecondActivity, android.R.layout.simple_list_item_1, dados)

                    // Defina o adaptador para o ListView
                    listView.adapter = adapter
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun showDeleteConfirmationDialog(codigo: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmação")
            .setMessage("Deseja excluir o item com código $codigo?")
            .setPositiveButton("Sim") { _, _ ->
                // Usuário escolheu "Sim", execute o endpoint de exclusão
                deleteItem(codigo)
            }
            .setNegativeButton("Não") { dialog, _ ->
                // Usuário escolheu "Não", fechar o diálogo
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun deleteItem(codigo: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // URL do endpoint para exclusão
                val deleteEndpointUrl = "http://45.237.118.118:4000/$codigo"

                // Realize a chamada de rede para excluir o item
                val responseCode = deleteItemFromEndpoint(deleteEndpointUrl)

                // Atualize a UI principal na thread principal usando runOnUiThread
                runOnUiThread {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Exclusão bem-sucedida, atualize a lista
                        updateList()
                    } else {
                        // Trate o erro, se necessário
                        // Exemplo: exibir uma mensagem de erro
                        Toast.makeText(this@SecondActivity, "Falha ao excluir item", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun updateList() {
        // Implemente a lógica para atualizar a lista após a exclusão
        // (por exemplo, fazendo uma nova chamada à API)
        // e atualizando o adaptador do ListView
        // ...
        fetchDataAndRefreshList()

        // Exiba um Toast informando que o item foi excluído com sucesso
        Toast.makeText(this, "Item excluído com sucesso!!!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteItemFromEndpoint(deleteEndpointUrl: String): Int {
        val url = URL(deleteEndpointUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"

        return connection.responseCode
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

    private fun fetchDataAndRefreshList() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // URL do seu endpoint
                val endpointUrl = "http://45.237.118.118:4000/"

                // Realize a chamada de rede e obtenha os resultados JSON
                val jsonString = fetchDataFromEndpoint(endpointUrl)

                // Parse JSON e obtenha os dados que você deseja exibir no ListView
                val dados = parseJsonResult(jsonString)

                // Atualize a UI principal na thread principal usando runOnUiThread
                runOnUiThread {
                    // Crie um ArrayAdapter para associar os dados ao ListView
                    val adapter = ArrayAdapter(this@SecondActivity, android.R.layout.simple_list_item_1, dados)

                    // Obtenha referência ao ListView
                    val listView = findViewById<ListView>(R.id.list_view)

                    // Defina o adaptador para o ListView
                    listView.adapter = adapter
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
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
            R.id.menu_second_activity -> {
                startActivity(Intent(this, SecondActivity::class.java))
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


