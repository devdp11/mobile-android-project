package com.example.android_studio_project.fragment.trip.edit_trip

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripModelEdit
import com.example.android_studio_project.data.retrofit.models.UserTripModel
import com.example.android_studio_project.data.retrofit.services.TripService
import com.example.android_studio_project.data.retrofit.services.UserService
import com.example.android_studio_project.data.room.ent.User
import com.example.android_studio_project.fragment.location.add_location.add_location
import com.example.android_studio_project.fragment.location.edit_location.edit_location
import com.example.android_studio_project.fragment.location.list_location.list_location_adapter
import com.example.android_studio_project.fragment.trip.list_user.list_user_adapter
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.ColorDrawable
import android.graphics.Color


class edit_trip(private val tripUuid: UUID, private val userUUID: String?) : Fragment() {
    private lateinit var tripService: TripService
    private lateinit var userService: UserService
    private lateinit var listLocationAdapter: list_location_adapter
    private lateinit var listUserAdapter: list_user_adapter

    private lateinit var tripNameEditText: TextView
    private lateinit var tripDescriptionEditText: TextView
    private lateinit var tripDateEditText: TextView
    private lateinit var tripRatingBar: RatingBar
    private lateinit var saveTripButton: Button
    private lateinit var changeRecyclerViewButton: Button
    private lateinit var recyclerView: RecyclerView

    private var isLocationView = true

