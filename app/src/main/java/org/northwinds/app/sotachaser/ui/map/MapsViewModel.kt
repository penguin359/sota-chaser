package org.northwinds.app.sotachaser.ui.map

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.repository.SummitsRepository
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

    init {
        Log.i(TAG, "Starting new model view")
        executorService.execute {
            //_associations.postValue(repo.getAssociations().value?.map { it.code } ?: listOf())
            runBlocking {
                repo.checkForRefresh()
            }
        }
    }

    val associations = repo.getAssociations()

    private val _association = MutableLiveData<String>().apply {
        value = ""
    }
    internal val association: LiveData<String> = _association

    private val _associationDetails = MutableLiveData<Association>()
    private val associationDetails: LiveData<Association> = _associationDetails

    val regions: LiveData<List<Region>> = Transformations.switchMap(association) { name ->
        repo.getRegionsInAssociationName(name)
    }

    private val _region = MutableLiveData<String>().apply {
    }
    private val region: LiveData<String> = _region

    private val _regionDetails = MutableLiveData<Region>()
    val regionDetails: LiveData<Region> = _regionDetails

    fun setAssociation(entry: Int) {
        // TODO replace the !!
        val newAssociation = associations.value!![entry].code
        if(newAssociation == association.value)
            return
        _association.value = newAssociation
        _associationDetails.value = associations.value!![entry]
        Log.d(TAG, "Selected association: $newAssociation")
        //_regions.value = listOf()
        //executorService.execute {
        //    _regions.postValue(repo.getRegionsInAssociationName(association.value!!).value?.map { it.code }
        //        ?: listOf())
        //}
        viewModelScope.launch {
            repo.updateAssociation(newAssociation)
        }
    }

    val summits: LiveData<List<Summit>> = Transformations.switchMap(region) { name ->
        repo.getSummits(association.value ?: "", name)
    }

    fun setRegion(entry: Int) {
        val newRegion = regions.value!![entry].code
        if(newRegion == region.value)
            return
        _region.value = newRegion
        _regionDetails.value = regions.value!![entry]
        Log.d(TAG, "Selected region: $newRegion")
        //_summits.value = listOf()
        //executorService.execute {
        //    _summits.postValue(repo.getSummits(association.value!!, region.value!!).value)
        //}
        viewModelScope.launch {
            repo.updateRegion(association.value!!, region.value!!)
        }
    }

    fun refreshAssociations() = viewModelScope.launch {
        repo.refreshAssociations()
    }

    companion object {
        private const val TAG = "SOTAChaser-MapsViewModel"
    }
}
