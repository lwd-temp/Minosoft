/*
 * Minosoft
 * Copyright (C) 2021 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.util

object UnitFormatter {
    private val BYTE_UNITS = arrayOf("B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB")
    private val UNITS = arrayOf("", "k", "M", "G", "T", "P", "E", "Z", "Y")
    private val TIME_UNITS = arrayOf("ns", "μs", "ms", "s", "m", "h", "d", "w", "M", "Y")

    fun Long.formatBytes(): String {
        if (this < 0) {
            return "Unknown"
        }
        return formatUnit(this, BYTE_UNITS, 1024L)
    }

    fun formatNumber(number: Int): String {
        return formatNumber(number.toLong())
    }

    fun formatNumber(number: Long): String {
        return formatUnit(number, UNITS, 1000L)
    }

    private fun formatUnit(number: Long, units: Array<String>, factor: Long): String {
        var lastFactor = 1L
        var currentFactor = factor
        for (unit in units) {
            if (number < currentFactor) {
                if (number < (lastFactor * 10)) {
                    return "${"%.1f".format(number / lastFactor.toFloat())}${unit}"
                }
                return "${number / lastFactor}${unit}"
            }
            lastFactor = currentFactor
            currentFactor *= factor
        }
        throw IllegalArgumentException()
    }

    fun formatNanos(nanos: Long): String {
        return "${nanos / 1000000}ms"

        // ToDo
    }

    fun formatMillis(millis: Long): String {
        return formatNanos(millis * 1000)
    }
}