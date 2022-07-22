package org.northwinds.app.sotachaser.ui.abstraction

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.ui.map.MapsViewModel
import java.util.concurrent.ExecutorService

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

abstract class AbstractViewModel<E>(private val executorService: ExecutorService, private val repo: SummitsRepository) : ViewModel() {
    //private val context = getApplication<Application>().applicationContext

    //private val _location = MutableLiveData<Location?>()
    //val location: LiveData<Location?> = _location

    //fun setLocation(location: Location?) {
    //    _location.value = location
    //}

    //init {
    //    Log.i(TAG, "Starting new model view")
    //    executorService.execute {
    //        //_associations.postValue(repo.getAssociations().value?.map { it.code } ?: listOf())
    //        runBlocking {
    //            repo.checkForRefresh()
    //        }
    //    }
    //}

    //abstract val _list_items: MutableLiveData<List<E>>
    //val list_items: LiveData<List<E>> = _list_items

    init {
        executorService.execute {
            runBlocking {
                repo.checkForRefresh()
            }
        }
    }


    private val _filter = MutableLiveData<String>().apply { value = "" }
    protected val filter: LiveData<String> = _filter

    internal fun setFilter(filter: String) {
        _filter.value = filter
    }

    abstract val list_items: LiveData<List<E>>

    abstract fun refresh(force: Boolean = false): Job
}
