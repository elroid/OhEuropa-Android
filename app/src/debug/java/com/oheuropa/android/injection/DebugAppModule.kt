package com.oheuropa.android.injection

import android.content.Context
import com.oheuropa.android.DebugApp
import dagger.Module
import dagger.Provides

/**
 *
 * Class: com.oheuropa.android.injection.DebugAppModule
 * Project: OhEuropa-Android
 * Created Date: 09/03/2018 09:58
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
class DebugAppModule {

	@Provides
	internal fun provideContext(application: DebugApp): Context {
		return application.applicationContext
	}
}