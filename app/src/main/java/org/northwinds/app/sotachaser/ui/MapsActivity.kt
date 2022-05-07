package org.northwinds.app.sotachaser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.ActivityMapsBinding

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val Tag = "SOTAChaser-MapsActivity"
    val model: MapsViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityMapsBinding.inflate(layoutInflater)
     setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.association.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(Tag, "Selecting association: $position")
                //TODO("Not yet implemented")
                model.setAssociation(position)
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
        binding.region.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(Tag, "Selecting region: $position")
                //TODO("Not yet implemented")
                model.setRegion(position)
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
        model.associations.observe(this, {
            binding.association.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
        })
        model.regions.observe(this, {
            binding.region.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
        })
        Log.d(Tag, "Maps activity configured")
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

        Log.d(Tag, "Google Maps ready")
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        model.summits.observe(this) { summits ->
            Log.d(Tag, "Updating Map with summit count: " + summits.count())
            mMap.clear()
            if(summits.count() == 0)
                return@observe
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
                mMap.addMarker(
                    MarkerOptions().position(LatLng(it.latitude, it.longitude)).title(it.summitCode)
                )
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                LatLng(minLatitude, minLongitude),
                LatLng(maxLatitude, maxLongitude)), 75))
        }
    }
}