package com.example.playmusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private var listSong: MutableList<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    var onClick: ClickItem? = null

    fun setOnClickItemMusic(onClick: ClickItem) {
        this.onClick = onClick
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNameSong: TextView = itemView.findViewById(R.id.tvNameSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_in_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = listSong[position]
        holder.textNameSong.text = song.nameSong

        holder.itemView.setOnClickListener { onClick?.onClickItem(song) }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }
}
