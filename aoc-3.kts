// https://adventofcode.com/2021/day/3

import java.io.File

val inputs = File("input/aoc-3.txt").readLines()

// Part 1.

fun p1(inputs: List<String>): Int {
    val r = inputs[0].indices.map { c -> 
        val j = inputs.count { it[c] == '0' }
        if (2*j >= inputs.size) 0 else 1
    }

    val g = r.fold(0) { acc, x -> 2 * acc + x }
    val e = r.fold(0) { acc, x -> 2 * acc + 1 - x }
    return g*e
}

// Part 2.

fun p2(inputs: List<String>): Int {
    fun find(c: Int, l: List<String>, max: Boolean): List<String> {
        val x = l.count { it[c] == '1' }
        val k = when { 
            2*x >= l.size -> l.filter { it[c] == '1' }
            else -> l.filter { it[c] == '0' }
        }
        return if (max) k else l - k
    }

    fun exec(max: Boolean): Int {
        var l = inputs
        for (c in l[0].indices) {
            l = find(c, l, max)
            if (l.size == 1) break
        }
        return l.first().fold(0) { acc, x -> 2 * acc + (if (x == '0') 0 else 1) }
    }

    val oxy = exec(true)
    var co2 = exec(false)
    return oxy * co2
}

println(p2(inputs))
