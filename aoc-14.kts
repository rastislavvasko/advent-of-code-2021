// https://adventofcode.com/2021/day/14

import java.io.File

fun input() = File("input/aoc-14.txt").readLines().let { lines ->
    val polymer = lines[0]
    val templates = lines.subList(2, lines.size).associate { line ->
        val (i, o) = line.split(" -> ")
        i to o[0]
    }
    polymer to templates
}

// Part 1.

fun p1(polymer: String, templates: Map<String, Char>): Int {
    fun step(p: String): String {
        var r = StringBuilder(p)
        for (i in p.lastIndex - 1 downTo 0) {
            templates[p.substring(i, i + 2)]?.let { o -> r.insert(i + 1, o) }
        }
        return r.toString()
    }

    val res = (1..10).fold(polymer) { p, _ -> step(p) }
    val freq = res.groupBy { it }
    return freq.maxOf { it.value.size } - freq.minOf { it.value.size }
}

// Part 2.

fun p2(polymer: String, templates: Map<String, Char>): Long {
    val cache = mutableMapOf<Pair<String, Int>, LongArray>()

    fun sum(arr1: LongArray, arr2: LongArray) = LongArray(26) { i -> arr1[i] + arr2[i] }

    fun rec(p: String, s: Int): LongArray {
        cache[p to s]?.let { return it }

        val c = templates[p]
        if (s == 0 || c == null) {
            return LongArray(26).apply { this[p[0] - 'A'] = 1 }.also { cache[p to s] = it }
        }

        val arr1 = rec("${p[0]}$c", s - 1)
        val arr2 = rec("$c${p[1]}", s - 1)
        return sum(arr1, arr2).also { cache[p to s] = it }
    }

    val initial = LongArray(26).apply { this[polymer.last() - 'A'] = 1 }
    val res = polymer.windowed(2).fold(initial) { arr, p -> sum(arr, rec(p, 40)) }
    val max = res.maxOf { it }
    val min = res.minOf { if (it != 0L) it else Long.MAX_VALUE }
    return max - min
}

with(input()) {
    val (polymer, templates) = this
    println(p1(polymer, templates)) // 3284.
    println(p2(polymer, templates)) // 4302675529689.
}
