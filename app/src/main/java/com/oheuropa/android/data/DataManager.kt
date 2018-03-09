package com.oheuropa.android.data

import com.oheuropa.android.data.remote.OhEuropaApiService
import com.oheuropa.android.model.Beacon
import io.objectbox.BoxStore
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject


/**
 *
 * Class: com.oheuropa.android.data.DataManager
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 17:49
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class DataManager @Inject constructor(
	private val apiService: OhEuropaApiService,
	private val boxStore: BoxStore
) {

	fun followBeaconList(): Observable<List<Beacon>> {
		val beaconBox = boxStore.boxFor(Beacon::class.java)
		val query = beaconBox.query().build()
		return RxQuery.observable(query)
	}

	fun updateBeaconList(): Completable {
		return Completable.create { emitter ->
			apiService.getBeacons()
				.map { it.data }//get beacons from parent object
				.subscribe({
					val beaconBox = boxStore.boxFor(Beacon::class.java)
					Timber.v("Adding %s beacons to object box of size(%s): %s", it.size, beaconBox.count(), it)
					beaconBox.put(it)
					Timber.v("...done adding beacons, new size:%s", beaconBox.count())
					emitter.onComplete()
				}, {
					emitter.onError(it)
				})
		}
	}
}