package com.oheuropa.android.ui.base

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.ajalt.timberkt.e
import com.oheuropa.android.R
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

	override fun getCtx(): Context {
		return this
	}

	override fun toast(msg: CharSequence, length: Int) {
		Toast.makeText(getCtx(), msg, length).show()
	}

	override fun showError(msgId: Int, msg: String?, fatal: Boolean) {
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
			builder.setPositiveButton(R.string.err_quit) { _, _ -> quit() }
		else
			builder.setPositiveButton(R.string.err_ok) { dialog, _ -> dialog.dismiss() }
		builder.create().show()
	}

	override fun quit() {
		System.exit(0)
	}

}