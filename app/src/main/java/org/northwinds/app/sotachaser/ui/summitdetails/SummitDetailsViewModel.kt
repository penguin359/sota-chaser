package org.northwinds.app.sotachaser.ui.summitdetails

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.northwinds.app.sotachaser.domain.models.GpxTrack
import org.northwinds.app.sotachaser.repository.SummitsRepository
import javax.inject.Inject

@HiltViewModel
class SummitDetailsViewModel @Inject constructor(private val repo: SummitsRepository) : ViewModel() {
    private val _tuple = MutableLiveData(Triple("", "", ""))

    var tracks: LiveData<List<GpxTrack>> = MutableLiveData(listOf())

    val summit = _tuple.switchMap {
        if(it.equals(Triple("", "", "")))
            return@switchMap MutableLiveData()
        repo.getSummitByCode(it.first, it.second, it.third).map { a ->
            a?.let { v -> tracks = repo.getGpxTracks(v) }
            tracks.observeForever {  }
            val code = "${it.first}/${it.second}-${a?.code}"
            a?.copy(code = code)?.also { v ->
                viewModelScope.launch { try { repo.updateGpxTracks(v) } catch(ex: RuntimeException) {} }
            }
        }
    }.distinctUntilChanged()

    fun updateSummit(a: String, r: String, s: String) = viewModelScope.launch {
        repo.updateSummit(a, r, s)
        _tuple.value = Triple(a,  r, s)
    }

    //private var _summit = LiveData<Summit?>()
    //val summit: LiveData<Summit?> get() = _summit
    //
    //fun updateSummit(a: String, r: String, s: String) = viewModelScope.launch {
    //    repo.updateSummit(a, r, s)
    //    _summit = repo.getSummitByCode(a, r, s)
    //}
}
