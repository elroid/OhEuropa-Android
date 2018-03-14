package com.oheuropa.android.data

import com.oheuropa.android.data.remote.OhEuropaApiService
import com.oheuropa.android.model.Beacon
import io.objectbox.BoxStore
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import timber.log.Timber.v
import java.util.*
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

	fun getTestBeaconList(): Observable<List<Beacon>> {
		return Observable.create({ it: ObservableEmitter<List<Beacon>> ->
			val beacons2 = ArrayList<Beacon>(2)
			beacons2.add(Beacon(name = "ChocFactory", id = 10, lat = 51.468002f, lng = -2.552165f))
			beacons2.add(Beacon(name = "StreetEnd", id = 10, lat = 51.469125f, lng = -2.550244f))
			it.onNext(beacons2)
		})
	}

	private fun updateBeaconList(): Completable {
		return Completable.create { emitter ->
			apiService.getBeacons()
				.map { it.data }//get beacons from parent object
				.subscribe({
					val beaconBox = boxStore.boxFor(Beacon::class.java)
					v("Adding ${it.size} beacons to object box of size(${beaconBox.count()}): $it")
					beaconBox.put(it)
					v("...done adding beacons, new size:${beaconBox.count()}")
					emitter.onComplete()
				}, {
					emitter.onError(it)
				})
		}
	}

	fun ensureBeaconListPresent(): Completable {
		return Completable.create { emitter ->
			val beaconBox = boxStore.boxFor(Beacon::class.java)
			val count = beaconBox.count()
			if (count > 0) {
				v("no update needed, we already have $count beacons")
				emitter.onComplete()
			} else {
				updateBeaconList()
					.subscribe({ emitter.onComplete() }, { emitter.onError(it) })
			}
		}
	}
}