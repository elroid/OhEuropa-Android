package com.oheuropa.android.model

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.oheuropa.android.framework.RoboelectricTest
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * Class: com.oheuropa.android.data.DataManagerTest
 * Project: OhEuropa-Android
 * Created Date: 09/03/2018 08:24
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class AudioStatusResponseTest : RoboelectricTest() {

	private val james = AudioStatusResponse(current_track = AudioStatusResponse.TrackInfo("James Stenhouse - singing \"City Lights\""))
	private val luke = AudioStatusResponse(current_track = AudioStatusResponse.TrackInfo("Luke Emery - singing Lovers and Friends"))
	private val lukas = AudioStatusResponse(current_track = AudioStatusResponse.TrackInfo("Lukus Robbins - singing Landslide "))


	@Test
	fun testTitle(){
		assertEquals("City Lights", james.current_track.getSongTitle())
		assertEquals("Lovers and Friends", luke.current_track.getSongTitle())
		assertEquals("Landslide", lukas.current_track.getSongTitle())
	}

	@Test
	fun testPerformer(){
		assertEquals("James Stenhouse", james.current_track.getPerformerName())
		assertEquals("Luke Emery", luke.current_track.getPerformerName())
		assertEquals("Lukus Robbins", lukas.current_track.getPerformerName())
	}
}