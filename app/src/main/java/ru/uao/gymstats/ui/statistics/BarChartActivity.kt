package ru.uao.gymstats.ui.statistics
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import ru.uao.gymstats.R
import java.util.ArrayList


class BarChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_statistics)
        val chart = findViewById<BarChart>(R.id.barchart)


        val NoOfEmp = ArrayList<BarEntry>()
        NoOfEmp.add(BarEntry(945f, 0))
        NoOfEmp.add(BarEntry(1040f, 1))
        NoOfEmp.add(BarEntry(1133f, 2))
        NoOfEmp.add(BarEntry(1240f, 3))
        NoOfEmp.add(BarEntry(1369f, 4))
        NoOfEmp.add(BarEntry(1487f, 5))
        NoOfEmp.add(BarEntry(1501f, 6))
        NoOfEmp.add(BarEntry(1645f, 7))
        NoOfEmp.add(BarEntry(1578f, 8))
        NoOfEmp.add(BarEntry(1695f, 9))
        val year = ArrayList<String>()
        year.add("2008")
        year.add("2009")
        year.add("2010")
        year.add("2011")
        year.add("2012")
        year.add("2013")
        year.add("2014")
        year.add("2015")
        year.add("2016")
        year.add("2017")

        val bardataset = BarDataSet(NoOfEmp, "No Of Employee")
        chart.animateY(5000)
        val data = BarData(year, bardataset)
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
        chart.data = data
    }
}