    private var tripStartDate: String? = null
    private var tripEndDate: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_trip, container, false)

        tripNameEditText = view.findViewById(R.id.trip_name)
        tripDescriptionEditText = view.findViewById(R.id.trip_description)
        tripDateEditText = view.findViewById(R.id.trip_date)
        tripRatingBar = view.findViewById(R.id.trip_rating)
        saveTripButton = view.findViewById(R.id.save_trip_button)
        changeRecyclerViewButton = view.findViewById(R.id.change_recycler_view)
        recyclerView = view.findViewById(R.id.recycler_view)

        saveTripButton.setOnClickListener {
            saveTrip()
        }
        tripDateEditText.setOnClickListener {
            showDatePicker()
        }

        val deleteButton: Button = view.findViewById(R.id.delete_btn)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(tripUuid)
        }

        val locationButton: Button = view.findViewById(R.id.add_location_btn)
        locationButton.setOnClickListener {
            openAddLocationFragment(tripUuid)
        }

        listLocationAdapter = list_location_adapter(requireContext(), emptyList()) { clickedLocation ->
            openEditLocationFragment(clickedLocation.uuid, tripUuid)
        }
        listUserAdapter = list_user_adapter(emptyList()) {
            showAddUserDialog()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = listLocationAdapter

        changeRecyclerViewButton.setOnClickListener {
            toggleRecyclerView()
        }

        tripService = TripService(requireContext())
        userService = UserService(requireContext())
        getLocations()
        getUsers()

        val backButton: ImageView = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        tripService.getTripById(tripUuid,
            onResponse = { tripDetails ->
                tripDetails?.let {
                    tripNameEditText.text = tripDetails.name ?: ""
                    tripDescriptionEditText.text = tripDetails.description ?: ""

                    val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                    tripDetails.startDate?.let { startDate ->
                        tripStartDate = isoDateFormat.format(startDate)
                    }
                    tripDetails.endDate?.let { endDate ->
                        tripEndDate = isoDateFormat.format(endDate)
                    }

                    tripStartDate?.let { startDateString ->
                        tripEndDate?.let { endDateString ->
                            val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            val startDateDisplay = displayFormat.format(isoDateFormat.parse(startDateString))
                            val endDateDisplay = displayFormat.format(isoDateFormat.parse(endDateString))
                            tripDateEditText.text = "$startDateDisplay - $endDateDisplay"
                        }
                    }
                    tripDetails.rating?.let { rating ->
                        tripRatingBar.rating = rating
                    }
                }
            },
            onFailure = {
                Toast.makeText(context, getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )
        return view
    }

    private fun toggleRecyclerView() {
        if (isLocationView) {
            recyclerView.adapter = listUserAdapter
            changeRecyclerViewButton.text = getString(R.string.toogle_locations)
            getUsers()
        } else {
            recyclerView.adapter = listLocationAdapter
            getLocations()
            changeRecyclerViewButton.text = getString(R.string.toogle_users)
        }
        isLocationView = !isLocationView
    }

    private fun openEditLocationFragment(locationUuid: UUID, tripUuid: UUID) {
        tripStartDate?.let { startDate ->
            tripEndDate?.let { endDate ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, edit_location.newInstance(locationUuid, tripUuid, startDate, endDate))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun getLocations() {
        tripService.getTripLocations(tripUuid,
            onResponse = { locations ->
                if (locations != null) {
                    listLocationAdapter.setData(locations)
                }
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun getUsers() {
        tripService.getTripUser(tripUuid,
            onResponse = { users ->
                listUserAdapter.setData(users)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), getString(R.string.load_error), Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.succe))
        builder.setMessage(getString(R.string.save_succe))
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteConfirmationDialog(uuid: UUID) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_title))
        builder.setMessage(getString(R.string.delete_description))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            tripService.deleteTrip(userUUID, uuid,
                onResponse = {
                    Toast.makeText(requireContext(), getString(R.string.save_succe), Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), getString(R.string.save_error), Toast.LENGTH_SHORT).show()
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

    private fun openAddLocationFragment(tripUuid: UUID) {
        tripStartDate?.let { startDate ->
            tripEndDate?.let { endDate ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, add_location.newInstance(tripUuid, startDate, endDate))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun saveTrip() {
        val tripName = tripNameEditText.text.toString()
        val tripDescription = tripDescriptionEditText.text.toString()
        val tripRating = tripRatingBar.rating

        if (tripName.isNotEmpty() && tripStartDate != null && tripEndDate != null) {
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val formattedStartDate = isoDateFormat.format(isoDateFormat.parse(tripStartDate!!))
            val formattedEndDate = isoDateFormat.format(isoDateFormat.parse(tripEndDate!!))

            val trip = TripModelEdit(
                name = tripName,
                description = tripDescription,
                startDate = formattedStartDate,
                endDate = formattedEndDate,
                rating = tripRating
            )

            tripService.updateTrip(tripUuid, trip, onResponse = { responseMessage, tripUUID ->
                requireActivity().runOnUiThread {
                    if (responseMessage == "success") {
                        showConfirmationDialog()
                    } else {
                        Toast.makeText(context, getString(R.string.save_error), Toast.LENGTH_LONG).show()
                    }
                }
            }, onFailure = { throwable ->
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_date))
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            tripStartDate = isoDateFormat.format(Date(selection.first ?: 0))
            tripEndDate = isoDateFormat.format(Date(selection.second ?: 0))

            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDisplay = displayFormat.format(Date(selection.first ?: 0))
            val endDisplay = displayFormat.format(Date(selection.second ?: 0))
            val dateRange = "$startDisplay - $endDisplay"
            tripDateEditText.setText(dateRange)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun showAddUserDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.popup_add_user, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextUserEmail)
        val addButton = dialogLayout.findViewById<Button>(R.id.btnAddUser)

        val dialog = builder.setView(dialogLayout).create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        addButton.setOnClickListener {
            val email = editText.text.toString()
            if (email.isNotEmpty()) {
                getAddUser(email)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.invite_user), Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun getAddUser(email: String) {
        userService.findUser(email,
            onResponse = { userId ->
                val userTrip = UserTripModel(userId = userId.toString(), tripId = tripUuid.toString())
                createUserTrip(userTrip)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun createUserTrip(userTrip: UserTripModel) {
        tripService.createUserTrip(userTrip,
            onResponse = {
                Toast.makeText(requireContext(), getString(R.string.invite_user_succe), Toast.LENGTH_SHORT).show()
                getUsers()
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), getString(R.string.invite_user_error), Toast.LENGTH_SHORT).show()
            }
        )
    }


    companion object {
        fun newInstance(tripUuid: UUID, userUUID: String?): edit_trip {
            return edit_trip(tripUuid, userUUID)
        }
    }
}
