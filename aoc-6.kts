// https://adventofcode.com/2021/day/6

import java.io.File

fun input(): List<Int> = File("input/aoc-6.txt").readLines().single().split(',').map { it.toInt() }

// Part 1.

data class Fish(val d: Int, val t: Int)

fun p1(seed: List<Int>): Int {
    val cache = mutableMapOf<Fish, Int>()

    fun gen(f: Fish): Int {
        cache[f]?.let { return it }

        return when {
            f.t == 0 -> 1
            f.d == 0 -> gen(Fish(6, f.t - 1)) + gen(Fish(8, f.t - 1))
            else -> gen(Fish(f.d - 1, f.t - 1))
        }.also { cache[f] = it }
    }

    return seed.sumBy { d -> gen(Fish(d, 80)) }
}

// Part 2.

fun p2(seed: List<Int>): Long {
    val cache = mutableMapOf<Fish, Long>()

    fun gen(f: Fish): Long {
        cache[f]?.let { return it }

        return when {
            f.t == 0 -> 1L
            f.d == 0 -> gen(Fish(6, f.t - 1)) + gen(Fish(8, f.t - 1))
            f.d < f.t -> gen(Fish(0, f.t - f.d))
            else -> 1L
        }.also { cache[f] = it }
    }

    return seed.map { d -> gen(Fish(d, 256)) }.sum()
}

with(input()) {
    println(p1(this))
    println(p2(this))
}
