package com.oheuropa.android.injection

import com.oheuropa.android.ui.test.TestKotlinActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Class: com.oheuropa.android.injection.DebuBuildersModule
 * Project: OhEuropa-Android
 * Created Date: 09/03/2018 09:27
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
abstract class DebugBuildersModule : MainBuildersModule(){
	@ContributesAndroidInjector
	abstract fun bindTestActivity(): TestKotlinActivity
}