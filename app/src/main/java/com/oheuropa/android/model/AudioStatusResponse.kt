package com.oheuropa.android.model

import com.github.ajalt.timberkt.w

/**
 *
 * Class: com.oheuropa.android.model.AudioStatusResponse
 * Project: OhEuropa-Android
 * Created Date: 20/03/2018 12:43
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
data class AudioStatusResponse(val current_track: TrackInfo) {
	data class TrackInfo(val title: String) {

		fun getTitleName(): String {
			return title
		}

		fun getSongTitle(): String {
			val title = getTitleName()
			try {
				val firstQuote = title.indexOf("\"")
				val secondQuote = title.lastIndexOf("\"")
				if(firstQuote != -1 && secondQuote != -1)
					return title.substring(firstQuote + 1, secondQuote).trim()
				else {
					//look for anything agfter "singing"
					val sg = "singing"
					return title.substring(title.indexOf(sg)+sg.length).trim()
				}
			} catch (ex: Exception) {
				w(ex) { "Error parsing song title from $title" }
			}
			return ""
		}

		fun getPerformerName(): String {
			val title = getTitleName()
			try {
				return title.substring(0, title.indexOf("-")).trim()
			} catch (ex: Exception) {
				w(ex) { "Error parsing performer name from $title" }
			}
			return ""
		}
	}
}