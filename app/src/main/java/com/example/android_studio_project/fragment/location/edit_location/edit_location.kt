package com.example.android_studio_project.fragment.location.edit_location

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.LocationModelCreate
import com.example.android_studio_project.data.retrofit.models.PhotoModel
import com.example.android_studio_project.data.retrofit.services.LocationService
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class edit_location(private val locationUuid: UUID, private val tripUuid: UUID) : Fragment(), OnMapReadyCallback {
    private lateinit var locationService: LocationService

    private lateinit var photosGridView: GridView
    private val photosList = mutableListOf<Bitmap>()
    private lateinit var photosAdapter: PhotosAdapter

    private lateinit var locationNameEditText: TextView
    private lateinit var locationDescriptionEditText: TextView
    private lateinit var locationTypeSpinner: Spinner
    private lateinit var locationRatingBar: RatingBar
    private lateinit var saveLocationButton: Button
    private lateinit var dateTextInput: TextView
    private val locationTypeMap = mutableMapOf<String, UUID>()

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedLatLng: LatLng? = null

    private var locationDate: String? = null
    private var displayDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_location, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationNameEditText = view.findViewById(R.id.location_name)
        locationDescriptionEditText = view.findViewById(R.id.location_description)
        locationTypeSpinner = view.findViewById(R.id.location_type)
        locationRatingBar = view.findViewById(R.id.location_rating)
        saveLocationButton = view.findViewById(R.id.save_btn)
        dateTextInput = view.findViewById(R.id.date_text_input)

        saveLocationButton.setOnClickListener {
            saveLocation()
        }

        dateTextInput.setOnClickListener {
            showDatePicker()
        }

        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(locationUuid)
        }

        val addPhotoButton: Button = view.findViewById(R.id.add_photos)
        addPhotoButton.setOnClickListener {
            openGallery()
        }

        val backButton: ImageView = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        photosGridView = view.findViewById(R.id.photos_grid)
        photosAdapter = PhotosAdapter()
        photosGridView.adapter = photosAdapter

        locationService = LocationService(requireContext())
        getTypes()

        locationService.getLocationById(locationUuid,
            onResponse = { locationDetails ->
                locationDetails?.let {
                    val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                    locationNameEditText.text = locationDetails.name ?: ""
                    locationDescriptionEditText.text = locationDetails.description ?: ""

                    locationDetails.date?.let { locationDate ->
                        val formattedDate = isoDateFormat.parse(locationDate)
                        this.locationDate = locationDate
                        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        this.displayDate = formattedDate?.let { it1 -> displayFormat.format(it1) }
                        dateTextInput.text = displayDate
                    }

                    locationDetails.rating?.let { rating ->
                        locationRatingBar.rating = rating
                    }

                    val latitude = locationDetails.latitude?.toDouble()
                    val longitude = locationDetails.longitude?.toDouble()

                    if (latitude != null && longitude != null) {
                        val locationLatLng = LatLng(latitude, longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
                        googleMap.addMarker(MarkerOptions().position(locationLatLng).title("Location"))
                        selectedLatLng = locationLatLng
                    }
                }
            },
            onFailure = {
                Toast.makeText(context, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )


        locationService.getPhotoByLocationId(locationUuid,
            onResponse = { photoDetails ->
                photoDetails?.let { photos ->
                    val bitmapList = mutableListOf<Bitmap>()
                    photos.forEach { photo ->
                        val bitmap = decodeBase64ToBitmap(photo.data)
                        bitmap?.let { decodedBitmap ->
                            bitmapList.add(decodedBitmap)
                        }
                    }
                    photosAdapter.setData(bitmapList)
                }
            },
            onFailure = {}
        )

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

    private fun decodeBase64ToBitmap(encodedString: String?): Bitmap? {
        encodedString?.let {
            val decodedBytes = Base64.decode(it, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
        return null
    }

    private fun showDeleteConfirmationDialog(uuid: UUID) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_title))
        builder.setMessage(getString(R.string.delete_description))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            locationService.deleteLocation(tripUuid, uuid,
                onResponse = {
                    Toast.makeText(requireContext(), getString(R.string.location_delete_succ), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), getString(R.string.location_delete_error), Toast.LENGTH_SHORT).show()
                    Log.e("DeleteLocation", "Error deleting location: $error")
                }
            )
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveLocation() {
        val locationName = locationNameEditText.text.toString()
        val locationDescription = locationDescriptionEditText.text.toString()
        val locationRating = locationRatingBar.rating
        val selectedTypeName = locationTypeSpinner.selectedItem as? String
        val selectedTypeUuid = locationTypeMap[selectedTypeName]

        val newLatitude = selectedLatLng?.latitude
        val newLongitude = selectedLatLng?.longitude

        if (locationName.isNotEmpty() && locationDescription.isNotEmpty()) {
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val formattedDate = locationDate ?: ""

            val updatedLocation = LocationModelCreate(
                name = locationName,
                description = locationDescription,
                date = formattedDate,
                rating = locationRating,
                uuid = locationUuid,
                latitude = newLatitude,
                longitude = newLongitude,
                typeId = selectedTypeUuid
            )

            locationService.updateLocation(locationUuid, updatedLocation, onResponse = { responseMessage, locationUUID ->
                requireActivity().runOnUiThread {
                    if (responseMessage == "success") {
                        uploadPhotosAndShowConfirmation()
                    } else {
                        Toast.makeText(context, getString(R.string.save_error), Toast.LENGTH_LONG).show()
                    }
                }
            }, onFailure = { throwable ->
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                    Log.e("EditLocationFragment", "Error updating location", throwable)
                }
            })
        } else {
            Toast.makeText(context, R.string.fill_fields, Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadPhotosAndShowConfirmation() {
        var photosProcessed = 0
        var photosFailed = 0
        val totalPhotos = photosList.size

        if (totalPhotos == 0) {
            showConfirmationDialog()
            return
        }

        for (i in totalPhotos - 1 downTo 0) {
            val view = photosGridView.getChildAt(i)
            if (view is ImageView) {
                val bitmap = captureScreenshot(view)
                val image = encodeBitmapToBase64(bitmap)
                val photoModel = PhotoModel(
                    uuid = UUID.randomUUID(),
                    data = image,
                    locationId = locationUuid.toString()
                )

                locationService.createPhoto(photoModel,
                    onResponse = {
                        photosProcessed++
                        if (photosProcessed + photosFailed == totalPhotos) {
                            if (photosFailed == 0) {
                                showConfirmationDialog()
                            } else {
                                showErrorDialog()
                            }
                        }
                    },
                    onFailure = { error ->
                        photosFailed++
                        Log.e("PhotoUpload", "Error adding photo: $error")
                        requireActivity().runOnUiThread {
                            photosList.removeAt(i)
                            photosAdapter.setData(photosList)
                        }
                        if (photosProcessed + photosFailed == totalPhotos) {
                            showErrorDialog()
                        }
                    })
            }
        }
    }

    private fun captureScreenshot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private val selectPhotosLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        uris.forEach { uri ->
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            photosList.add(bitmap)
        }
        photosAdapter.setData(photosList)
    }

    private fun openGallery() {
        selectPhotosLauncher.launch("image/*")
    }

    inner class PhotosAdapter : BaseAdapter() {
        private var bitmapList = mutableListOf<Bitmap>()

        fun setData(bitmaps: List<Bitmap>) {
            bitmapList.clear()
            bitmapList.addAll(bitmaps)
            notifyDataSetChanged()
        }

        override fun getCount(): Int = bitmapList.size

        override fun getItem(position: Int): Bitmap = bitmapList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView = convertView as? ImageView ?: ImageView(requireContext()).apply {
                layoutParams = AbsListView.LayoutParams(300, 300)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            imageView.setImageBitmap(getItem(position))
            return imageView
        }
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(getString(R.string.photo_error))
        builder.setPositiveButton("OK") { dialog, _ ->
            if (isAdded) {
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.succe))
        builder.setMessage(getString(R.string.save_succe))
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            if (isAdded) {
                parentFragmentManager.popBackStack()
            } else {
                Log.e("EditLocationFragment", "Fragment not associated with FragmentManager")
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getTypes() {
        locationService.getAllTypes(
            onResponse = { types ->
                if (types != null) {
                    val typeNames = types.map { it.name ?: "Unknown" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    locationTypeSpinner.adapter = adapter
                } else {
                    Log.d("edit_location", "No types received")
                }
            },
            onFailure = { error ->
                Log.e("edit_location", "Failed to get types", error)
            }
        )
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            locationDate = isoDateFormat.format(Date(selection ?: 0))

            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            displayDate = displayFormat.format(Date(selection ?: 0))
            dateTextInput.text = displayDate
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        fun newInstance(locationUuid: UUID, tripUuid: UUID): edit_location {
            return edit_location(locationUuid, tripUuid)
        }
    }
}
