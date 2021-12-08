// https://adventofcode.com/2021/day/8

import java.io.File
import kotlin.math.abs

data class Entry(val signal: List<String>, val output: List<String>)

fun input() = File("input/aoc-8.txt").readLines().map { line -> 
    val (l1, l2) = line.split(" | ")
    Entry(l1.split(' '), l2.split(' '))
}

// Part 1.

fun p1(entries: List<Entry>): Int {
    val unique = setOf(2, 4, 3, 7)
    val sum = entries.sumOf { entry -> 
        entry.output.count { it.length in unique }
    }
    return sum
}

// Part 2.

fun p2(entries: List<Entry>): Int {
    fun String.sort() = toCharArray().apply { sort() }.joinToString("")

    fun decoder(p: List<Set<Char>>): Map<String, Int> {
        val map = Array<Set<Char>>(10) { emptySet() }
        map[8] = "abcdefg".toSet()
        map[1] = p.first { it.size == 2 }
        map[4] = p.first { it.size == 4 }
        map[7] = p.first { it.size == 3 }
        map[3] = p.first { it.size == 5 && (it - map[1]).size == 3 }

        val bd = map[4] - map[1]
        map[2] = p.first { it.size == 5 && (it - map[1]).size == 4 && (bd - it).size == 1 }
        map[5] = p.first { it.size == 5 && (bd - it).size == 0 }

        val d = bd intersect map[2]
        map[0] = p.first { it.size == 6 && it == (map[8] - d) }
        map[6] = p.first { it.size == 6 && (map[1] - it).size == 1 }
        map[9] = p.first { it.size == 6 && (it - map[4]).size == 2 }

        return mutableMapOf<String, Int>().apply { map.forEachIndexed { i, s -> put(s.joinToString("").sort(), i) } }

    }

    return entries.sumOf { entry ->
        val patterns = (entry.signal + entry.output).map { it.toSet() }
        val decoder = decoder(patterns)
        val digits = entry.output.map { decoder[it.sort()]!! }
        digits.fold(0) { acc: Int, num -> 10 * acc + num }
    }
}

with(input()) {
    println(p1(this))
    println(p2(this))
}
