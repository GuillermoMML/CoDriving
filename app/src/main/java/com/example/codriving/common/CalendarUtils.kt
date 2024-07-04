package com.example.codriving.common


//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Calendar
import java.util.Date

@Composable
fun PrecioTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Price per day",
    isError: Boolean = false,
) {
    val precioRegex = Regex("[0-9]+")

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { newValue ->
            if (precioRegex.matches(newValue)) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(
    state: DateRangePickerState,
    onReady: () -> Unit,
) {
    DateRangePicker(
        state,
        modifier = Modifier.fillMaxSize(),
        title = {
            Text(
                text = "Select date range to assign the chart", modifier = Modifier
                    .padding(16.dp)
            )
        },
        headline = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f)) {
                    (if (state.selectedStartDateMillis != null) state.selectedStartDateMillis?.let {
                        getFormattedDate(
                            it
                        )
                    } else "Start Date")?.let { Text(text = it) }
                }
                Box(Modifier.weight(1f)) {
                    (if (state.selectedEndDateMillis != null) state.selectedEndDateMillis?.let {
                        getFormattedDate(
                            it
                        )
                    } else "End Date")?.let { Text(text = it) }
                }
                IconButton(onClick = { onReady() }) {
                    Icon(Icons.Filled.Done,contentDescription = null)
                }
            }
        },
        showModeToggle = true,
        colors = DatePickerDefaults.colors(
            dayContentColor = Color.Black,
            containerColor = Color.Black,
            titleContentColor = Color.Black,
            headlineContentColor = Color.Black,
            weekdayContentColor = Color.Black,
            subheadContentColor = Color.Black,
            yearContentColor = Color.Green,
            currentYearContentColor = Color.Red,
            selectedYearContainerColor = Color.Red,
            disabledDayContentColor = Color.Black,
            todayDateBorderColor = Color.Blue,
            dayInSelectionRangeContainerColor = Color.LightGray,
            dayInSelectionRangeContentColor = Color.White,
            selectedDayContainerColor = Color.Black
        )
    )
}

fun getFormattedDate(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(calender.timeInMillis)
}

fun getFormattedDateNoYear(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("dd/MM")
    return dateFormat.format(calender.timeInMillis)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun contentModalSheet(bottomSheetState: ModalBottomSheetState,onConfirmation: (Date,Date) -> Unit,content: @Composable () -> Unit,) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = Instant.now().toEpochMilli(),
        initialSelectedEndDateMillis = OffsetDateTime.now().plusDays(8).toInstant().toEpochMilli(),
        yearRange = IntRange(2023,2024), // available years
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis > System.currentTimeMillis()
            }
        }
    )
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {

                DateRangePickerSample(state){
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                    val startDate = Date(state.selectedStartDateMillis ?: Instant.now().toEpochMilli())
                    val endDate = state.selectedEndDateMillis?.let { Date(it) } ?: startDate
                    onConfirmation(startDate, endDate)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }


                        val startDate = Date(state.selectedStartDateMillis ?: Instant.now().toEpochMilli())
                        val endDate = state.selectedEndDateMillis?.let { Date(it) } ?: startDate
                        onConfirmation(startDate, endDate)


                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp)
                ) {
                    Text("Done", color = Color.White)
                }
            }

        }
    ){
        content()
    }

}

