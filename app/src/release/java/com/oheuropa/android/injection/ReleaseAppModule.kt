package com.oheuropa.android.injection

import android.content.Context
import com.oheuropa.android.App
import dagger.Module
import dagger.Provides

/**
 *
 * Class: com.oheuropa.android.injection.ReleaseAppModule
 * Project: OhEuropa-Android
 * Created Date: 09/03/2018 10:02
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
class ReleaseAppModule {
	@Provides
	internal fun provideContext(application: App): Context {
		return application.applicationContext
	}
}