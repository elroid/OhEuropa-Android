package com.oheuropa.android.ui.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.oheuropa.android.R
import com.oheuropa.android.ui.compass.CompassActivity
import com.oheuropa.android.ui.info.InfoActivity
import com.oheuropa.android.ui.map.MapActivity
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
		super.onCreate(savedInstanceState)
		setContentView(getLayoutId())
		bottomNavigationView.setOnNavigationItemSelectedListener(this)
	}

	@LayoutRes
	protected abstract fun getLayoutId(): Int

	internal abstract fun getNavigationMenuItemId(): Int

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		bottomNavigationView.postDelayed({
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