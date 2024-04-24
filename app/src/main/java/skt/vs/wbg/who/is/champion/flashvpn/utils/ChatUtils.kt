package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp

object ChatUtils {
    fun initChart(chart: LineChart) {
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setDrawGridBackground(false)
        chart.setPinchZoom(false)
        chart.setBackgroundColor(Color.WHITE)
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return value.toInt().toString() + "s"
            }
        }
        val yAxis = chart.axisLeft
        yAxis.textColor = Color.WHITE
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawZeroLine(false)
        yAxis.axisMinimum = 0f
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.legend.isEnabled = false
        chart.setNoDataText("")
    }

    private fun CharSequence.extractNumberFromString(): Float? {
        val regex = Regex("""(\d+(\.\d+)?)""")
        val matchResult = regex.find(this)
        return matchResult?.value?.toFloatOrNull()
    }
    fun CharSequence.convertToKbps(): Float? {
        val rateRegex = """(\d+(\.\d+)?)\s*([kMG]?bit/s)""".toRegex()
        val matchResult = rateRegex.find(this)

        matchResult?.let { result ->
            val value = result.groupValues[1].toFloat()
            val unit = result.groupValues[3]

            return when (unit) {
                "bit/s" -> value / 1000 // 1 kbit = 1000 bit
                "kbit/s" -> value // Already in kbit/s
                "Mbit/s" -> value * 1000 // 1 Mbit = 1000 kbit
                "Gbit/s" -> value * 1000000 // 1 Gbit = 1000000 kbit
                else -> null // Invalid unit
            }
        }

        return null // No match found
    }
    fun simulateDataUpdate(
        time: Float,
        chart: LineChart,
        uploadText: String,
        downloadText: String
    ) {
        val uploadSpeed =
            if (DataHelp.isConnectFun()) uploadText.convertToKbps()
                ?: 0f else 0f
        val downloadSpeed =
            if (DataHelp.isConnectFun()) downloadText.convertToKbps()
                ?: 0f else 0f
        updateChart(chart, time, uploadSpeed, downloadSpeed)
    }

    fun updateChart(
        chart: LineChart,
        time: Float,
        uploadSpeed: Float,
        downloadSpeed: Float
    ) {
        var data = chart.data
        if (data == null) {
            data = LineData()
            chart.data = data
        }
        var uploadSet = try {
            data.getDataSetByIndex(0)
        } catch (e: Exception) {
            null
        }
        var downloadSet = try {
            data.getDataSetByIndex(1)
        } catch (e: Exception) {
            null
        }
        if (uploadSet == null) {
            uploadSet = createSet(ColorTemplate.rgb("#59B520"))
            data.addDataSet(uploadSet)
        }
        if (downloadSet == null) {
            downloadSet = createSet(ColorTemplate.rgb("#E2504F"))
            data.addDataSet(downloadSet)
        }
        data.addEntry(Entry(time, uploadSpeed), 0)
        data.addEntry(Entry(time, downloadSpeed), 1)
        while (data.entryCount > 32) {
            data.removeEntry(0f, 0)
            data.removeEntry(0f, 1)
        }
        data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    fun createSet(color: Int): LineDataSet {
        val set = LineDataSet(null, null)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = color
        set.setCircleColor(color)
        set.lineWidth = 2f
        set.circleRadius = 0f
        set.fillAlpha = 65
        set.fillColor = color
        set.setDrawCircleHole(false)
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        return set
    }
}