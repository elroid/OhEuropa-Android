package com.oheuropa.android.ui.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.oheuropa.android.R
import com.oheuropa.android.domain.AudioComponent
import com.oheuropa.android.domain.AudioComponent.State.*
import com.oheuropa.android.ui.base.BaseActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Class: com.oheuropa.android.ui.test.TestActivity
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 16:48
 *
 * @author [Elliot Long](mailto:e@elroid.com)
 * Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class TestKotlinActivity : BaseActivity() {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, TestKotlinActivity::class.java)
		}
	}
	@Inject lateinit var audioPlayer: AudioComponent

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.kotlin_test)

		findViewById<Button>(R.id.but1).setOnClickListener({
			i { "Playing static" }
			audioPlayer.setState(STATIC)
		})
		findViewById<Button>(R.id.but2).setOnClickListener({
			i { "Playing static + stream" }
			audioPlayer.setState(STATIC_MIX)
		})

		findViewById<Button>(R.id.but3).setOnClickListener({
			i { "Playing stream" }
			audioPlayer.setState(SIGNAL)
		})

		findViewById<Button>(R.id.but4).setOnClickListener({
			i { "Playing silence" }
			audioPlayer.setState(QUIET)
		})
	}

	override fun onResume() {
		super.onResume()
		audioPlayer.activate()
		d { "done with activate()" }
	}

	override fun onPause() {
		audioPlayer.deactivate()
		super.onPause()
	}
}
