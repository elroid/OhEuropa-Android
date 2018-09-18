package com.oheuropa.android.data.job

import android.annotation.SuppressLint
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.w
import com.oheuropa.android.data.DataManager
import com.oheuropa.android.data.local.AnalyticsHelper
import javax.inject.Inject

/**
 * Created by elroid on 23/03/2018.
 */
class RefreshBeaconsJob
@Inject constructor(private val dataManager: DataManager) : Job() {

	companion object {
		const val TAG = "refresh_beacons_job_tag"

		@JvmStatic
		fun schedule() {

			d { "scheduling job..." }
			//every 24 hours, give or take 2 hours
			val updateIntervalMS = 86_400_000L//24 hours
			val updateFlexMS = 21_600_000L//6 hours

			JobRequest.Builder(RefreshBeaconsJob.TAG)
				.setPeriodic(updateIntervalMS, updateFlexMS)
				.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
				.setRequirementsEnforced(true)
				.setUpdateCurrent(true)
				.build()
				.schedule()

			//test oneshot (3-5 minutes)
			/*JobRequest.Builder(RefreshBeaconsJob.TAG)
				.setExecutionWindow(180_000, 300_000)
				.setUpdateCurrent(true)
				.build()
				.schedule()*/
		}
	}

	@SuppressLint("CheckResult")
	override fun onRunJob(params: Params): Result {
		return try {
			i { "RUNNING RefreshBeaconsJob: $params *********************************************" }
			dataManager.updateBeaconList()
				.subscribe({
					i { "Finished beacon update job" }
					AnalyticsHelper.logBeaconUpdateComplete()
				},{
					AnalyticsHelper.logException(it, "Error running RefreshBeaconsJob")
				})

			Job.Result.SUCCESS
		} catch (ex: Exception) {
			AnalyticsHelper.logException(ex, "Error running background RefreshBeaconsJob")
			Job.Result.FAILURE
		}
	}
}