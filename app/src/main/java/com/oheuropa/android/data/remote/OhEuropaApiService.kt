package com.oheuropa.android.data.remote

import com.oheuropa.android.model.AudioStatusResponse
import com.oheuropa.android.model.GetPlacesResponse
import io.reactivex.Single
import retrofit2.http.GET

/**
 *
 * Class: com.oheuropa.android.data.remote.OhEuropaApiService
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 17:24
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
interface OhEuropaApiService {

	@GET("getdata.php?getplaces")
	fun getBeacons(): Single<GetPlacesResponse>

	@GET("https://public.radio.co/stations/s02776f249/status")
	fun getAudioStatus(): Single<AudioStatusResponse>
}