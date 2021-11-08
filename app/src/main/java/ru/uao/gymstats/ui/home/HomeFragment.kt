package ru.uao.gymstats.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malkinfo.editingrecyclerview.view.WorkoutAdapter
import ru.uao.gymstats.R
import ru.uao.gymstats.databinding.FragmentHomeBinding
import ru.uao.gymstats.ui.home.data.Workout
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.Month
import java.time.ZoneId


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var addsWorkoutBtn: FloatingActionButton
    private lateinit var workoutList:ArrayList<Workout>
    private lateinit var workoutAdapter: WorkoutAdapter
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private var fireBase = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDateWorkout: LocalDate = now()
    private var selectedWorkoutDateString: String = ""
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        FirebaseApp.initializeApp(root.context)
        auth = FirebaseAuth.getInstance()

        //val textView: TextView = binding.selectedDate
        val calendarWorkout: CalendarView = binding.calendarWorkout
        //val btnAddWorkout: Button = binding.btnAddWorkout
        //val workoutListType: Spinner = binding.workoutListType
        addsWorkoutBtn = binding.addingWorkoutBtn
        /*homeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })*/
        /*homeViewModel.maxDate.observe(viewLifecycleOwner, {
            calendarWorkout.maxDate = it
            textView.text = "Workout at: " + Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                .toLocalDate().toString()
        })*/

        calendarWorkout.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val dateTime = of(year, Month.values()[month], dayOfMonth)
            /*textView.text = "Workout at: $dateTime"
            btnAddWorkout.isActivated = true*/
            selectedDateWorkout = dateTime
            selectedWorkoutDateString =
                "${selectedDateWorkout.dayOfMonth}-${selectedDateWorkout.month}-${selectedDateWorkout.year}"
            getWorkoutInfo(selectedWorkoutDateString, root.context)
        }
        /*btnAddWorkout.setOnClickListener {
            //addWorkoutInfo(root.context);
        }*/

        val adapter = ArrayAdapter.createFromResource(
            root.context,
            R.array.WorkoutTypeList,
            android.R.layout.simple_spinner_item
        )
        /*workoutListType.adapter = adapter
        workoutListType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //..
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getItemAtPosition(position).toString()
                //selectedWorkoputType = item
                Toast.makeText(root.context, "Selected $item", Toast.LENGTH_SHORT).show()
            }
        }*/

        addsWorkoutBtn.setOnClickListener { addWorkout(root.context) }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun addWorkoutInfo(context: Context) {
        if (!selectedWorkoputType.isEmpty()) {
            var workoutDate =
                "${selectedDateWorkout.dayOfMonth}-${selectedDateWorkout.month}-${selectedDateWorkout.year}"
            fireBase.collection(auth.currentUser?.uid.toString())
                .document(workoutDate)
                .set(Workout(workoutDate, selectedWorkoputType, , "some data workout", 1))
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
    }*/

    private fun getWorkoutInfo(selectedWorkoutDateString: String, context: Context) {
        fireBase.collection(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (selectedWorkoutDateString == document.id) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun addWorkout(context: Context) {
        val inflter = LayoutInflater.from(context)
        val v = inflter.inflate(R.layout.add_item, null)
        val workoutInfo = v.findViewById<EditText>(R.id.workoutInfo)
        val count = v.findViewById<EditText>(R.id.count)
        val weight = v.findViewById<EditText>(R.id.weight)

        val addDialog = AlertDialog.Builder(context)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val workoutInfo = workoutInfo.text.toString()
            val count = count.text.toString().toInt()
            val weight = weight.text.toString().toFloat()
            workoutList.add(Workout("01-01-2021",1F, workoutInfo,count, weight))
            workoutAdapter.notifyDataSetChanged()
            Toast.makeText(context, "Adding User Information Success", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }
}