package com.oheuropa.android.ui.test;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.oheuropa.android.model.Beacon;
import com.oheuropa.android.ui.base.BaseActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

/**
 * Class: com.oheuropa.android.ui.test.TestActivity
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 16:48
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
public class TestActivity extends BaseActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

	}

	private Observable<Beacon> getClosestBeacon(
		Observable<List<Beacon>> allBeacons,
		Observable<Location> currentLocation){
		return Observable.combineLatest(allBeacons,
			currentLocation,
			new BiFunction<List<Beacon>, Location, Beacon>()
			{
				@Override
				public Beacon apply(List<Beacon> allBeacons, final Location location) throws Exception{

					Collections.sort(allBeacons, new BeaconDistanceComparator(location));
					return allBeacons.get(0);
				}
			});
	}

	private class BeaconDistanceComparator implements Comparator<Beacon>{
		private Location loc1 = new Location("");
		private Location loc2 = new Location("");
		private Location currentLocation;

		BeaconDistanceComparator(Location currentLocation){
			this.currentLocation = currentLocation;
		}

		@Override
		public int compare(Beacon beacon1, Beacon beacon2){
			loc1.setLatitude(beacon1.getLat());
			loc1.setLongitude(beacon1.getLng());
			loc2.setLatitude(beacon2.getLat());
			loc2.setLongitude(beacon2.getLng());
			return Float.valueOf(loc1.distanceTo(currentLocation))
				.compareTo(loc2.distanceTo(currentLocation));
		}
	}
}
