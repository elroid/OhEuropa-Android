package com.oheuropa.android.ui.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import androidx.core.view.doOnPreDraw
import com.github.ajalt.timberkt.i
import com.github.ajalt.timberkt.v
import com.oheuropa.android.R
import com.oheuropa.android.ui.compass.CompassActivity
import com.oheuropa.android.ui.info.InfoActivity
import com.oheuropa.android.ui.map.MapActivity
import com.oheuropa.android.util.ViewUtils
import kotlinx.android.synthetic.main.bottom_navigation.*

/**
 *
 * Class: com.oheuropa.android.ui.base.BaseActivity
 * Project: OhEuropa
 * Created Date: 19/02/2018 19:04
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
abstract class BottomNavActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		i { "onCreate $this" }
		super.onCreate(savedInstanceState)
		setContentView(getLayoutId())

		//set up bottom nav
		bottomNavigationView.onNavigationItemSelectedListener = this
		bottomNavigationView.setTextVisibility(false)
		val tabBarBgDrawable = bottomNavigationView.background
		val iw = tabBarBgDrawable.intrinsicWidth
		val ih = tabBarBgDrawable.intrinsicHeight
		val sw = ViewUtils.getScreenWidth()
		val th = ViewUtils.getHeightAtWidth(iw, ih, sw)
		bottomNavigationView.doOnPreDraw {
			bottomNavigationView.itemHeight = th
			bottomNavigationView.setIconsMarginTop(ViewUtils.dpToPx(12))
		}
	}

	@LayoutRes
	protected abstract fun getLayoutId(): Int

	internal abstract fun getNavigationMenuItemId(): Int

	open fun onThisTabPressed() {
		v { "this tab pressed(%s) - doing nothing by default" }
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		i { "onNavigationItemSelected(${item.title})" }
		if (item.itemId == getNavigationMenuItemId())
			onThisTabPressed()
		else
			bottomNavigationView.postDelayed({
				i { "creating intent..." }
				when (item.itemId) {
					R.id.navigation_compass -> startActivity(CompassActivity.createIntent(getCtx()))
					R.id.navigation_map -> startActivity(MapActivity.createIntent(getCtx()))
					R.id.navigation_info -> startActivity(InfoActivity.createIntent(getCtx()))
				}
				finish()
			}, 300)
		return true
	}

	private fun updateNavigationBarState() {
		selectBottomNavigationBarItem(getNavigationMenuItemId())
	}

	private fun selectBottomNavigationBarItem(itemId: Int) {
		val menu = bottomNavigationView.menu
		val lim = menu.size() - 1
		(0..lim)
			.map { menu.getItem(it) }
			.forEach { if (it.itemId == itemId) it.isChecked = true }
	}

	override fun onStart() {
		super.onStart()
		updateNavigationBarState()
	}

	// Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
	override fun onPause() {
		super.onPause()
		overridePendingTransition(0, 0)
	}

}