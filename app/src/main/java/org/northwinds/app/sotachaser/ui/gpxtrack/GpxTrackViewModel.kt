package org.northwinds.app.sotachaser.ui.gpxtrack

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.repository.SummitsRepository
import javax.inject.Inject

/*
 * Copyright (c) 2022 Loren M. Lang
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@HiltViewModel
class GpxTrackViewModel @Inject constructor(app: Application, private val repo: SummitsRepository) : AndroidViewModel(app) {
    private data class TrackTuple(val a: String, val r: String, val s: String, val t: Long)

    private val _tuple = MutableLiveData(TrackTuple("", "", "", 0))

    val summit = _tuple.switchMap {
        if(it.equals(TrackTuple("", "", "", 0)))
            return@switchMap MutableLiveData()
        repo.getSummitByCode(it.component1(), it.component2(), it.component3()).map { a ->
            val code = "${it.component1()}/${it.component2()}-${it.component3()}"
            a?.copy(code = code)
        }
    }.distinctUntilChanged()

    val track = _tuple.switchMap {
        repo.getGpxTrack(it.component4())
    }.distinctUntilChanged()

    val points = track.switchMap {
        if(it == null)
            return@switchMap MutableLiveData(listOf())
        repo.getGpxPoints(it)
    }.distinctUntilChanged()

    fun updateTrack(a: String, r: String, s: String, t: Long) = viewModelScope.launch {
        _tuple.value = TrackTuple(a, r, s, t)
    }

    companion object {
        private const val TAG = "SOTAChaser-GpxTrackViewModel"
    }
}
