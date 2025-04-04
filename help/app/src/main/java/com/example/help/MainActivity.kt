package com.example.help

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.language = Locale.US
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        startSpeechRecognition()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap!!.isMyLocationEnabled = true
            fetchUserLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun fetchUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    mMap?.addMarker(MarkerOptions().position(latLng).title("Your Location"))
                }
            }
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    handleVoiceCommand(matches[0].lowercase(Locale.getDefault()))
                }
                startSpeechRecognition()
            }

            override fun onError(error: Int) {
                startSpeechRecognition()
            }
            override fun onReadyForSpeech(params: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle) {}
            override fun onEvent(eventType: Int, params: Bundle) {}
        })

        speechRecognizer!!.startListening(intent)
    }

    private fun handleVoiceCommand(command: String) {
        when {
            command.contains("call") || command.contains("emergency") -> {
                makeEmergencyCall("+918610929397")
                sendEmergencySMS()
            }
            command.contains("help") -> {
                speakOut("Voice input activated. Say 'call' for emergency or 'navigate to' followed by a location name.")
            }
            command.startsWith("navigate to") -> {
                val destination = command.replace("navigate to", "").trim()
                navigateTo(destination)
            }
            else -> speakOut("I did not understand. Please say again.")
        }
    }

    private fun sendEmergencySMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val message = "Emergency! Live location: https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    SmsManager.getDefault().sendTextMessage("+918610929397", null, message, null, null)
                    speakOut("Emergency SMS sent with location.")
                } else {
                    speakOut("Unable to fetch location for SMS.")
                }
            }.addOnFailureListener {
                speakOut("Failed to get location for SMS.")
            }
        } else {
            speakOut("Location permission not granted.")
        }
    }


    private fun navigateTo(destination: String) {
        val uri = Uri.parse("google.navigation:q=$destination")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            speakOut("Navigating to $destination")
        } else {
            speakOut("Google Maps not installed.")
        }
    }

    private fun makeEmergencyCall(number: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
            startActivity(intent)
        } else {
            requestCallPermission()
        }
    }

    private fun speakOut(message: String) {
        textToSpeech!!.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun requestCallPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) fetchUserLocation()
            CALL_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) speakOut("Call permission granted.")
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val CALL_PERMISSION_REQUEST_CODE = 2
    }
}
