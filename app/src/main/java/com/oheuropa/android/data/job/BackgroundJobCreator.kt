package com.oheuropa.android.data.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.oheuropa.android.data.DataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundJobCreator @Inject constructor(
	private val dataManager: DataManager
) : JobCreator {

	override fun create(tag: String): Job? {
		return RefreshBeaconsJob(dataManager)
	}
}
