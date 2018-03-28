package com.oheuropa.android.conn

import android.support.test.runner.AndroidJUnit4
import com.oheuropa.android.data.remote.OhEuropaApiService
import com.oheuropa.android.injection.AppModule
import com.oheuropa.android.injection.AppTestComponent
import com.oheuropa.android.injection.DaggerAppTestComponent
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 *
 * Class: com.oheuropa.android.conn.ApiConnectionTest
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 18:12
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@RunWith(AndroidJUnit4::class)
class ApiConnectionTest {

	private lateinit var testAppComponent: AppTestComponent

	@Inject lateinit var apiService: OhEuropaApiService

	@Before
	fun setup() {
		testAppComponent = DaggerAppTestComponent.builder()
			.appModule(AppModule())
			.build()
		testAppComponent.inject(this)
	}

	@Test
	fun getBeacons_returnsAtLeastOneValidBeacon() {
		val testObserver = apiService.getBeacons().test()
		testObserver.assertNoErrors()
		assertEquals(testObserver.valueCount(), 1)
		val gpr = testObserver.values()[0]
		assertTrue(gpr.data.isNotEmpty())
		val firstBeacon = gpr.data[0]
		assertNotNull(firstBeacon.name)
	}
}