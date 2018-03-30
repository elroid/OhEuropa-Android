package com.oheuropa.android.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.v
import com.oheuropa.android.R
import com.oheuropa.android.data.event.AppQuitEvent
import com.oheuropa.android.data.local.AnalyticsHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.security.InvalidParameterException


/**
 *
 * Class: com.oheuropa.android.ui.base.BaseActivity
 * Project: OhEuropa
 * Created Date: 19/02/2018 19:04
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		//ensure app is shown fullscreen
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

		EventBus.getDefault().register(this)
	}

	override fun onDestroy() {
		EventBus.getDefault().unregister(this)
		super.onDestroy()
	}

	override fun getCtx(): Context {
		return this
	}

	override fun toast(msg: CharSequence, length: Int) {
		Toast.makeText(getCtx(), msg, length).show()
	}

	override fun showError(msgId: Int, msg: String?, fatal: Boolean) {
		v { "showError($msgId, $msg, $fatal)" }
		try {
			val builder = AlertDialog.Builder(this)
			builder.setTitle(R.string.err_title)
			when {
				msgId != 0 -> builder.setMessage(msgId)
				msg != null -> builder.setMessage(msg)
				else -> {
					builder.setMessage(R.string.err_unspecified)
					try {
						throw InvalidParameterException("No msg specified")
					} catch (ex: Exception) {
						e { "Error: ${ex.message}" }
					}
				}
			}

			if (fatal)
				builder.setPositiveButton(R.string.quit) { _, _ -> quit() }
			else
				builder.setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
			builder.create().show()
		} catch (ex: Throwable) {
			AnalyticsHelper.logException(ex, "Error showing an error!")
		}
	}

	override fun quit() {
		EventBus.getDefault().post(AppQuitEvent())
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onQuit(e: AppQuitEvent) {
		v { "received $e on ${name()}" }
		finish()
	}

	fun name(): String {
		return javaClass.simpleName
	}

}