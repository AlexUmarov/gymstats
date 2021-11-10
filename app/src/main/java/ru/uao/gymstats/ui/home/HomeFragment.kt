package ru.uao.gymstats.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malkinfo.editingrecyclerview.view.WorkoutAdapter
import ru.uao.gymstats.R
import ru.uao.gymstats.databinding.FragmentHomeBinding
import ru.uao.gymstats.ui.home.data.Workout
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.LocalDateTime
import java.time.Month


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var addsWorkoutBtn: FloatingActionButton
    private lateinit var arrow: ImageButton
    private lateinit var hiddenView: LinearLayout
    private lateinit var cardView: CardView

    private lateinit var workoutList: ArrayList<Workout>
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var recv: RecyclerView
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

        arrow = binding.arrowButton
        hiddenView = binding.hiddenView
        cardView = binding.baseCardview

        workoutList = ArrayList()
        recv = binding.mRecycler
        workoutAdapter = WorkoutAdapter(root.context, workoutList)
        recv.layoutManager = LinearLayoutManager(root.context)
        recv.adapter = workoutAdapter
        selectedWorkoutDateString = "${selectedDateWorkout.dayOfMonth}-${selectedDateWorkout.month}-${selectedDateWorkout.year}"
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

        arrow.setOnClickListener{
            if (hiddenView.visibility == View.VISIBLE) {

                // The transition of the hiddenView is carried out
                //  by the TransitionManager class.
                // Here we use an object of the AutoTransition
                // Class to create a default transition.
                TransitionManager.beginDelayedTransition(
                    cardView,
                    AutoTransition()
                )
                hiddenView.visibility = View.GONE
                arrow.setImageResource(R.drawable.common_full_open_on_phone)
            } else {
                TransitionManager.beginDelayedTransition(
                    cardView,
                    AutoTransition()
                )
                hiddenView.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.common_full_open_on_phone)
            }
        }
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

        /*val adapter = ArrayAdapter.createFromResource(
            root.context,
            R.array.WorkoutTypeList,
            android.R.layout.simple_spinner_item
        )*/
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveWorkoutInfo(context: Context, workout: Workout) {
        fireBase.collection(auth.currentUser?.uid.toString())
            .document(selectedWorkoutDateString)
            .set(workout)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    context, "Add workout date: $selectedWorkoutDateString",
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

    private fun getWorkoutInfo(selectedWorkoutDateString: String, context: Context) {
        fireBase.collection(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                workoutList.clear()
                for (document in result) {
                    if (selectedWorkoutDateString == document.id) {
                        val w = Workout(selectedWorkoutDateString, 1F, document.data["workoutInfo"].toString(),
                            document.data["count"].toString().toInt(), document.data["weight"].toString().toFloat())
                        workoutList.add(w)
                    }
                }
                workoutAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addWorkout(context: Context) {
        val inflter = LayoutInflater.from(context)
        val v = inflter.inflate(R.layout.add_item, null)
        val workoutInfo = v.findViewById<EditText>(R.id.workoutInfo)
        val count = v.findViewById<EditText>(R.id.count)
        val weight = v.findViewById<EditText>(R.id.weight)

        val addDialog = AlertDialog.Builder(context)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            when {
                "" == workoutInfo.text.toString() -> {
                    Toast.makeText(context, "Set param workoutInfo", Toast.LENGTH_SHORT).show()
                }
                "" == count.text.toString() -> {
                    Toast.makeText(context, "Set param count", Toast.LENGTH_SHORT).show()
                }
                "" == weight.text.toString() -> {
                    Toast.makeText(context, "Set param weight", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val workoutInfo = workoutInfo.text.toString()
                    val count = count.text.toString().toInt()
                    val weight = weight.text.toString().toFloat()
                    val w = Workout(selectedWorkoutDateString, 1F, workoutInfo, count, weight)
                    workoutList.add(w)
                    workoutAdapter.notifyDataSetChanged()
                    Toast.makeText(context, "Adding User Information Success", Toast.LENGTH_SHORT)
                        .show()
                    saveWorkoutInfo(context, w);
                    dialog.dismiss()
                }
            }
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }
}