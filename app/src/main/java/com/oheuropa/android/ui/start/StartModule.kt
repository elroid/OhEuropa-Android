package com.oheuropa.android.ui.start

import com.oheuropa.android.data.DataManager
import dagger.Module
import dagger.Provides

/**
 * Created Date: 26/03/2018 19:11
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
@Module
class StartModule {

	@Provides
	fun provideStartPresenter(view: StartContract.View,
							  dataManager: DataManager): StartContract.Presenter {
		return StartPresenter(view, dataManager)
	}
}