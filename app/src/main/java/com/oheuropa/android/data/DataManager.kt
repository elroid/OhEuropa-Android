package com.oheuropa.android.data

import com.fernandocejas.frodo.annotation.RxLogObservable
import com.github.ajalt.timberkt.*
import com.oheuropa.android.data.job.RefreshBeaconsJob
import com.oheuropa.android.data.local.PrefsHelper
import com.oheuropa.android.data.remote.OhEuropaApiService
import com.oheuropa.android.domain.Constants
import com.oheuropa.android.domain.USE_MOCK_BEACON_LOCATIONS
import com.oheuropa.android.domain.USE_MOCK_INTERACTION_EVENTS
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.BeaconLocation
import com.oheuropa.android.model.UserRequest
import com.oheuropa.android.ui.base.SchedulersFacade
import io.objectbox.BoxStore
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
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
	private val boxStore: BoxStore,
	private val prefs: PrefsHelper
) {

	fun followBeaconList(): Observable<List<Beacon>> {
		return when (Constants.isDebug(USE_MOCK_BEACON_LOCATIONS)) {
			false -> {
				val beaconBox = boxStore.boxFor(Beacon::class.java)
				val query = beaconBox.query().build()
				RxQuery.observable(query)
			}
			true -> {
				Observable.create({ it: ObservableEmitter<List<Beacon>> ->
					val beacons2 = ArrayList<Beacon>(2)
					beacons2.add(Beacon(name = "ChocFactory", placeid = "ChocFact", id = 10, lat = 51.468260f, lng = -2.554214f))
					beacons2.add(Beacon(name = "StreetEnd", placeid = "StreetEnd", id = 11, lat = 51.469125f, lng = -2.550244f))
					it.onNext(beacons2)
				})
			}
		}
	}

	@RxLogObservable
	fun updateBeaconList(): Completable {
		return Completable.create { emitter ->
			apiService.getBeacons()
				.map { it.data }//get beacons from parent object
				.subscribe({
					val beaconBox = boxStore.boxFor(Beacon::class.java)
					d { "RefreshBeaconsJob-Adding ${it.size} beacons to object box of size(${beaconBox.count()}): $it" }
					beaconBox.removeAll()
					beaconBox.put(it)
					d { "RefreshBeaconsJob-...done adding beacons, new size:${beaconBox.count()}" }
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
				v { "no update needed, we already have $count beacons" }
				emitter.onComplete()
				RefreshBeaconsJob.schedule()
			} else {
				updateBeaconList()
					.subscribe({
						emitter.onComplete()
					}, {
						w(it) { "error updating beacon list..." }
						emitter.onError(it)
					})
			}
		}
	}

	fun ensureUserIdCreated(): Completable {
		return Completable.create({ emitter ->
			if (!prefs.hasUserId()) {
				v { "creating user id..." }
				val userId = UUID.randomUUID().toString()
				if (Constants.isDebug(USE_MOCK_INTERACTION_EVENTS)) {
					i { "(not)uploading UserId($userId)" }
					emitter.onComplete()
				} else {
					v { "upload UserId($userId)" }
					createUserIdInteraction(userId).subscribe({
						prefs.setUserId(userId)
						emitter.onComplete()
					}, { emitter.onError(it) })
				}
			} else
				v { "we already have a user id: ${prefs.getUserId()}" }
			emitter.onComplete()
		})
	}

	private fun createUserIdInteraction(userid: String): Completable {
		return apiService.uploadNewUserId(UserRequest(userid))
	}


	fun uploadUserInteraction(placeId: String,
							  circleState: BeaconLocation.CircleState,
							  action: UserRequest.Action) {

		//todo upload in background with a queue?
		if (Constants.isDebug(USE_MOCK_INTERACTION_EVENTS))
			i { "(not)uploadUserInteraction($placeId, $circleState, $action)" }
		else {
			d { "uploadUserInteraction($placeId, $circleState, $action)" }
			createUserInteraction(placeId, circleState, action)
				.subscribeOn(SchedulersFacade.io())
				.observeOn(SchedulersFacade.io())
				.subscribe({
					i { "user interaction uploaded" }
				}, {
					e(it) { "Error uploading user interaction" }
				})
		}
	}

	private fun createUserInteraction(placeId: String,
									  circleState: BeaconLocation.CircleState,
									  action: UserRequest.Action): Completable {
		val zoneId = UserRequest.map(circleState) ?: return Completable.error(
			IllegalArgumentException("Couldn't recognise circle state: $circleState"))
		val userId = prefs.getUserId() ?: return Completable.error(
			IllegalArgumentException("No user id found!"))
		return apiService.uploadUserInteraction(UserRequest(userId, placeId, zoneId, action))
	}
}