package com.oheuropa.android.injection

import com.oheuropa.android.App
import dagger.Module
import dagger.Provides

/**
 *
 * Class: com.oheuropa.android.injection.AppTestModule
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 18:19
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
class AppTestModule(protected val app: App) {
	@Provides
	fun provideApplication(): App {
		return app
	}
}