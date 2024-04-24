package skt.vs.wbg.who.`is`.champion.flashvpn

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        main()
    }

    fun convertToKbps(input: String): Float? {
        val rateRegex = """(\d+(\.\d+)?)\s*([kMG]?bit/s)""".toRegex()
        val matchResult = rateRegex.find(input)

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

    fun main() {
        val rates = listOf("3.99bit/s", "4.23kbit/s", "3.22Mbit/s", "5.44Gbit/s")

        for (rate in rates) {
            val convertedRate = convertToKbps(rate)
            println("Input: $rate, Output: $convertedRate kbit/s")
        }
    }

}