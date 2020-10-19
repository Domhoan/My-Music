package com.example.playmusic

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, ClickItem {

    private var mMusicService: MusicService? = null
    private var mServiceConnection: ServiceConnection? = null
    private var mBound = false
    private var isPlaying = false
    private val list = mutableListOf<Song>()
    private var idSong = 0
    private lateinit var mBroadcastReceiver: BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initButton()
        fakeListSong()
        bindToService()
        receiverNextSong()
    }

    private fun initButton() {
        imgPre.setOnClickListener(this)
        imgPlay.setOnClickListener(this)
        imgNext.setOnClickListener(this)
    }

    private fun fakeListSong() {
        list.add(Song("Bình yên nơi đâu", R.raw.binhyennoidau))
        list.add(Song("Đừng về trễ", R.raw.dungvetre))
        list.add(Song("Mưa trong lòng", R.raw.muatronglong))
        list.add(Song("Nơi này có anh", R.raw.noinaycoanh))
        list.add(Song("Thế giới ảo tình yêu thật", R.raw.thegioiao))

        setDetailSong(list[idSong], idSong)

        val adapter = SongAdapter(list)
        adapter.setOnClickItemMusic(this)
        rcvList.setHasFixedSize(true)
        rcvList.adapter = adapter

    }

    private fun setDetailSong(song: Song, indexSong: Int) {
        tvSongSelect.text = song.nameSong
    }

    private fun bindToService() {
        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val musicBinder: MusicService.MusicBinder = service as MusicService.MusicBinder
                mMusicService = musicBinder.getService()
                mMusicService?.setListSong(list)
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mBound = false
            }
        }

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, mServiceConnection as ServiceConnection, BIND_AUTO_CREATE)
    }


    private fun receiverNextSong() {
        val intentFiler = IntentFilter()
        intentFiler.addAction("NEXT_SONG")
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null || intent.action == null) return

                val index = intent.getIntExtra("INDEX_SONG", 0)
                nextSong(index)
            }

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFiler)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgPlay -> {
                isPlaying = !isPlaying
                playOrPauseSong(this.idSong)
            }
            R.id.imgNext -> {
                nextSong(this.idSong)
            }
            R.id.imgPre -> {
                previousSong()
            }
        }
    }

    override fun onClickItem(song: Song) {
        setDetailSong(song, list.indexOf(song))
        this.idSong = list.indexOf(song)
        if (!isPlaying) isPlaying = true
        stopSong()
        playOrPauseSong(idSong)
    }

    private fun playOrPauseSong(indexSong: Int) {
        if (!mBound) return
        if (isPlaying) {
            imgPlay.setImageResource(R.drawable.ic_pause)
        } else {
            imgPlay.setImageResource(R.drawable.ic_play)
        }
        mMusicService?.playOrPauseSong(indexSong)
    }

    private fun previousSong() {
        if (!mBound) return
        stopSong()
        if (idSong > 0) {
            idSong--
        } else {
            idSong = list.size - 1
        }
        setDetailSong(list[idSong], idSong)
        if (isPlaying) {
            mMusicService?.nextOrPre(idSong)
        }
    }

    private fun nextSong(index: Int) {
        if (!mBound) return
        this.idSong = index
        stopSong()
        if (idSong < list.size - 1) {
            idSong++
        } else {
            idSong = 0
        }
        setDetailSong(list[idSong], idSong)
        if (isPlaying) {
            mMusicService?.nextOrPre(idSong)
        }
    }


    private fun stopSong() {
        if (!mBound) return
        mMusicService?.stopSong()
    }


    override fun onDestroy() {
        unbindService(mServiceConnection!!)
        mBound = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver)
        super.onDestroy()
    }

}
