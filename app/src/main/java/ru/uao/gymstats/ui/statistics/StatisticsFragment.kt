package ru.uao.gymstats.ui.statistics

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.uao.gymstats.databinding.FragmentStatisticsBinding
import ru.uao.gymstats.ui.home.data.Workout


class StatisticsFragment : Fragment() {

    private lateinit var statisticsViewModel: StatisticsViewModel
    private var _binding: FragmentStatisticsBinding? = null

    private lateinit var auth: FirebaseAuth
    private var fireBase = Firebase.firestore
    private lateinit var workoutList: ArrayList<Workout>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        statisticsViewModel =
            ViewModelProvider(this).get(StatisticsViewModel::class.java)

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textStatistics
        statisticsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        //add chart
        auth = FirebaseAuth.getInstance()
        workoutList = ArrayList()
        getWorkoutInfo()

        return root
    }

    private fun getWorkoutInfo() {
        fireBase.collection(auth.currentUser?.email.toString())
            .orderBy("workoutDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val date = java.util.ArrayList<String>()
                val bodyWeightInfo = java.util.ArrayList<BarEntry>()
                for ((i, document) in result.withIndex()) {
                    date.add(document.id)
                    bodyWeightInfo.add(
                        BarEntry(document.data["bodyWeight"].toString().toFloat(),i)
                    )
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
                val barDataSet = BarDataSet(bodyWeightInfo, "Body Weight Info")
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
                //date.sortedBy { it. }
                showChart(BarData(date, barDataSet))
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun showChart(data: BarData) {
        var chart = binding.barchart
        chart.animateY(500)
        chart.data = data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
