package org.northwinds.app.sotachaser.ui

import android.content.Context
import android.content.Intent
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
import org.northwinds.app.sotachaser.BuildConfig
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.ActivityMapsBinding

@AndroidEntryPoint
class MapsActivity : AppCompatActivity() {
    private val Tag = "SOTAChaser-MapsActivity"
    lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
           throw RuntimeException("Test Crash") // Force a crash
        }

        //addContentView(crashButton, ViewGroup.LayoutParams(
        //       ViewGroup.LayoutParams.MATCH_PARENT,
        //       ViewGroup.LayoutParams.WRAP_CONTENT))

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val changelogVersion = prefs.getInt(getString(R.string.preference_changelog), 0)
        if (changelogVersion < BuildConfig.VERSION_CODE) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.title_changelog)

            builder.setMessage(R.string.changelog_text)
            builder.setPositiveButton("OK") { _, _ ->
                prefs.edit {
                    putInt(getString(R.string.preference_changelog),BuildConfig.VERSION_CODE)
                }
            }

            val dialog = builder.create()
            dialog.show()
        }

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
}
