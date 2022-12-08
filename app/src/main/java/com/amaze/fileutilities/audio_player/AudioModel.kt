/*
 * Copyright (C) 2014-2021 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.fileutilities.audio_player

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This class represents any filetype that is openable in a [AudioPlayerDialogActivity]
 * and contains all information to show it in one
 *
 */
@Parcelize
data class LocalAudioModel(
    var id: Long,
    private var uri: Uri,
    private val mimeType: String
) : Parcelable, AudioModel {
    override fun getUri(): Uri {
        return uri
    }

    override fun getName(): String {
        return uri.path!!
    }

    override fun getMimeType(): String {
        return mimeType
    }
}

interface AudioModel {
    fun getUri(): Uri
    fun getName(): String
    fun getMimeType(): String
}
