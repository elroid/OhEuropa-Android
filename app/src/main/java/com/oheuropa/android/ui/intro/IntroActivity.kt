package com.oheuropa.android.ui.intro

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.oheuropa.android.R
import com.oheuropa.android.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_intro.*
import com.oheuropa.android.ui.compass.CompassActivity

class IntroActivity: BaseActivity()
{
    companion object {
        fun createIntent(ctx: Context): Intent {
            return Intent(ctx, IntroActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        setupView()
    }

    private fun setupView() {
        setupVideoView()

        skipButton.setOnClickListener {
            nextActivity()
        }
    }

    private fun nextActivity() {
        startActivity(CompassActivity.createIntent(getCtx()))
        finish()
    }

    private fun setupVideoView(){
        val path = "android.resource://" + packageName + "/" + R.raw.intro
        val videoView = findViewById<TextureVideoView>(R.id.videoView)

        videoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP)
        videoView.setDataSource(getCtx(), Uri.parse(path))

        val listener = object : TextureVideoView.MediaPlayerListener {
            override fun onVideoPrepared() {
                videoView.play()
            }

            override fun onVideoEnd() {
                nextActivity()
            }
        }

        videoView.setListener(listener)
    }
}