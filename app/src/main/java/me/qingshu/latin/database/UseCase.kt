package me.qingshu.latin.database

import javax.inject.Inject

class UseCase @Inject constructor(
    private val plantDao: PlantDao
) {

    suspend fun insertPlants(plants:List<Plant>) = plantDao.insertPlants(plants)
    suspend fun insertPlant(plant:Plant) = plantDao.insertPlant(plant)

    fun queryPlants() = plantDao.queryPlants()

    suspend fun updatePlant(plant: Plant) = plantDao.updatePlant(plant)

    suspend fun deletePlants() = plantDao.deletePlants()

    suspend fun insertGold(gold: Gold) = plantDao.insertGold(gold)
    suspend fun insertGolds(golds: List<Gold>) = plantDao.insertGolds(golds)

    fun queryGolds() = plantDao.queryGolds()

    suspend fun deleteGolds() = plantDao.deleteGolds()

    fun queryProblems() = plantDao.queryProblems()
    suspend fun insertProblems(problems: List<Problem>) = plantDao.insertProblems(problems)
}