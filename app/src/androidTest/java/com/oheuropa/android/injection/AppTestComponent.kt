package com.oheuropa.android.injection

import com.oheuropa.android.conn.ApiConnectionTest
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 *
 * Class: com.oheuropa.android.injection.ApplicationTestComponent
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 18:18
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, MainBuildersModule::class])
interface AppTestComponent:MainComponent {
	fun inject(test: ApiConnectionTest)
}