package me.qingshu.latin

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.qingshu.latin.data.PlayController
import me.qingshu.latin.databinding.ActivityMainBinding
import me.qingshu.latin.extensions.fadeToVisibilityUnsafe
import me.qingshu.latin.model.DatabaseViewModel
import me.qingshu.latin.model.MainViewModel
import me.qingshu.latin.model.QuizViewModel
import me.qingshu.latin.model.VoiceViewModel
import me.qingshu.latin.preference.Repository
import me.qingshu.latin.preference.Settings
import kotlin.coroutines.CoroutineContext

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : AppCompatActivity(),CoroutineScope, SharedPreferences.OnSharedPreferenceChangeListener {

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO

    private val viewModel: MainViewModel by viewModels()
    private val databaseViewModel: DatabaseViewModel by viewModels()
    private val voiceViewModel: VoiceViewModel by viewModels()
    private val quizViewModel:QuizViewModel by viewModels()

    private val settings = Settings(Repository(this))
    private val sound = Sound().create(
        playStatus = {
            Log.d(TAG, "load: playStatus = $it")
        },
        playPosition = {
            voiceViewModel.playPosition(it)
        }
    )

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        settings.initializeDefaultValues()
        settings.registerOnSharedPreferenceChangeListener(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.content.bottomNav.setupWithNavController(navController)
        binding.content.rail.setupWithNavController(navController)

        repeatWithViewLifecycle {
            launch {
                viewModel.message.collect{
                    it.showMessage()
                }
            }
            launch {
                databaseViewModel.message.collect{
                    it.showMessage()
                }
            }
            launch {
                viewModel.playController.collect{
                    it.use()
                }
            }
            launch {
                voiceViewModel.pause.collect{
                    sound.pause()
                }
            }
            launch {
                voiceViewModel.tryAutoPlay.collect{ item ->
                    if(item.auto) item.item?.also { sound.play(it.data.resId,it.data.type) }
                }
            }
            launch {
                voiceViewModel.play.collect{
                    sound.play(it.data.resId,it.data.type)
                }
            }
        }
        navController.addOnDestinationChangedListener{ _,destination,_ ->
            val gone = arrayOf(R.id.nav_learn,R.id.nav_quiz,R.id.nav_privacy_policy,R.id.nav_quiz_answer)
            if(resources.getBoolean(R.bool.show_rail))
                binding.content.bottomNav.fadeToVisibilityUnsafe(destination.id !in gone, gone = true)
        }
        settings.playbackSpeed().toFloat().also {
            sound.setPlaybackSpeed(it)
            viewModel.playbackSpeed(it)
        }
        settings.repeatMode().also {
            sound.setRepeatMode(it.toInt())
        }
        settings.autoPlay().also {
            voiceViewModel.isAutoPlay(it)
        }
        settings.quizCount().also {
            quizViewModel.quizCount(it)
        }
        viewModel.sortBy(settings.sortBy())

        launch(Dispatchers.IO) {
            settings.privacyPolicyStatus().also {
                if (!it) {
                    delay(50)
                    launch(Dispatchers.Main) {
                        navController.navigate(R.id.nav_privacy_policy)
                    }
                }
            }
        }
    }

    private fun PlayController.use(){
        voiceViewModel.playDataSource(this)
        sound.playGroup(resource)
    }

    override fun onDestroy() {
        sound.release()
        super.onDestroy()
    }

    private fun Int.showMessage(){
        launch(Dispatchers.Main) {
            Snackbar.make(binding.toolbar,this@showMessage,Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        settings.sortChanged(key){
            viewModel.sortBy(it)
        }
        settings.playbackSpeedChanged(key){
            sound.setPlaybackSpeed(it.toFloat())
            viewModel.playbackSpeed(it.toFloat())
        }
        settings.repeatModeChanged(key){
            sound.setRepeatMode(it.toInt())
        }
        settings.autoPlayChanged(key){
            voiceViewModel.isAutoPlay(it)
        }
        settings.quizCountChanged(key){
            quizViewModel.quizCount(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private inline fun AppCompatActivity.repeatWithViewLifecycle(
        minState: Lifecycle.State = Lifecycle.State.STARTED,
        crossinline block: suspend CoroutineScope.() -> Unit
    ) {
        if (minState == Lifecycle.State.INITIALIZED || minState == Lifecycle.State.DESTROYED) {
            throw IllegalArgumentException("minState must be between INITIALIZED and DESTROYED")
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(minState) {
                block()
            }
        }
    }
}