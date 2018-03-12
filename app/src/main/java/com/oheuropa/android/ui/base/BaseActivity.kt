package com.oheuropa.android.ui.base

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.oheuropa.android.R
import timber.log.Timber
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
abstract class BaseActivity : AppCompatActivity() {

	protected fun getCtx(): Context {
		return this
	}

	protected fun toast(msg: CharSequence, length: Int = Toast.LENGTH_SHORT) {
		Toast.makeText(getCtx(), msg, length).show()
	}

	protected fun showError(msgId: Int = 0, msg: String?, fatal: Boolean = false) {
		val builder = AlertDialog.Builder(this)
		builder.setTitle(R.string.err_title)
		when {
			msgId != 0 -> builder.setMessage(msgId)
			msg != null -> builder.setMessage(msg)
			else -> {
				builder.setMessage(R.string.err_unspecified)
				try {
					throw InvalidParameterException("No msg specified")
				} catch (e: Exception) {
					Timber.e(e)
				}
			}
		}

		if (fatal)
			builder.setPositiveButton(R.string.err_quit) { _, _ -> quit() }
		else
			builder.setPositiveButton(R.string.err_ok) { dialog, _ -> dialog.dismiss() }
		builder.create().show()
	}

	protected fun quit() {
		System.exit(0)
	}
}