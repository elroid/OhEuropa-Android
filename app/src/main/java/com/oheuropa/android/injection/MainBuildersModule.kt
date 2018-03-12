package com.oheuropa.android.injection

import com.oheuropa.android.ui.start.StartActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 *
 * Class: com.oheuropa.android.injection.BuildersModule
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 13:19
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
abstract class MainBuildersModule {

	/*@ContributesAndroidInjector(modules = arrayOf(LobbyViewModule::class, LobbyModule::class))
	internal abstract fun bindLobbyActivity(): LobbyActivity*/

	// Add bindings for other sub-components here
	@ContributesAndroidInjector
	abstract fun bindStartActivity(): StartActivity
}