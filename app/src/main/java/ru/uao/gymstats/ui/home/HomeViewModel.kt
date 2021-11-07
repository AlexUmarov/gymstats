package ru.uao.gymstats.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Select date and add workout info"
    }
    val text: LiveData<String> = _text

    @RequiresApi(Build.VERSION_CODES.O)
    private val _maxDate = MutableLiveData<Long>().apply {
        value = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val maxDate: LiveData<Long> = _maxDate
}