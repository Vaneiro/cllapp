package com.example.cllapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhotoActivity : AppCompatActivity() {

    private lateinit var folderNameTextView: TextView
    private lateinit var addPhotoButton: Button
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    private val photoUris = mutableListOf<Uri>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoUris.add(it)
            photoAdapter.notifyItemInserted(photoUris.size - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        folderNameTextView = findViewById(R.id.folderNameTextView) // ✅ правильно
        var addPhotoButton = findViewById<Button>(R.id.addPhotoButton)
        var photoRecyclerView = findViewById<RecyclerView>(R.id.photoRecyclerView)
        var folderName = intent.getStringExtra("folder_name") ?: "Без имени"

        folderNameTextView = findViewById(R.id.folderNameTextView)
        addPhotoButton = findViewById(R.id.addPhotoButton)
        photoRecyclerView = findViewById(R.id.photoRecyclerView)

        folderNameTextView.text = "Папка: $folderName"

        photoAdapter = PhotoAdapter(photoUris)
        photoRecyclerView.layoutManager = GridLayoutManager(this, 3)
        photoRecyclerView.adapter = photoAdapter

        addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }
}
