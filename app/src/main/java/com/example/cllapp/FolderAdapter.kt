package com.example.cllapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(
    private val folders: MutableList<String>,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String, Int) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folderName = folders[position]
        holder.folderNameTextView.text = folderName

        holder.itemView.setOnClickListener {
            onItemClick(folderName)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(folderName, position)
        }
    }

    override fun getItemCount(): Int = folders.size

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderNameTextView: TextView = itemView.findViewById(R.id.folderNameTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteFolderButton)
    }
}
