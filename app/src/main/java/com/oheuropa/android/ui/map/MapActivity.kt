package com.oheuropa.android.ui.map

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.oheuropa.android.R
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.base.BottomNavActivity
import com.oheuropa.android.util.ViewUtils.Companion.dpToPx
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 *
 * Class: com.oheuropa.android.ui.compass.CompassActivity
 * Project: OhEuropa
 * Created Date: 21/02/2018 17:56
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class MapActivity : BottomNavActivity(), OnMapReadyCallback, MapContract.View {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, MapActivity::class.java)
		}
	}

	@Inject lateinit var presenter: MapContract.Presenter
	private lateinit var map: GoogleMap

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)

		//keep screen on while viewing the map
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

		//ensure map goes under status bar
		window.decorView.systemUiVisibility =
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

		val mapFragment = supportFragmentManager
			.findFragmentById(R.id.mapFragment) as SupportMapFragment
		mapFragment.getMapAsync(this)
	}

	override fun onStop() {
		presenter.stop()
		super.onStop()
	}

	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		map.mapType = MAP_TYPE_TERRAIN
		presenter.startBeaconListener()
	}

	override fun showBeacons(beacons: List<Beacon>) {
		beacons.iterator().forEach {
			map.addMarker(MarkerOptions().position(it.getCoordinate().toLatLng()))
		}
	}

	override fun showMyLocation(loc: Coordinate) {
		val opts = MarkerOptions()
		opts.position(loc.toLatLng())
		val drawable = ContextCompat.getDrawable(getCtx(), R.drawable.me_marker)
		val icon = createBitmap(drawable, dpToPx(18), dpToPx(18))
		opts.icon(BitmapDescriptorFactory.fromBitmap(icon))
		map.addMarker(opts)
	}

	private fun createBitmap(drawable: Drawable?, width: Int, height: Int): Bitmap {
		if (drawable is BitmapDrawable)
			return drawable.bitmap
		val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		drawable?.setBounds(0, 0, canvas.width, canvas.height)
		drawable?.draw(canvas)
		return bitmap
	}

	override fun zoomTo(beacons: List<Beacon>, myLocation: Coordinate) {
		val b = LatLngBounds.builder()
		beacons.iterator().forEach {
			b.include(it.getCoordinate().toLatLng())
		}
		b.include(myLocation.toLatLng())
		val bounds = b.build()
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(32)))
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_map
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_map
	}
}