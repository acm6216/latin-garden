package me.qingshu.latin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Transaction
    @Insert
    suspend fun insertPlants(plants:List<Plant>)

    @Insert
    suspend fun insertPlant(plant:Plant)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Query("SELECT * FROM plants")
    fun queryPlants(): Flow<List<Plant>>

    @Transaction
    @Query("DELETE FROM plants")
    suspend fun deletePlants()

    @Query("DELETE FROM golds")
    suspend fun deleteGolds()

    @Transaction
    @Insert
    suspend fun insertGolds(golds: List<Gold>)

    @Insert
    suspend fun insertGold(gold: Gold)

    @Query("SELECT * FROM golds")
    fun queryGolds():Flow<List<Gold>>

    @Transaction
    @Insert
    suspend fun insertProblems(problems:List<Problem>)

    @Query("SELECT * FROM problems")
    fun queryProblems():Flow<List<Problem>>

    @Insert
    suspend fun insertProblem(problem: Problem)
}