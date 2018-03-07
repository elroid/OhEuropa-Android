package com.oheuropa.android.injection

import android.content.Context
import com.oheuropa.android.App
import com.oheuropa.android.data.remote.OhEuropaApiService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 *
 * Class: com.oheuropa.android.injection.AppModule
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 13:18
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
class AppModule {

	@Provides
	internal fun provideContext(application: App): Context {
		return application.getApplicationContext()
	}

	@Singleton
	@Provides
	internal fun provideApi(): OhEuropaApiService {
		val retrofit = Retrofit.Builder()
			.baseUrl("http://oheuropa.com/api/")
			.addConverterFactory(MoshiConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()
		return retrofit.create(OhEuropaApiService::class.java)
	}
}