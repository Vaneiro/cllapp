package com.example.cllapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var folderNameEditText: EditText
    private lateinit var addFolderButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderAdapter

    private val sharedPrefName = "folders_pref"
    private val foldersKey = "folders_list"

    private var folders = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        folderNameEditText = findViewById(R.id.folderNameEditText)
        addFolderButton = findViewById(R.id.addFolderButton)
        recyclerView = findViewById(R.id.folderRecyclerView)

        // Загрузка папок из SharedPreferences
        folders = loadFolders().toMutableList()

        adapter = FolderAdapter(folders) { folderName ->
            openPhotoActivity(folderName)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addFolderButton.setOnClickListener {
            val folderName = folderNameEditText.text.toString().trim()
            if (folderName.isNotEmpty()) {
                if (folders.contains(folderName)) {
                    Toast.makeText(this, "Папка с таким именем уже существует", Toast.LENGTH_SHORT).show()
                } else {
                    folders.add(folderName)
                    saveFolders(folders)
                    adapter.notifyItemInserted(folders.size - 1)
                    folderNameEditText.text.clear()
                    openPhotoActivity(folderName)
                }
            } else {
                Toast.makeText(this, "Введите имя папки", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openPhotoActivity(folderName: String) {
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra("folder_name", folderName)
        startActivity(intent)
    }

    private fun loadFolders(): List<String> {
        val prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(foldersKey, null) ?: return emptyList()

        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    private fun saveFolders(list: List<String>) {
        val prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val jsonArray = JSONArray()
        for (item in list) {
            jsonArray.put(item)
        }
        editor.putString(foldersKey, jsonArray.toString())
        editor.apply()
    }
}
