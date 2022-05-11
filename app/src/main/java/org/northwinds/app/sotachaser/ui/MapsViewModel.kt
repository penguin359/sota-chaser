package org.northwinds.app.sotachaser.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SummitInterface
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.room.Summit
import org.northwinds.app.sotachaser.room.SummitDatabase
import java.util.concurrent.ExecutorService
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
class MapsViewModel @Inject constructor(app: Application, private val executorService: ExecutorService) : AndroidViewModel(app) {
    private val Tag = "SOTAChaser-MapsViewModel"
    private val context = getApplication<Application>().applicationContext
    private val db = Room.databaseBuilder(context, SummitDatabase::class.java, "database").build()
    private val dao = db.summitDao()

    private val _associations = MutableLiveData<List<String>>().apply {
        //value = SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region.keys.toList()
        value = listOf()
    }

    init {
        Log.i(Tag, "Starting new model view")
        executorService.execute {
            var items = dao.getAssociations()
            if(items.count() <= 0) {
                val input = context.resources.openRawResource(R.raw.summitslist)
                val list = SummitList(input)

                SummitInterface.loadDatabase(dao, list)
                items = dao.getAssociations()
            }
            //_associations.postValue(SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region.keys.toList())
            _associations.postValue(items.map { it.code })
        }
    }

    val associations: LiveData<List<String>> = _associations

    private var association = ""

    private val _regions = MutableLiveData<List<String>>().apply {
        value = listOf()
    }

    val regions: LiveData<List<String>> = _regions

    private var region = ""

    fun setAssociation(entry: Int) {
        val newAssociation = associations.value!![entry]
        if(newAssociation == association)
            return
        association = newAssociation
        Log.d(Tag, "Selected association: $association")
        _regions.value = listOf()
        executorService.execute {
            //_regions.postValue(SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region[association]!!.keys.toList())
            _regions.postValue(dao.getRegionsInAssociationName(association).map { it.code })
        }
    }

    private val _summits = MutableLiveData<List<Summit>>().apply {
        value = listOf()
    }

    val summits: LiveData<List<Summit>> = _summits

    fun setRegion(entry: Int) {
        val newRegion = regions.value!![entry]
        if(newRegion == region)
            return
        val region = regions.value!![entry]
        Log.d(Tag, "Selected region: $region")
        _summits.value = listOf()
        executorService.execute {
            //_summits.postValue(SummitList(context.resources.openRawResource(R.raw.summitslist)).summits_by_region[association]!![region]!!.toList())
            _summits.postValue(dao.getSummits(association, region))
        }
    }

    override fun onCleared() {
        super.onCleared()
        executorService.shutdown()
    }
}
