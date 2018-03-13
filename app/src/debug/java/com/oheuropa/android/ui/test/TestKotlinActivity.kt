package com.oheuropa.android.ui.test

import android.os.Bundle
import android.widget.Button
import com.oheuropa.android.R
import com.oheuropa.android.domain.AudioComponent
import com.oheuropa.android.ui.base.BaseActivity
import dagger.android.AndroidInjection
import timber.log.Timber
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

	@Inject lateinit var audioPlayer: AudioComponent

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.kotlin_test)

		audioPlayer.setStreamUrl("https://streams.radio.co/s02776f249/listen")

		findViewById<Button>(R.id.but1).setOnClickListener({
			Timber.i("Playing static")
			audioPlayer.setState(AudioComponent.State.STATIC)
		})
		findViewById<Button>(R.id.but2).setOnClickListener({
			Timber.i("Playing static + stream")
			audioPlayer.setState(AudioComponent.State.STATIC_MIX)
		})

		findViewById<Button>(R.id.but3).setOnClickListener({
			Timber.i("Playing stream")
			audioPlayer.setState(AudioComponent.State.SIGNAL)
		})

		findViewById<Button>(R.id.but4).setOnClickListener({
			Timber.i("Playing silence")
			audioPlayer.setState(AudioComponent.State.QUIET)
		})
	}

	override fun onResume() {
		super.onResume()
		audioPlayer.activate()
		Timber.d("done with activate()")
	}

	override fun onPause() {
		audioPlayer.deactivate()
		super.onPause()
	}
}
