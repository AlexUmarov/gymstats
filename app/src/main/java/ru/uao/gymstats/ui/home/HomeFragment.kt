package ru.uao.gymstats.ui.home

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import ru.uao.gymstats.R
import ru.uao.gymstats.databinding.FragmentHomeBinding
import ru.uao.gymstats.ui.home.data.UserInfo
import ru.uao.gymstats.ui.home.data.Workout
import ru.uao.gymstats.ui.home.view.WorkoutAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter





class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var addsWorkoutBtn: FloatingActionButton
    private lateinit var save_data: Button
    private lateinit var arrow: ImageButton
    private lateinit var hiddenView: LinearLayout
    private lateinit var cardView: CardView

    private lateinit var workoutList: ArrayList<Workout>
    private lateinit var userInfo: UserInfo
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var recv: RecyclerView
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private var fireBase = Firebase.firestore
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
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
        userInfo = UserInfo(Date(), 0F)
        recv = binding.mRecycler
        workoutAdapter = WorkoutAdapter(root.context, workoutList)
        recv.layoutManager = LinearLayoutManager(root.context)
        recv.adapter = workoutAdapter
        val calendarWorkout: CalendarView = binding.calendarWorkout
        addsWorkoutBtn = binding.addingWorkoutBtn
        save_data = binding.saveData

        arrow.setOnClickListener{
            hideKeyboard()
            hideShowCalendar()
        }
        calendarWorkout.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val string = "${dayOfMonth}-${Month.values()[month].value}-${year}"
            val format: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val date: Date = format.parse(string)
            userInfo.workoutDate = date
            hideShowCalendar()
            getWorkoutInfo(userInfo)
        }

        addsWorkoutBtn.setOnClickListener { addWorkout(root.context) }
        save_data.setOnClickListener { saveWorkoutInfo(root.context, workoutList) }
        return root
    }

    private fun hideShowCalendar(){
        if (hiddenView.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(
                cardView,
                AutoTransition()
            )
            hiddenView.visibility = View.GONE
            arrow.setImageResource(android.R.drawable.ic_menu_month)
        } else {
            TransitionManager.beginDelayedTransition(
                cardView,
                AutoTransition()
            )
            hiddenView.visibility = View.VISIBLE
            arrow.setImageResource(android.R.drawable.ic_menu_month)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun toSimpleString(date: Date?) = with(date ?: Date()) {
        SimpleDateFormat("dd-MM-yyyy").format(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveWorkoutInfo(context: Context, workoutList: ArrayList<Workout>) {
        hideKeyboard()
        if(binding.bodyWeight.text.toString() != "")
            userInfo.bodyWeight = binding.bodyWeight.text.toString().toFloat()

        fireBase.collection(auth.currentUser?.email.toString())
            .document(toSimpleString(userInfo.workoutDate))
            .set(userInfo)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    context, "Add bodyWeight: ${userInfo.bodyWeight}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        workoutList.forEach {
            fireBase.collection(auth.currentUser?.email.toString())
                .document(toSimpleString(userInfo.workoutDate))
                .collection("current")
                .document(it.workoutInfo)
                .set(it)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        context, "Add workout: ${it.workoutInfo}",
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWorkoutInfo(userInfo: UserInfo) {
        var dateString = toSimpleString(userInfo.workoutDate)
        fireBase.collection(auth.currentUser?.email.toString())
            .document(dateString)
            .get()
            .addOnSuccessListener { result ->

                if(result != null && result.data != null) {
                    userInfo.bodyWeight = result.data!!["bodyWeight"].toString().toFloat()
                    binding.bodyWeight.setText(userInfo.bodyWeight.toString())
                }else{
                    binding.bodyWeight.setText("")
                }

            }
        fireBase.collection(auth.currentUser?.email.toString())
            .document(toSimpleString(userInfo.workoutDate))
            .collection("current")
            .get()
            .addOnSuccessListener { result ->
                workoutList.clear()
                for (document in result) {
                    val w = Workout(document.data["workoutInfo"].toString(), document.data["numberOfRepeat"].toString().toInt(),
                        document.data["count"].toString().toInt(), document.data["weight"].toString().toFloat())
                    workoutList.add(w)
                }
                workoutAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addWorkout(context: Context) {
        hideKeyboard()
        val inflter = LayoutInflater.from(context)
        val v = inflter.inflate(R.layout.add_item, null)
        val workoutInfo = v.findViewById<EditText>(R.id.workoutInfo)
        val numberOfRepeat = v.findViewById<EditText>(R.id.numberOfRepeat)
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
                    val numberOfRepeat = numberOfRepeat.text.toString().toInt()
                    val count = count.text.toString().toInt()
                    val weight = weight.text.toString().toFloat()
                    val w = Workout(workoutInfo, numberOfRepeat, count, weight)
                    workoutList.add(w)
                    workoutAdapter.notifyDataSetChanged()
                    Toast.makeText(context, "Adding User Information Success", Toast.LENGTH_SHORT)
                        .show()
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

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}