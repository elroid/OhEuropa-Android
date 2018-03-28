package com.oheuropa.android.model

/**
 * Created Date: 26/03/2018 19:58
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
data class UserRequest(
	private val newuser: String? = null,
	private val userid: String? = null,

	private val newevent: String? = null,
	private val placeid: String? = null,
	private val zoneid: Zone? = null,
	private val action: Action? = null
) {
	constructor(userId: String) : this(userid = userId, newuser = "1")

	constructor(userId: String, placeId: String, zoneId: Zone, action: Action)
		: this(newevent = "1", userid = userId, placeid = placeId, zoneid = zoneId, action = action)

	enum class Zone { C, O, I }
	enum class Action { Exited, Entered }

	companion object {
		fun map(circleState: BeaconLocation.CircleState): Zone? {
			return when (circleState) {
				BeaconLocation.CircleState.CENTRE -> Zone.C
				BeaconLocation.CircleState.INNER -> Zone.I
				BeaconLocation.CircleState.OUTER -> Zone.O
				else -> null
			}
		}
	}
}