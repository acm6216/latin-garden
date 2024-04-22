package me.qingshu.latin.print

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.util.DisplayMetrics
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_MM
import me.qingshu.latin.data.Completion
import me.qingshu.latin.database.Plant
import me.qingshu.latin.extensions.chineseType
import me.qingshu.latin.extensions.chineseWithFamily
import me.qingshu.latin.extensions.familyType
import me.qingshu.latin.extensions.latinType
import me.qingshu.latin.extensions.latinWithFamily
import kotlin.random.Random

class Print(
    private val plants:List<Completion>,
    private val metrics: DisplayMetrics
) {

    private val random = Random.Default
    private var pageNumber:Int = 0
    private var completionIndex:Int = 0

    fun create(){
        pageNumber = 1
        completionIndex = 0
        /*val target = plants.map {
            when (random.nextInt(5)) {
                0 -> it.chineseType()
                1 -> it.latinType(random)
                2 -> it.familyType(random)
                3 -> it.chineseWithFamily(random)
                else -> it.latinWithFamily(random)
            }
        }*/
        val a4 = Paper.A4()
        val pdfDocument = PdfDocument()
        draw(pdfDocument,a4)
    }

    private fun draw(pdfDocument: PdfDocument,paper: Paper){
        var pageInfo = paper.newPage()
        var page = pdfDocument.startPage(pageInfo)
        val canDrawLines = paper.canDrawLines()
        var currentLines = 1
        while (true){
            if(currentLines>canDrawLines)
                pdfDocument.finishPage(page)
        }
    }

    private fun next():Completion? =
        if(completionIndex>plants.size-1) null
        else plants[completionIndex++]

    private fun Canvas.drawPdf(){
        drawColor(Color.parseColor("#ffffff"))
    }

    private fun Int.applyDimension(unit:Int):Float =
        when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> this.toFloat()
            TypedValue.COMPLEX_UNIT_DIP -> this * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> this * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> this * metrics.xdpi * (1.0f/72)
            TypedValue.COMPLEX_UNIT_IN -> this * metrics.xdpi
            COMPLEX_UNIT_MM -> this * metrics.xdpi * (1.0f/25.4f)
            else -> 0f
        }

    private fun Paper.newPage() = PdfDocument.PageInfo.Builder(
        widthPx().toInt(),
        heightPx().toInt(), pageNumber++
    ).create()

    private fun Paper.canDrawLines() = (heightPx()-padding()*2)/textSize()

    private fun Paper.textSize() = widthPx()/40F
    private fun Paper.padding() = widthPx()/20F

    private fun Paper.widthPx() = width.applyDimension(COMPLEX_UNIT_MM)
    private fun Paper.heightPx() = height.applyDimension(COMPLEX_UNIT_MM)
}