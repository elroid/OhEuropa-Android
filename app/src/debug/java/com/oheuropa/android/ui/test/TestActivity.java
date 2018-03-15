package com.oheuropa.android.ui.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.oheuropa.android.ui.base.BaseActivity;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


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

	private Observable<Float> doSomething(){
		return null;//new SensorObservable().observe();
	}

	abstract class SensorObservable
	{
		SensorManager mgr;

		public SensorObservable(Context ctx){
			this.mgr = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
		}

		abstract float convertToDegrees(float[] eventValues);
		abstract int getType();

		Callable<Sensor> callable = new Callable<Sensor>(){
			@Override
			public Sensor call() throws Exception{
				return mgr.getDefaultSensor(getType());
			}
		};
		class Listener implements SensorEventListener
		{
			ObservableEmitter<Float> emitter;

			public Listener(ObservableEmitter<Float> emitter){
				this.emitter = emitter;
			}

			@Override
			public void onSensorChanged(SensorEvent event){
				emitter.onNext(convertToDegrees(event.values));
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy){

			}
		}
		Function<Sensor, ObservableSource<Float>> function = new Function<Sensor, ObservableSource<Float>>()
		{
			@Override
			public ObservableSource<Float> apply(final Sensor sensor) throws Exception{
				return Observable.create(new ObservableOnSubscribe<Float>(){
					@Override
					public void subscribe(final ObservableEmitter<Float> e) throws Exception{
						mgr.registerListener(new Listener(e), sensor, SensorManager.SENSOR_DELAY_NORMAL);
					}
				});
			}
		};
		Consumer<Sensor> consumer = new Consumer<Sensor>()
		{
			@Override
			public void accept(Sensor sensor) throws Exception{
				//mgr.unregisterListener(listener);
			}
		};

		public Observable<Float> observe(){
			return Observable.using(callable, function, consumer);
		}
	}

	/*float[] orientation = new float[3];
	float[] rMat = new float[9];

	public void onAccuracyChanged(Sensor sensor, int accuracy ) {}

	@Override
	public void onSensorChanged( SensorEvent event ) {
		if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
			// calculate th rotation matrix
			SensorManager.getRotationMatrixFromVector( rMat, event.values );
			float[] orient = SensorManager.getOrientation( rMat, orientation )
			// get the azimuth value (orientation[0]) in degree
			int azimuth = (int) ( Math.toDegrees( orient[0] ) + 360 ) % 360;
		}
	}*/

}
