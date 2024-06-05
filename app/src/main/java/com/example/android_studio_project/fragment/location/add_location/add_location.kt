package com.example.android_studio_project.fragment.location.add_location

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.*
import com.example.android_studio_project.data.retrofit.services.LocationService
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import com.example.android_studio_project.data.retrofit.models.LocationModelCreate
import com.example.android_studio_project.data.retrofit.models.PhotoModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date
import java.util.TimeZone


class add_location(private val tripUuid: UUID) : Fragment(), OnMapReadyCallback {

    private lateinit var photosGridView: GridView
    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter

    private lateinit var locationService: LocationService
    private lateinit var locationTypeSpinner: Spinner
    private val locationTypeMap = mutableMapOf<String, UUID>()

    private lateinit var dateTextInput: EditText

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedLatLng: LatLng? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)

        locationService = LocationService(requireContext())
        locationTypeSpinner = view.findViewById(R.id.location_type)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationTypeSpinner.adapter = adapter

        getTypes(adapter)

        val addPhotosButton: Button = view.findViewById(R.id.add_photos)
        photosGridView = view.findViewById(R.id.photos_grid)

        photosAdapter = PhotosAdapter()
        photosGridView.adapter = photosAdapter

        addPhotosButton.setOnClickListener {
            openGallery()
        }

        val saveBtn: Button = view.findViewById(R.id.save_btn)
        saveBtn.setOnClickListener {
            saveLocation()
        }

        dateTextInput = view.findViewById(R.id.location_date)
        dateTextInput.setOnClickListener {
            showDatePicker()
        }

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLatLng = latLng
        }

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val currentLocation = task.result
                    val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                    selectedLatLng = currentLatLng
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateDisplay = displayFormat.format(Date(selection ?: 0))
            dateTextInput.setText(dateDisplay)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }


    private val selectPhotosLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        photosList.addAll(uris)
        photosAdapter.notifyDataSetChanged()
    }

    private fun getTypes(adapter: ArrayAdapter<String>) {
        locationService.getAllTypes(
            onResponse = { types ->
                if (types != null) {
                    val typeNames = types.map { it.name ?: "Unknown" }
                    adapter.clear()
                    adapter.addAll(typeNames)
                    adapter.notifyDataSetChanged()

                    types.forEach { type ->
                        if (type.name != null) {
                            locationTypeMap[type.name] = type.uuid
                        }
                    }
                }
            },
            onFailure = {
            }
        )
    }

    private fun saveLocation() {
        val selectedTypeName = locationTypeSpinner.selectedItem as? String
        val selectedTypeUuid = locationTypeMap[selectedTypeName]
        val locationName = view?.findViewById<EditText>(R.id.location_name)?.text.toString()
        val locationDescription = view?.findViewById<EditText>(R.id.location_description)?.text.toString()
        val locationRating = view?.findViewById<RatingBar>(R.id.location_rating)?.rating ?: 0.0f
        val locationDateStr = dateTextInput.text.toString()

        if (selectedTypeUuid != null && locationName.isNotEmpty() && locationDescription.isNotEmpty() && locationDateStr.isNotEmpty()) {

            val isoDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedCalendar = isoDateFormat.parse(locationDateStr)
            val formattedDate =
                selectedCalendar?.let {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(
                        it
                    )
                }

            val location = LocationModelCreate(
                uuid = UUID.randomUUID(),
                name = locationName,
                description = locationDescription,
                typeId = selectedTypeUuid,
                rating = locationRating,
                latitude = selectedLatLng?.latitude,
                longitude = selectedLatLng?.longitude,
                date = formattedDate
            )

            locationService.createLocation(location,
                onResponse = { _, locationUuid ->
                    val tripLocation = locationUuid?.let { TripLocationModel(tripId = tripUuid, locationId = it) }
                    if (tripLocation != null) {
                        locationService.createTripLocation(tripLocation,
                            onResponse = { _ ->
                                var photosProcessed = 0
                                val totalPhotos = photosList.size
                                photosList.forEach { uri ->
                                    if (isAdded) {
                                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                                        val image = convertBitmapToBase64(bitmap)
                                        if (image != null) {
                                            val photo = PhotoModel(uuid = UUID.randomUUID(), data = image, locationId = locationUuid.toString())
                                            locationService.createPhoto(photo,
                                                onResponse = {
                                                    photosProcessed++
                                                    if (photosProcessed == totalPhotos) {
                                                        Toast.makeText(requireContext(), getString(R.string.location_add_succ), Toast.LENGTH_LONG).show()
                                                        parentFragmentManager.popBackStack()
                                                    }
                                                },
                                                onFailure = {
                                                    Toast.makeText(requireContext(), getString(R.string.location_add_error), Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        }
                                    }
                                }
                                // Verificar se não há fotos para processar
                                if (totalPhotos == 0) {
                                    // Nenhuma foto para processar, podemos retornar imediatamente
                                    Toast.makeText(requireContext(), getString(R.string.location_add_succ), Toast.LENGTH_LONG).show()
                                    parentFragmentManager.popBackStack()
                                }
                            },
                            onFailure = {
                                Toast.makeText(requireContext(), getString(R.string.location_add_error), Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                onFailure = {
                    Toast.makeText(requireContext(), getString(R.string.location_add_error), Toast.LENGTH_LONG).show()
                }
            )

        } else {
            Toast.makeText(requireContext(), getString(R.string.fill_fields), Toast.LENGTH_LONG).show()
        }
    }

    private fun openGallery() {
        selectPhotosLauncher.launch("image/*")
    }

    inner class PhotosAdapter : BaseAdapter() {
        override fun getCount(): Int = photosList.size

        override fun getItem(position: Int): Uri = photosList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView = convertView as? ImageView ?: ImageView(requireContext()).apply {
                layoutParams = AbsListView.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            imageView.setImageURI(getItem(position))
            return imageView
        }
    }


    private fun convertBitmapToBase64(bitmap: Bitmap?): String? {
        bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        fun newInstance(tripUuid: UUID): add_location {
            return add_location(tripUuid)
        }
    }
}
