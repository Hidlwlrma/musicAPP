package com.example.helloWorld

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {

    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        val menu = intent.getSerializableExtra("item_data") as MenuData
        subtitle.text = menu.artist
        musicTitle.text = menu.title
        duration.text = getMMSSText(menu.duration.toLong())
        Glide.with(this).load(menu.image).into(albumArt)
        mediaPlayer = MediaPlayer.create(this, Uri.parse(menu.source))
        playMusic(mediaPlayer)

        var beforePosition : Long = 0
        val duration : Long = menu.duration.toLong() * 1000 - beforePosition
        var currentPosition : Long = 0

        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }
        }

        val countDownTimer = object : CountDownTimer(duration,1000) {

            override fun onTick(millisUntilFinished: Long) {
                currentPosition = (duration - millisUntilFinished) / 1000
                position.text = getMMSSText(currentPosition + beforePosition)
            }

            override fun onFinish() {
                position.text = getMMSSText(menu.duration.toLong())
                media_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                beforePosition = 0
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
                beforePosition += currentPosition
            }
        }



    }

    fun getMMSSText(time : Long) : String {
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