package com.oheuropa.android.ui.map

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.content.ContextCompat.getDrawable
import android.view.View
import android.view.WindowManager
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.v
import com.github.ajalt.timberkt.w
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oheuropa.android.R
import com.oheuropa.android.data.local.PrefsHelper
import com.oheuropa.android.model.Beacon
import com.oheuropa.android.model.Coordinate
import com.oheuropa.android.ui.base.BottomNavActivity
import com.oheuropa.android.util.ViewUtils.Companion.dpToPx
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
const val REQUEST_CHECK_SETTINGS = 667
const val DEF_ZOOM = 15f

class MapActivity : BottomNavActivity(), OnMapReadyCallback, MapContract.View {

	companion object {
		fun createIntent(ctx: Context): Intent {
			return Intent(ctx, MapActivity::class.java)
		}
	}

	@Inject lateinit var presenter: MapContract.Presenter
	@Inject lateinit var prefs: PrefsHelper
	private lateinit var map: GoogleMap

	private var currentZoom: Float = DEF_ZOOM
	private var currentCentre: LatLng? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		d { "MapActivity.create: $savedInstanceState" }
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

	override fun onPause() {
		super.onPause()
		prefs.saveMapCentre(currentCentre, currentZoom)
	}

	override fun onStop() {
		presenter.stop()
		super.onStop()
	}

	override fun onMapReady(googleMap: GoogleMap) {
		map = googleMap
		applyMapStyle(map)

		map.setOnCameraIdleListener {
			currentZoom = map.cameraPosition.zoom
			currentCentre = map.cameraPosition.target
			d { "recording idle zoom($currentZoom) and pos($currentCentre)" }
		}

		//apply state if we have it
		val (centre, zoom) = prefs.restoreMapCentre()
		if (centre.isValid()) {
			d { "restoring saved centre($centre) and zoom($zoom)" }
			val cu = CameraUpdateFactory.newLatLngZoom(centre.toLatLng(), zoom)
			map.moveCamera(cu)
		}

		askForPermissions()
	}

	private fun applyMapStyle(map: GoogleMap) {
		try {
			if (map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))) {
				v { "Map style success" }
			} else {
				w { "Map style failed" }
			}
		} catch (ex: Resources.NotFoundException) {
			e(ex) { "Error applying map style" }
		}
	}

	override fun showBeacons(beacons: List<Beacon>) {
		beacons.iterator().forEach {
			map.addMarker(MarkerOptions().position(it.getCoordinate().toLatLng()))
		}
	}

	var meMarker: Marker? = null
	var meCircle: Circle? = null
	override fun showMyLocation(loc: Coordinate) {
		//show blue dot
		val opts = MarkerOptions()
		opts.position(loc.toLatLng())
		val drawable = getDrawable(getCtx(), R.drawable.me_marker)
		val icon = createBitmap(drawable, dpToPx(18), dpToPx(18))
		opts.icon(BitmapDescriptorFactory.fromBitmap(icon))
		opts.anchor(0.5f, 0.5f)
		opts.flat(true)
		meMarker?.remove()
		meMarker = map.addMarker(opts)

		//show accuracy circle
		val copts = CircleOptions()
		copts.fillColor(getColor(this, R.color.me_acc_fill))
		copts.strokeColor(getColor(this, R.color.me_acc_stroke))
		copts.strokeWidth(dpToPxF(1f))
		copts.radius(loc.accuracy.toDouble())
		copts.center(loc.toLatLng())
		meCircle?.remove()
		meCircle = map.addCircle(copts)
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
		val margin = getScreenWidth() / 4
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, margin))
	}

	override fun askForPermissions() {
		Dexter.withActivity(this)
			.withPermissions(
				Manifest.permission.ACCESS_FINE_LOCATION)
			.withListener(object : MultiplePermissionsListener {
				override fun onPermissionsChecked(report: MultiplePermissionsReport) {
					presenter.start()
				}

				override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>,
																token: PermissionToken) {
					AlertDialog.Builder(getCtx())
						.setMessage(R.string.loc_justification)
						.setPositiveButton(R.string.loc_ok) { _, _ -> token.continuePermissionRequest() }
						.setNegativeButton(R.string.loc_quit) { _, _ -> quit() }
						.create().show()
				}
			})
			.check()
	}

	override fun resolveApiIssue(ex: ResolvableApiException) {
		try {
			// Show the dialog by calling startResolutionForResult(),
			// and check the result in onActivityResult().
			ex.startResolutionForResult(this@MapActivity, REQUEST_CHECK_SETTINGS)
		} catch (sendEx: IntentSender.SendIntentException) {
			w { "Send intent exception" }// Ignore the error.
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		d { "onActivityResult($requestCode, $resultCode, $data)" }
		if (requestCode == REQUEST_CHECK_SETTINGS) {
			if (resultCode == Activity.RESULT_OK)
				presenter.start()
			else
				askForPermissions()
		} else
			super.onActivityResult(requestCode, resultCode, data)
	}

	override fun getLayoutId(): Int {
		return R.layout.activity_map
	}

	override fun getNavigationMenuItemId(): Int {
		return R.id.navigation_map
	}
}