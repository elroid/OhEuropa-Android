package com.oheuropa.android.data.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.w
import com.oheuropa.android.data.DataManager
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

			//every 24 hours, give or take 2 hours
			val updateIntervalMS = 2_073_600_000L//24 hours
			val updateFlexMS = 518_400_000L//6 hours

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

	override fun onRunJob(params: Params): Result {
		return try {
			w { "RUNNING RefreshBeaconsJob: $params *********************************************" }
			dataManager.updateBeaconList()
				.subscribe({
					w { "Finished beacon update job" }
				})

			Job.Result.SUCCESS
		} catch (ex: Exception) {
			e(ex) { "Error running background RefreshBeaconsJob" }
			Job.Result.FAILURE
		}
	}
}