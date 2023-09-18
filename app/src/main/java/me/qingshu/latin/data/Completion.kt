package me.qingshu.latin.data

data class Completion(
    val resId:String,
    val detail:String,
    val answer:String,
    val isTesting:Boolean = false,
    val useAnswer:String = ""
){
    companion object{
        fun empty() = Completion("","","",false)
    }
}