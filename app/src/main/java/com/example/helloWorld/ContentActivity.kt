package com.example.helloWorld

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_content.*
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class ContentActivity : AppCompatActivity() {

    private lateinit var mediaPlayer : MediaPlayer

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        val menu = intent.getSerializableExtra("item_data") as MenuData
        subtitle.text = menu.artist
        musicTitle.text = menu.title
        duration.text = convertToMMSS(menu.duration)
        seekBar.max = menu.duration.toInt()

        Glide.with(this).load(menu.image).into(albumArt)

        mediaPlayer = MediaPlayer.create(this, Uri.parse(menu.source))
        playMusic(mediaPlayer)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress*1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("startPosition", "${seekBar?.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("stopPosition", "${seekBar?.progress}")
            }
        })


        val countDownTimer = object : CountDownTimer(mediaPlayer.duration.toLong(),1000) {

            override fun onTick(millisUntilFinished: Long) {
                position.text = convertToMMSS(mediaPlayer.currentPosition.toLong()/1000)
                seekBar.setProgress(mediaPlayer.currentPosition/1000, false)
                Log.d("currentPosition", "${mediaPlayer.currentPosition}")
            }

            override fun onFinish() {
                Log.d("finish","${mediaPlayer.currentPosition/1000}")
                media_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            }
        }
        countDownTimer.start()


        media_button.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                playMusic(mediaPlayer)
                countDownTimer.start()
            } else {
                pauseMusic(mediaPlayer)
                countDownTimer.cancel()
            }
        }
    }



    private fun convertToMMSS(time : Long) : String {
        val minute = time / 60
        val second = time % 60
        val text : String
        if (second < 10) {
            text = "${minute}:0${second}"
        } else {
            text = "${minute}:${second}"
        }
        return text
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun playMusic(mediaPlayer : MediaPlayer) {
        media_button.setImageResource(R.drawable.ic_pause_black_24dp)
        mediaPlayer.start()
    }

    private fun pauseMusic(mediaPlayer: MediaPlayer) {
        media_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        mediaPlayer.pause()
    }

    companion object {
        fun actionStart(context: Context, menuData: MenuData) {
            val intent = Intent(context, ContentActivity::class.java).apply {
                putExtra("item_data", menuData)
            }
            context.startActivity(intent)
        }
    }
}