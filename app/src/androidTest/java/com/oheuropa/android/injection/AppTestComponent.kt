package com.oheuropa.android.injection

import com.oheuropa.android.conn.ApiConnectionTest
import dagger.Component
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
@Component(modules = arrayOf(AppModule::class, AppTestModule::class))
interface AppTestComponent : AppComponent {

	fun inject(test: ApiConnectionTest)
}