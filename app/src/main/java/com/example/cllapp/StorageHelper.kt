package com.example.cllapp


import android.content.Context
import android.net.Uri
import org.json.JSONArray

object StorageHelper {
    fun savePhotoUris(context: Context, folderName: String, uris: List<Uri>) {
        val prefs = context.getSharedPreferences("photos", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        uris.forEach { jsonArray.put(it.toString()) }
        prefs.edit().putString(folderName, jsonArray.toString()).apply()
    }

    fun loadPhotoUris(context: Context, folderName: String): MutableList<Uri> {
        val prefs = context.getSharedPreferences("photos", Context.MODE_PRIVATE)
        val json = prefs.getString(folderName, null) ?: return mutableListOf()
        val jsonArray = JSONArray(json)
        val result = mutableListOf<Uri>()
        for (i in 0 until jsonArray.length()) {
            result.add(Uri.parse(jsonArray.getString(i)))
        }
        return result
    }
    fun saveFolders(context: Context, folders: List<String>) {
        val prefs = context.getSharedPreferences("folders", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        folders.forEach { jsonArray.put(it) }
        prefs.edit().putString("folder_list", jsonArray.toString()).apply()
    }

    fun loadFolders(context: Context): MutableList<String> {
        val prefs = context.getSharedPreferences("folders", Context.MODE_PRIVATE)
        val json = prefs.getString("folder_list", null) ?: return mutableListOf()
        val jsonArray = JSONArray(json)
        val result = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            result.add(jsonArray.getString(i))
        }
        return result
    }

}
