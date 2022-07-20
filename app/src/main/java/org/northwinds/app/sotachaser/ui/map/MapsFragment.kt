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
package org.northwinds.app.sotachaser.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SimpleAdapter
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentMapsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val model: MapsViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    lateinit var binding: FragmentMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapsBinding.inflate(inflater)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.association.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(Companion.Tag, "Selecting association: $position")
                model.setAssociation(position)
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString("association", model.associations.value!![position].code)
                    //putString("region", "")
                }
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
            }
        }
        binding.region.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(Companion.Tag, "Selecting region: $position")
                model.setRegion(position)
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString("region", model.regions.value!![position].code)
                }
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
            }
        }
        model.associations.observe(viewLifecycleOwner) { value ->
            val adapter = SimpleAdapter(requireContext(), value.map {
                    mapOf("code" to it.code, "name" to it.name)
                }, R.layout.spinner_entry, arrayOf("code", "name"), intArrayOf(R.id.code, R.id.name))
            adapter.setDropDownViewResource(R.layout.spinner_dropdown)
            binding.association.adapter = adapter
            val last = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("association", "")
            if(last != "") {
                val index = model.associations.value?.indexOfFirst { it.code == last } ?: -1
                if(index >= 0) {
                    binding.association.setSelection(index)
                }
            }
        }
        model.regions.observe(viewLifecycleOwner) { value ->
            val adapter = SimpleAdapter(requireContext(), value.map {
                mapOf("code" to it.code, "name" to it.name)
            }, R.layout.spinner_entry, arrayOf("code", "name"), intArrayOf(R.id.code, R.id.name))
            adapter.setDropDownViewResource(R.layout.spinner_dropdown)
            binding.region.adapter = adapter
            val last = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("region", "")
            if(last != "") {
                val index = model.regions.value?.indexOfFirst { it.code == last } ?: -1
                if(index >= 0) {
                    binding.region.setSelection(index)
                }
            }
        }
        return binding.root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        Log.d(Companion.Tag, "Google Maps ready")
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        model.regionDetails.observe(this) { _ ->
            //mMap.addPolygon(PolygonOptions().add(
            //    LatLng(region.minLat!!, region.minLong!!),
            //    LatLng(region.minLat, region.maxLong!!),
            //    LatLng(region.maxLat!!, region.maxLong),
            //    LatLng(region.maxLat, region.minLong)))
        }
        model.summits.observe(this) { summits ->
            if(summits == null)
                return@observe
            Log.d(Companion.Tag, "Updating Map with summit count: " + summits.count())
            mMap.clear()
            if(summits.count() == 0)
                return@observe
            val region = model.regionDetails.value!!
            if(region.minLat != null && region.maxLat != null && region.minLong != null && region.maxLong != null) {
                mMap.addPolygon(
                    PolygonOptions().add(
                        LatLng(region.minLat, region.minLong),
                        LatLng(region.minLat, region.maxLong),
                        LatLng(region.maxLat, region.maxLong),
                        LatLng(region.maxLat, region.minLong)
                    )
                        .fillColor(0x400000ff.toInt())
                        .strokeColor(0x800000ff.toInt())
                )
            }
            var minLatitude = summits[0].latitude
            var maxLatitude = summits[0].latitude
            var minLongitude = summits[0].longitude
            var maxLongitude = summits[0].longitude
            summits.forEach {
                if(it.latitude < minLatitude)
                    minLatitude = it.latitude
                if(it.latitude > maxLatitude)
                    maxLatitude = it.latitude
                if(it.longitude < minLongitude)
                    minLongitude = it.longitude
                if(it.longitude > maxLongitude)
                    maxLongitude = it.longitude
                val hue = when(it.points) {
                    1 -> BitmapDescriptorFactory.HUE_BLUE
                    2 -> BitmapDescriptorFactory.HUE_AZURE
                    3 -> BitmapDescriptorFactory.HUE_GREEN
                    4 -> BitmapDescriptorFactory.HUE_GREEN
                    5 -> BitmapDescriptorFactory.HUE_YELLOW
                    6 -> BitmapDescriptorFactory.HUE_YELLOW
                    7 -> BitmapDescriptorFactory.HUE_ORANGE
                    8 -> BitmapDescriptorFactory.HUE_ORANGE
                    else -> BitmapDescriptorFactory.HUE_RED
                }
                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title(it.code)
                        .snippet(it.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(hue))
                )
            }
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds(
                LatLng(minLatitude, minLongitude),
                LatLng(maxLatitude, maxLongitude)
                    ), 75))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val Tag = "SOTAChaser-MapsFragment"
    }
}
