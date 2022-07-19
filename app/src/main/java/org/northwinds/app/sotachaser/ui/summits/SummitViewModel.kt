package org.northwinds.app.sotachaser.ui.summits

import android.location.Location
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.repository.SummitsRepository
import org.northwinds.app.sotachaser.ui.abstraction.AbstractViewModel
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
class SummitViewModel @Inject constructor(private val repo: SummitsRepository) : AbstractViewModel<Summit>() {
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    fun setLocation(location: Location?) {
        _location.value = location
    }

    private val _association = MutableLiveData<String>().apply {
        value = ""
    }

    internal val association: LiveData<String> = _association

    private val _region = MutableLiveData<String>().apply {
        value = ""
    }

    internal val region: LiveData<String> = _region

    fun setRegion(a: String, r: String) {
        _association.value = a
        _region.value = r
    }

    override val list_items = filter.switchMap { f ->
        association.switchMap { a ->
            region.switchMap { r ->
                repo.getSummits(a ?: "", r ?: "").map { items ->
                    items.filter {
                        it.code.contains(f, ignoreCase = true) || it.name.contains(
                            f,
                            ignoreCase = true
                        )
                    }
                }
            }
        }
    }

    override fun refresh(force: Boolean) = viewModelScope.launch {
        //repo.refreshSummits()
    }
}
