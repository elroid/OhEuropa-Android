package com.oheuropa.android.model

/**
 *
 * Class: com.oheuropa.android.model.Model
 * Project: OhEuropa-Android
 * Created Date: 06/03/2018 17:29
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
object Model {
	data class Beacon(
		val id:Int,
		val name:String,//"Alvington",
		val placeid:String,//"ALV1X",
		val lat:Float,//"51.70524147484223",
		val lng:Float,//"-2.5765114127077595",
		val datecreated:String,//"2018-03-01 23:20:00",
		val centerradius:Int,//"40",
		val innerradius:Int,//"60",
		val outerradius:Int,//"100",
		val radioplays:Int,//"0",
		val nearbys:Int//"0"
	)
	data class GetPlacesResponse(val success:Boolean, val data:List<Beacon>)
}