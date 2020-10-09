package com.oheuropa.android.ui.map

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.v
import com.github.ajalt.timberkt.w
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.oheuropa.android.R
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.base.LocationEnabledActivity
import com.oheuropa.android.util.ViewUtils.Companion.dpToPxF
import com.oheuropa.android.util.ViewUtils.Companion.getScreenWidth
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
class MapActivity:LocationEnabledActivity<MapContract.Presenter>(), OnMapReadyCallback, MapContract.View {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, MapActivity::class.java)
		}
	}

	@Inject override lateinit var presenter: MapContract.Presenter
	private lateinit var map: GoogleMap

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)

		//keep screen on while viewing the map
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

		val mapFragment = supportFragmentManager
			.findFragmentById(R.id.mapFragment) as SupportMapFragment
		mapFragment.getMapAsync(this)
	}

	override fun onPause() {
		super.onPause()
		presenter.saveMapState()
	}

	override fun onStop() {
		presenter.stop()
		super.onStop()
	}

	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		applyMapStyle(map)

		map.setOnCameraIdleListener {
			presenter.onCameraIdle(Coordinate(map.cameraPosition.target), map.cameraPosition.zoom)
		}

		//remove popup directions
		map.uiSettings.isMapToolbarEnabled = false

		map.setOnCameraMoveStartedListener {
			if(it == REASON_GESTURE)
				presenter.onMapWander()
		}
	}

	private fun applyMapStyle(map: GoogleMap) {
		try {
			if(map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))) {
				v { "Map style success" }
			} else {
				w { "Map style failed" }
			}
		} catch(ex: Resources.NotFoundException) {
			e(ex) { "Error applying map style" }
		}
	}

	override fun showBeacons(beacons: List<Beacon>) {
		beacons.iterator().forEach {
			showBeaconLocation(it)
		}
	}

	private fun showBeaconLocation(beacon: Beacon) {
		val opts = MarkerOptions()
		opts.position(beacon.getCoordinate().toLatLng())
		opts.icon(createIcon(R.drawable.beacon_marker))
		opts.anchor(0.5f, 0.5f)
		opts.flat(true)
		opts.title(beacon.name + " " + getString(R.string.beacon))
		opts.snippet(getSnippet(beacon))

		map.addMarker(opts)
	}

	private fun getSnippet(beacon: Beacon): String {
		return beacon.getCoordinate().toMinutesString()
	}

	private var meMarker: Marker? = null
	private var meCircle: Circle? = null
	override fun showMyLocation(loc: Coordinate) {
		//show blue dot
		val opts = MarkerOptions()
		opts.position(loc.toLatLng())
		opts.icon(createIcon(R.drawable.me_marker))
		opts.anchor(0.5f, 0.5f)
		opts.flat(true)
		meMarker?.remove()
		meMarker = map.addMarker(opts)

		//show accuracy circle
		val circleOpts = CircleOptions()
		circleOpts.fillColor(ContextCompat.getColor(this, R.color.me_acc_fill))
		circleOpts.strokeColor(ContextCompat.getColor(this, R.color.me_acc_stroke))
		circleOpts.strokeWidth(dpToPxF(1f))
		circleOpts.radius(loc.accuracy.toDouble())
		circleOpts.center(loc.toLatLng())
		meCircle?.remove()
		meCircle = map.addCircle(circleOpts)
	}

	val bitMap = mutableMapOf<Int, BitmapDescriptor>()
	private fun createIcon(drawableResId: Int): BitmapDescriptor {
		if(bitMap.containsKey(drawableResId))
			return bitMap.getValue(drawableResId)

		val drawable = ContextCompat.getDrawable(getCtx(), drawableResId)
			?: throw IllegalArgumentException("Drawable is null")
		val bitmap = createBitmap(drawable)
		val canvas = Canvas(bitmap)
		drawable.setBounds(0, 0, canvas.width, canvas.height)
		drawable.draw(canvas)
		val icon = createBitmap(drawable)
		val descriptor = BitmapDescriptorFactory.fromBitmap(icon)
		bitMap[drawableResId] = descriptor
		return descriptor
	}

	private fun createBitmap(drawable: Drawable): Bitmap {
		if(drawable is BitmapDrawable)
			return drawable.bitmap

		val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight,
			Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		drawable.setBounds(0, 0, canvas.width, canvas.height)
		drawable.draw(canvas)
		return bitmap
	}

	override fun zoomTo(beacons: List<Beacon>, myLocation: Coordinate, durationSeconds: Int) {
		v { "zoomTo($beacons, $myLocation, $durationSeconds)" }
		val b = LatLngBounds.builder()
		beacons.iterator().forEach {
			b.include(it.getCoordinate().toLatLng())
		}
		b.include(myLocation.toLatLng())
		val bounds = b.build()
		val margin = getScreenWidth() / 8
		zoomTo(CameraUpdateFactory.newLatLngBounds(bounds, margin), durationSeconds)
	}

	override fun zoomTo(centre: Coordinate, zoom: Float, durationSeconds: Int) {
		v { "zoomTo($centre, $zoom, $durationSeconds)" }
		val cu = CameraUpdateFactory.newLatLngZoom(centre.toLatLng(), zoom)
		zoomTo(cu, durationSeconds)
	}

	private fun zoomTo(cu: CameraUpdate, durationSeconds: Int) {
		if(durationSeconds == 0) {
			map.moveCamera(cu)
		} else {
			map.animateCamera(cu, durationSeconds * 1000, null)
		}
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_map
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_map
	}

	override fun onThisTabPressed() {
		presenter.onMapTabPressed()
	}
}