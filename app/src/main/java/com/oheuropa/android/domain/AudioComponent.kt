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

	data class State(
		val radioStreamUrl:String, val staticPlaying:Boolean, val radioPlaying:Boolean
	)
	fun setState(state:AudioComponent.State)

}

