package com.oheuropa.android.ui.test

import android.os.Bundle
import android.widget.Button
import com.oheuropa.android.R
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.ui.base.BaseActivity
import dagger.android.AndroidInjection
import io.objectbox.BoxStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

	@Inject lateinit var dataManager: DataManager
	@Inject lateinit var boxStore: BoxStore

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.kotlin_test)
		findViewById<Button>(R.id.but1).setOnClickListener({
			Timber.i("Updating beacon list...")
			dataManager.updateBeaconList()
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe({
					Timber.i("Update complete")
				}, {
					Timber.e(it, "updateBeaconList error")
				})
		})
		findViewById<Button>(R.id.but2).setOnClickListener({
			dataManager.followBeaconList()
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribe({
					Timber.i("Got updated list: %s", it)
				}, {
					Timber.e(it, "followBeacon error")
				})
		})

		findViewById<Button>(R.id.but3).setOnClickListener({
			Timber.i("Adding new beacon...")
			val beaconBox = boxStore.boxFor(Beacon::class.java)
			beaconBox.put(Beacon(
				id = 666,
				name = "New beak",
				placeid = "",
				lat = 51.5f,
				lng = -2.6f,
				datecreated = "2018-03-08 19:56:00",
				centerradius = 40,
				innerradius = 60,
				outerradius = 100,
				radioplays = 0,
				nearbys = 0
			))
		})

		findViewById<Button>(R.id.but4).setOnClickListener({
			Timber.i("Clearing all beacons...")
			val beaconBox = boxStore.boxFor(Beacon::class.java)
			beaconBox.removeAll()
		})
	}


}
