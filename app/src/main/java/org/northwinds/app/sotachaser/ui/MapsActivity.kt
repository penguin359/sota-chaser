package org.northwinds.app.sotachaser.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.preference.PreferenceManager

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
    lateinit var binding: ActivityMapsBinding

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
                model.setAssociation(position)
                //PreferenceManager.getDefaultSharedPreferences(applicationContext)
                getPreferences(Context.MODE_PRIVATE).edit {
                    putString("association", model.associations.value!![position])
                    //putString("region", "")
                }
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
            }
        }
        binding.region.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(Tag, "Selecting region: $position")
                model.setRegion(position)
                getPreferences(Context.MODE_PRIVATE).edit {
                    putString("region", model.regions.value!![position])
                }
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
            }
        }
        model.associations.observe(this) {
            binding.association.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
            val last = getPreferences(Context.MODE_PRIVATE).getString("association", "")
            if(last != "") {
                val index = model.associations.value?.indexOf(last) ?: -1
                if(index >= 0) {
                    binding.association.setSelection(index)
                }
            }
        }
        model.regions.observe(this) {
            binding.region.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it)
            val last = getPreferences(Context.MODE_PRIVATE).getString("region", "")
            if(last != "") {
                val index = model.regions.value?.indexOf(last) ?: -1
                if(index >= 0) {
                    binding.region.setSelection(index)
                }
            }
        }
        val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
           throw RuntimeException("Test Crash") // Force a crash
        }

        //addContentView(crashButton, ViewGroup.LayoutParams(
        //       ViewGroup.LayoutParams.MATCH_PARENT,
        //       ViewGroup.LayoutParams.WRAP_CONTENT))

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val asked = prefs.getBoolean(getString(R.string.preference_asked_for_consent), false)
        if (!asked) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.title_analytics))

            val analytics = arrayOf(
                getString(R.string.title_anonymous_usage),
                getString(R.string.title_crash_reports))
            var checkedItems = booleanArrayOf(false, false)
            builder.setMultiChoiceItems(analytics, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }

            builder.setPositiveButton("OK") { _, _ ->
                prefs.edit {
                    putBoolean(getString(R.string.preference_enable_analytics), checkedItems[0])
                    putBoolean(getString(R.string.preference_enable_crash_reports), checkedItems[1])
                    putBoolean(getString(R.string.preference_asked_for_consent), true)
                }
            }

            val dialog = builder.create()
            dialog.show()
        }

        Log.d(Tag, "Maps activity configured")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //val inflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
                true
            }
            R.id.feedback -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.Builder().scheme("mailto").build())
                //intent.type = "text/html"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("penguin359@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for SOTA Chaser")
                intent.putExtra(Intent.EXTRA_TEXT, "Version 0.1")
                startActivity(Intent.createChooser(intent, "Send Feedback"))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                    MarkerOptions().position(LatLng(it.latitude, it.longitude)).title(it.code)
                )
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                LatLng(minLatitude, minLongitude),
                LatLng(maxLatitude, maxLongitude)), 75))
        }
    }
}
