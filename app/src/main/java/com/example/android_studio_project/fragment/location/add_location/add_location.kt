import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripLocationModel
import com.example.android_studio_project.data.retrofit.services.LocationService
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import android.util.Base64
import android.util.Log
import com.example.android_studio_project.data.retrofit.models.LocationModelCreate
import com.example.android_studio_project.data.retrofit.models.PhotoModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date
import java.util.TimeZone

class add_location(private val tripUuid: UUID) : Fragment() {

    private lateinit var photosGridView: GridView
    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter

    private lateinit var locationService: LocationService
    private lateinit var locationTypeSpinner: Spinner
    private val locationTypeMap = mutableMapOf<String, UUID>()

    private lateinit var uuidTextView: TextView
    private lateinit var dateTextInput: EditText

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)

        uuidTextView = view.findViewById(R.id.uuid_teste)
        uuidTextView.text = tripUuid.toString()

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

        return view
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

        val isoDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val selectedCalendar = isoDateFormat.parse(locationDateStr)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(selectedCalendar)

        if (selectedTypeUuid != null && locationName.isNotEmpty() && locationDescription.isNotEmpty() && locationDateStr.isNotEmpty()) {
            val location = LocationModelCreate(
                uuid = UUID.randomUUID(),
                name = locationName,
                description = locationDescription,
                typeId = selectedTypeUuid,
                rating = locationRating,
                latitude = null,
                longitude = null,
                date = formattedDate
            )

            locationService.createLocation(location,
                onResponse = { _, locationUuid ->
                    val tripLocation = locationUuid?.let { TripLocationModel(tripId = tripUuid, locationId = it) }
                    if (tripLocation != null) {
                        locationService.createTripLocation(tripLocation,
                            onResponse = { _ ->
                                photosList.forEach { uri ->
                                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                                    val image = convertBitmapToBase64(bitmap)
                                    if (image != null) {
                                        val photo = PhotoModel(uuid = UUID.randomUUID(), data = image, locationId = locationUuid.toString())
                                        locationService.createPhoto(photo,
                                            onResponse = {
                                                Toast.makeText(requireContext(), getString(R.string.location_add_succ), Toast.LENGTH_LONG).show()
                                                parentFragmentManager.popBackStack()
                                            },
                                            onFailure = {
                                                Toast.makeText(requireContext(), getString(R.string.location_add_error), Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                }
                                Toast.makeText(requireContext(), getString(R.string.location_add_succ), Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(requireContext(), getString(R.string.location_add_error), Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                    uuidTextView.text = locationUuid.toString()
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

    companion object {
        fun newInstance(tripUuid: UUID): add_location {
            return add_location(tripUuid)
        }
    }
}
