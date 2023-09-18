package me.qingshu.latin.model

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.qingshu.latin.R
import me.qingshu.latin.data.PlantType
import me.qingshu.latin.database.Gold
import me.qingshu.latin.database.Plant
import me.qingshu.latin.database.Problem
import me.qingshu.latin.database.UseCase
import me.qingshu.latin.extensions.chineseType
import me.qingshu.latin.extensions.chineseWithFamily
import me.qingshu.latin.extensions.edit
import me.qingshu.latin.extensions.familyType
import me.qingshu.latin.extensions.latinType
import me.qingshu.latin.extensions.latinWithFamily
import me.qingshu.latin.extensions.preProcess
import me.qingshu.latin.extensions.sharedPreferences
import me.qingshu.latin.extensions.toGold
import me.qingshu.latin.extensions.toLatinDefinition
import me.qingshu.latin.extensions.toProblem
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {

    private val plants = useCase.queryPlants()

    private val _message = Channel<Int>(capacity = Channel.CONFLATED)
    val message = _message.receiveAsFlow()

    private fun message(resId: Int) {
        viewModelScope.launch {
            _message.trySend(resId)
        }
    }

    private val databaseCreateStatus = MutableStateFlow(false)

    fun load(context: Context) {
        viewModelScope.launch { databaseCreateStatus.emit(false) }

        val process = arrayListOf<(String) -> Plant>(
            { it.toLatinDefinition(PlantType.TREE) },
            { it.toLatinDefinition(PlantType.FLOWER) }
        )
        arrayOf(
            R.raw.excel_150definition,
            R.raw.excel_200definition
        ).forEachIndexed { index, res ->
            context.resources.openRawResource(res)
                .bufferedReader()
                .use { it.readText() }
                .preProcess("\n")
                .map { process[index].invoke(it) }
                .insertPlants()
        }

        context.resources.openRawResource(R.raw.gold)
            .bufferedReader()
            .use { it.readText() }
            .preProcess("\n")
            .map { it.toGold() }
            .insertGolds()

        context.sharedPreferences().edit {
            putBoolean(Plant.CREATE, true)
        }

        val plantTypes = arrayListOf(PlantType.TREE, PlantType.FLOWER)
        arrayListOf(R.raw.quiz1_7_25, R.raw.quiz2_7_25).forEachIndexed { index, id ->
            context.resources.openRawResource(id)
                .bufferedReader()
                .use { it.readText() }
                .preProcess("\n")
                .map { it.toProblem(plantTypes[index]) }
                .insertProblems()
        }
        viewModelScope.launch { databaseCreateStatus.emit(true) }
    }

    private fun List<Problem>.insertProblems() {
        viewModelScope.launch {
            useCase.insertProblems(this@insertProblems)
        }
    }

    private fun List<Plant>.insertPlants() {
        viewModelScope.launch {
            useCase.insertPlants(this@insertPlants)
        }
    }

    private fun List<Gold>.insertGolds() {
        viewModelScope.launch {
            useCase.insertGolds(this@insertGolds)
        }
    }

    fun export(uri: Uri, context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val raw = arrayListOf(R.raw.excel_200definition, R.raw.excel_150definition)
        val names = arrayListOf(R.string.setting_export_file_name_flower, R.string.setting_export_file_name_tree)
        DocumentFile.fromTreeUri(context, uri)?.also { tree ->
            raw.forEachIndexed { index, i ->
                tree.copyFile(i, names[index], context)
            }
            message(R.string.message_export_completed)
        }
    }

    private fun DocumentFile.copyFile(rawRes: Int, displayName: Int, context: Context) {
        createFile(
            context.getString(R.string.csv_mimetype),
            context.getString(displayName, System.currentTimeMillis().toString())
        )?.also { file ->
            context.contentResolver.openOutputStream(file.uri)?.use { os ->
                context.resources.openRawResource(rawRes).use { fis ->
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (fis.read(buffer).also { len = it } != -1) {
                        os.write(buffer, 0, len)
                    }
                }
            }
        }
    }

    fun exportCompletion(uri: Uri, context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val random = Random.Default
        plants.first().toMutableList().map {
            when (random.nextInt(5)) {
                0 -> it.chineseType()
                1 -> it.latinType(random)
                2 -> it.familyType(random)
                3 -> it.chineseWithFamily(random)
                else -> it.latinWithFamily(random)
            }
        }.mapIndexed { index, completion ->
            "${index + 1},${
                completion.detail.replace("\n", "").replace("\r", "")
            },${completion.answer.replace("\n", "").replace("\r", "")}"
        }.joinToString(prefix = "id,detail,answer\n", separator = "\n").also { str ->
            DocumentFile.fromTreeUri(context, uri)
                ?.createFile(
                    context.getString(R.string.csv_mimetype),
                    context.getString(
                        R.string.setting_export_file_name_completion,
                        System.currentTimeMillis().toString()
                    )
                )?.also { targetFile ->
                    context.contentResolver.openOutputStream(targetFile.uri)?.use { os ->
                        os.bufferedWriter().use {
                            it.write(str)
                        }.also {
                            message(R.string.message_export_completion_completed)
                        }
                    }
                }
        }
    }

}