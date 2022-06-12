package com.aliumujib.mkopatest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aliumujib.mkopatest.presentation.TimerEvent
import com.aliumujib.mkopatest.presentation.TimerState
import com.aliumujib.mkopatest.presentation.TimerViewModel
import com.aliumujib.mkopatest.presentation.rememberStateWithLifecycle
import com.aliumujib.mkopatest.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val vm = viewModel<TimerViewModel>()
    TimerScreen(viewModel = vm)
}

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val timerState: TimerState by rememberStateWithLifecycle(viewModel.uiState)
    viewModel.produceEvent(TimerEvent.Start)

    when (timerState) {
        TimerState.Loading -> {
            Column(
                Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        TimerState.RunOut -> {
            Text(
                text = stringResource(id = R.string.phone_locked_label),
                style = MaterialTheme.typography.h4
            )
        }
        is TimerState.Running -> {
            val state = (timerState as TimerState.Running)
            CountDownTimer(state.days, state.hours, state.minutes, state.seconds)
        }
        TimerState.Error -> {
            Text(
                text = stringResource(id = R.string.something_went_wrong),
                style = MaterialTheme.typography.h4
            )
        }
    }
}

@Composable
fun CountDownTimer(days: String, hours: String, minutes: String, seconds: String) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(id = R.string.unlock_count_down_label),
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CountDownSection(
                    sectionValue = days,
                    sectionName = stringResource(id = R.string.days)
                )
                ColonSeparator()
                CountDownSection(
                    sectionValue = hours,
                    sectionName = stringResource(id = R.string.hours)
                )
                ColonSeparator()
                CountDownSection(
                    sectionValue = minutes,
                    sectionName = stringResource(id = R.string.minutes)
                )
                ColonSeparator()
                CountDownSection(
                    sectionValue = seconds,
                    sectionName = stringResource(id = R.string.seconds)
                )
            }
        }
    }
}

@Composable
fun ColonSeparator(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = ":",
        style = MaterialTheme.typography.h2
    )
}

@Composable
fun CountDownSection(modifier: Modifier = Modifier, sectionValue: String, sectionName: String) {
    Column(modifier = modifier) {
        Text(text = sectionValue, style = MaterialTheme.typography.h2)
        Text(
            text = sectionName,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
