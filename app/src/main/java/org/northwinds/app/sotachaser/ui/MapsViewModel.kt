package org.northwinds.app.sotachaser.ui

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.SummitList
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.repository.SummitsRepositoryImpl
import org.northwinds.app.sotachaser.room.SummitDao
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
class MapsViewModel @Inject constructor(app: Application, private val executorService: ExecutorService, private val repo: SummitsRepository) : AndroidViewModel(app) {
    private val context = getApplication<Application>().applicationContext

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    fun setLocation(location: Location?) {
        _location.value = location
    }

    //private val _associations = repo.getAssociations()MutableLiveData<List<String>>().apply {
    //    value = listOf()
    //}

    init {
        Log.i(Companion.TAG, "Starting new model view")
        //executorService.execute {
        //    _associations.postValue(repo.getAssociations().value?.map { it.code } ?: listOf())
        //}
    }

    val associations: LiveData<List<String>> = Transformations.map(repo.getAssociations()) { list -> list.map { it.code }}//_associations

    private val _association = MutableLiveData<String>().apply {
        value = ""
    }
    private val association: LiveData<String> = _association

    //private val _regions = MutableLiveData<List<String>>().apply {
    //    value = listOf()
    //}

    //val regions: LiveData<List<String>> = _regions
    val regions: LiveData<List<String>> = Transformations.switchMap(association) { name ->
        Transformations.map(repo.getRegionsInAssociationName(name)) { list ->
            list.map { it.code }
        }
    }

    private val _region = MutableLiveData<String>().apply {
    }
    private val region: LiveData<String> = _region

    fun setAssociation(entry: Int) {
        val newAssociation = associations.value!![entry]
        if(newAssociation == association.value)
            return
        _association.value = newAssociation
        Log.d(Companion.TAG, "Selected association: $association")
        //_regions.value = listOf()
        //executorService.execute {
        //    _regions.postValue(repo.getRegionsInAssociationName(association.value!!).value?.map { it.code }
        //        ?: listOf())
        //}
    }

    //private val _summits = MutableLiveData<List<Summit>>().apply {
    //    value = listOf()
    //}

    //val summits: LiveData<List<Summit>> = _summits
    val summits: LiveData<List<Summit>> = Transformations.switchMap(region) { name ->
        repo.getSummits(association.value ?: "", name)
    }

    fun setRegion(entry: Int) {
        val newRegion = regions.value!![entry]
        if(newRegion == region.value)
            return
        _region.value = newRegion
        Log.d(Companion.TAG, "Selected region: $region")
        //_summits.value = listOf()
        //executorService.execute {
        //    _summits.postValue(repo.getSummits(association.value!!, region.value!!).value)
        //}
    }

    override fun onCleared() {
        super.onCleared()
        executorService.shutdown()
    }

    companion object {
        private const val TAG = "SOTAChaser-MapsViewModel"
    }
}
