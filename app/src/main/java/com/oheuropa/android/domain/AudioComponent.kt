package com.oheuropa.android.domain

/**
 * Used to control the audio output
 *
 * Class: com.oheuropa.android.domain.AudioComponent
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 16:07
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface AudioComponent {

	enum class State {
		QUIET, STATIC, STATIC_MIX, SIGNAL
	}
	fun setStreamUrl(radioStreamUrl: String)

	fun setState(state: AudioComponent.State)

	fun activate()

	fun deactivate()

}

