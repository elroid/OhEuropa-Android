package com.oheuropa.android.framework

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import com.oheuropa.android.BuildConfig
import com.oheuropa.android.model.Beacon
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import timber.log.Timber
import java.io.File

/**
 * Base class for Robolectric data layer tests.
 * Inherit from this class to create a test.
 *
 * Class: com.oheuropa.android.framework.RoboelectricTest
 * Project: OhEuropa-Android
 * Created Date: 08/03/2018 09:11
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
	application = RoboelectricTest.ApplicationStub::class,
	sdk = intArrayOf(21))
abstract class RoboelectricTest {

	@Before
	@Throws(Exception::class)
	fun setUp() {
		ShadowLog.stream = System.out
		Timber.plant(SystemOutTree(Log.VERBOSE))
	}

	fun getCtx(): Context {
		return RuntimeEnvironment.application
	}

	fun cacheDir(): File {
		return getCtx().cacheDir
	}

	internal class ApplicationStub : Application()

	protected fun createBeacon(name: String, id: Int, loc: Location): Beacon {
		return Beacon(
			id = id, name = name, placeid = "",
			lat = loc.latitude.toFloat(),
			lng = loc.longitude.toFloat(),
			datecreated = "2018-03-08 16:29:00",
			centerradius = 40,
			innerradius = 60,
			outerradius = 100,
			radioplays = 0,
			nearbys = 0
		)
	}
}