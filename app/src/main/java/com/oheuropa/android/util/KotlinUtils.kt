package com.oheuropa.android.util

import io.reactivex.functions.BiFunction

/**
 * Created Date: 28/03/2018 14:11
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
class PairFunction<A,B>: BiFunction<A, B, Pair<A, B>> {
	override fun apply(t1: A, t2: B): Pair<A, B> {
		return Pair(t1, t2)
	}
}