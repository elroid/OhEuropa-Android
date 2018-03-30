package com.oheuropa.android.injection

import android.content.Context
import com.evernote.android.job.JobManager
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.v
import com.oheuropa.android.data.AudioPlayer
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.data.job.BackgroundJobCreator
import com.oheuropa.android.data.local.CompassProvider
import com.oheuropa.android.data.local.LocationProvider
import com.oheuropa.android.data.local.PrefsHelper
import com.oheuropa.android.data.remote.OhEuropaApiService
import com.oheuropa.android.domain.*
import com.oheuropa.android.model.MyObjectBox
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
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
@Suppress("unused")
@Module
class AppModule {

	@Singleton
	@Provides
	internal fun provideApi(ctx: Context): OhEuropaApiService {

		val okBuilder = OkHttpClient.Builder()

		//add 5mb cache
		val cacheSize = 5 * 1024 * 1024L // 5 MB
		val cache = Cache(ctx.cacheDir, cacheSize)
		okBuilder.cache(cache)

		//20s timeout
		okBuilder.connectTimeout(20, TimeUnit.SECONDS)
		okBuilder.writeTimeout(20, TimeUnit.SECONDS)
		okBuilder.readTimeout(20, TimeUnit.SECONDS)

		//add logging for debug builds
		@Suppress("ConstantConditionIf")
		if (Constants.isDebug(LOG_HTTP)) {
			val verboseLog = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
				v { "OkHttp: $message" }
			})
			verboseLog.level = HttpLoggingInterceptor.Level.BODY//Headers//
			okBuilder.addInterceptor(verboseLog)

			val infoLog = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
				i { "OkHttp: $message" }
			})
			infoLog.level = HttpLoggingInterceptor.Level.BASIC
			okBuilder.addInterceptor(infoLog)
		}

		val retrofit = Retrofit.Builder()
			.baseUrl("http://oheuropa.com/api/")
			.client(okBuilder.build())
			.addConverterFactory(MoshiConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()

		return retrofit.create(OhEuropaApiService::class.java)
	}

	@Singleton
	@Provides
	internal fun provideBoxStore(ctx: Context): BoxStore {
		return MyObjectBox.builder().androidContext(ctx).build()
	}

	@Provides
	internal fun provideAudioComponent(ctx: Context): AudioComponent {
		return AudioPlayer(ctx)
	}

	@Singleton
	@Provides
	internal fun provideLocationComponent(ctx: Context): LocationComponent {
		return LocationProvider(ctx)
	}

	@Singleton
	@Provides
	internal fun provideCompassComponent(ctx: Context): CompassComponent {
		return CompassProvider(ctx)
	}

	@Singleton
	@Provides
	internal fun provideBeaconWatcher(dataManager: DataManager, locator: LocationComponent): BeaconWatcher {
		return BeaconWatcher(dataManager, locator)
	}

	@Singleton
	@Provides
	fun provideJobManager(ctx: Context, jobCreator: BackgroundJobCreator): JobManager {
		JobManager.create(ctx).addJobCreator(jobCreator)
		return JobManager.instance()
	}

	@Singleton
	@Provides
	internal fun providePrefsHelper(ctx: Context): PrefsHelper {
		return PrefsHelper(ctx)
	}
}