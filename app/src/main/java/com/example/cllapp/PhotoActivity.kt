package com.example.cllapp

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class PhotoActivity : AppCompatActivity() {

    private lateinit var folderName: String
    private lateinit var folderNameTextView: TextView
    private lateinit var addPhotoButton: Button
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    private val photoUris = mutableListOf<Uri>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val savedUri = copyImageToInternalStorage(it)
            savedUri?.let { finalUri ->
                photoUris.add(finalUri)
                savePhotoUris(folderName, photoUris)
                photoAdapter.notifyItemInserted(photoUris.size - 1)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        folderName = intent.getStringExtra("folder_name") ?: "Без имени"

        folderNameTextView = findViewById(R.id.folderNameTextView)
        addPhotoButton = findViewById(R.id.addPhotoButton)
        photoRecyclerView = findViewById(R.id.photoRecyclerView)

        folderNameTextView.text = "Папка: $folderName"

        photoUris.addAll(loadPhotoUris(folderName))

        photoAdapter = PhotoAdapter(photoUris) { position ->
            // Удаляем файл с устройства
            val fileToDelete = File(photoUris[position].path ?: "")
            if (fileToDelete.exists()) {
                fileToDelete.delete()
            }

            // Удаляем из списка и SharedPreferences
            photoUris.removeAt(position)
            savePhotoUris(folderName, photoUris)
            photoAdapter.notifyItemRemoved(position)
        }

        photoRecyclerView.layoutManager = GridLayoutManager(this, 3)
        photoRecyclerView.adapter = photoAdapter

        addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val file = File(getExternalFilesDir(null), fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun savePhotoUris(folder: String, list: List<Uri>) {
        val prefs = getSharedPreferences("photos_pref", MODE_PRIVATE)
        val jsonString = prefs.getString("photos_map", null)
        val json = if (jsonString != null) JSONObject(jsonString) else JSONObject()

        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it.toString()) }

        json.put(folder, jsonArray)
        prefs.edit().putString("photos_map", json.toString()).apply()
    }

    private fun loadPhotoUris(folder: String): List<Uri> {
        val prefs = getSharedPreferences("photos_pref", MODE_PRIVATE)
        val jsonString = prefs.getString("photos_map", null) ?: return emptyList()
        val json = JSONObject(jsonString)
        if (!json.has(folder)) return emptyList()

        val jsonArray = json.getJSONArray(folder)
        return List(jsonArray.length()) { i -> Uri.parse(jsonArray.getString(i)) }
    }
}
