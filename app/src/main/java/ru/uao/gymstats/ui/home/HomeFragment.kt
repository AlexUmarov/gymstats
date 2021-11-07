package ru.uao.gymstats.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.WorkSource
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.uao.gymstats.R
import ru.uao.gymstats.databinding.FragmentHomeBinding
import android.widget.CalendarView.OnDateChangeListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.uao.gymstats.MainActivity
import ru.uao.gymstats.ui.gallery.GalleryFragment
import java.time.LocalDate
import java.time.LocalDate.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.Instant
import java.time.ZoneId
import android.widget.ArrayAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.uao.gymstats.ui.home.data.Workout


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    //private var fireBase = FirebaseFirestore.getInstance()
    private var fireBase = Firebase.firestore
    private var selectedWorkoputType: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDateWorkoput: LocalDate = now()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        FirebaseApp.initializeApp(root.context)
        auth = FirebaseAuth.getInstance()

        val textView: TextView = binding.selectedDate
        val calendarWorkout: CalendarView = binding.calendarWorkout
        val btnAddWorkout: Button = binding.btnAddWorkout
        val workoutListType: Spinner = binding.workoutListType
        homeViewModel.text.observe(viewLifecycleOwner,  {
            textView.text = it
        })
        homeViewModel.maxDate.observe(viewLifecycleOwner, {
            calendarWorkout.maxDate = it
            textView.text = "Workout at: " + Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
        })

        calendarWorkout.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val dateTime = of(year, month, dayOfMonth)
            textView.text = "Workout at: $dateTime"
            btnAddWorkout.isActivated = true
            selectedDateWorkoput = dateTime
        }
        btnAddWorkout.setOnClickListener{
            addWorkoutInfo(root.context);
            //val activity = requireView().context as AppCompatActivity
            //activity.supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, GalleryFragment()).addToBackStack(null).commit()
        }

        val adapter = ArrayAdapter.createFromResource(root.context, R.array.WorkoutTypeList, android.R.layout.simple_spinner_item)
        workoutListType.adapter = adapter
        workoutListType?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //..
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.getItemAtPosition(position).toString()
                selectedWorkoputType = item
                Toast.makeText(root.context, "Selected $item", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addWorkoutInfo(context: Context) {
        if(!selectedWorkoputType.isEmpty()){
            var workoutDate = "${selectedDateWorkoput.dayOfMonth}-${selectedDateWorkoput.month}-${selectedDateWorkoput.year}"
            fireBase.collection(auth.currentUser?.uid.toString())
                .document(workoutDate)
                .set(Workout(selectedWorkoputType, workoutDate, "some data workout"))
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        context, "Add workout date: $workoutDate",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context, "Error adding document: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}