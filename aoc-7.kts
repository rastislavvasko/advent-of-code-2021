// https://adventofcode.com/2021/day/7

import java.io.File
import kotlin.math.abs

fun input(): List<Int> = File("input/aoc-7.txt").readLines().single().split(',').map { it.toInt() }

// Part 1.

fun p1(pos: List<Int>): Int {
    fun count(d: Int) = pos.sumOf { abs(it - d) }

    var min = pos.minOf { it }
    var max = pos.maxOf { it }
    return (min..max).minOf { d -> count(d) }
}

// Part 2.

fun p2(pos: List<Int>): Int {
    fun count(d: Int) = pos.sumOf {
        val x = abs(it - d)
        (x + 1) * x / 2
    }

    var min = pos.minOf { it }
    var max = pos.maxOf { it }
    return (min..max).minOf { d -> count(d) }
}

with(input()) {
    println(p1(this))
    println(p2(this))
}
