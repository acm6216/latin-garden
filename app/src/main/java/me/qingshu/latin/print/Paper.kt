package me.qingshu.latin.print

sealed class Paper{

    open val width:Int = 0
    open val height:Int = 0

    data class A4(
        override val width: Int = 210,
        override val height:Int = 297
    ):Paper()
    data class B4(
        override val width: Int = 257,
        override val height: Int = 264
    ):Paper()
    //class A3:Paper(width = , height = )
    //class B3:Paper(width = , height = )
}