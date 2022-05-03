package org.northwinds.app.sotachaser.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.SummitRecord

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
class MapsViewModel(app: Application) : AndroidViewModel(app) {
    private val Tag = "SOTAChaser-MapsViewModel"
    private val context = getApplication<Application>().applicationContext

    private val _associations = MutableLiveData<List<String>>().apply {
        value = SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region.keys.toList()
    }

    val associations: LiveData<List<String>> = _associations

    private val _regions = MutableLiveData<List<String>>().apply {
        value = listOf()
    }

    val regions: LiveData<List<String>> = _regions
    private var association = ""

    fun set_association(entry: Int) {
        association = associations.value!![entry]
        Log.d(Tag, "Selected association: $association")
        _regions.value = SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region[association]!!.keys.toList()
    }

    private val _summits = MutableLiveData<List<SummitRecord>>().apply {
        value = listOf()
    }

    val summits: LiveData<List<SummitRecord>> = _summits

    fun set_region(entry: Int) {
        val region = regions.value!![entry]
        Log.d(Tag, "Selected region: $region")
        _summits.value = SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region[association]!![region]!!.toList()
    }
}
