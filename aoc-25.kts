// https://adventofcode.com/2021/day/25

import java.io.File

fun input() = File("input/aoc-25.txt").readLines()

data class Cucumber(val east: Boolean, val x: Int, val y: Int)

data class Map(val m: Int, val n: Int, val s: String) {
    val cucumbers = s.mapIndexedNotNull { i, c ->
        if (c == '>' || c == 'v') Cucumber(c == '>', i % n, i / n) else null
    }

    override fun toString() = s.chunked(n).joinToString("\n")
}

fun process(lines: List<String>) = Map(lines.size, lines[0].length, lines.joinToString(""))

fun Map.move(): Map {
    val sb = StringBuilder(s)

    fun index(x: Int, y: Int) = y * n + x
    fun Cucumber.mx() = if (east) (x + 1) % n else x
    fun Cucumber.my() = if (east) (y) else (y + 1) % m
    fun Cucumber.canMove() = sb[index(mx(), my())] == '.'

    cucumbers.filter { c -> c.east && c.canMove() }.forEach { c ->
        sb[index(c.x, c.y)] = '.'
        sb[index(c.mx(), c.my())] = '>'
    }

    cucumbers.filter { c -> !c.east && c.canMove() }.forEach { c ->
        sb[index(c.x, c.y)] = '.'
        sb[index(c.mx(), c.my())] = 'v'
    }

    return Map(m, n, sb.toString())
}

// Part 1.

fun p1(input: List<String>): Int {
    var map = process(input)
    var prev = Map(0, 0, "")
    var i = 0
    while (map != prev) {
        map = map.move().also { prev = map }
        i++
    }
    return i
}

// Part 2.

fun p2(input: List<String>): Int {
    val map = process(input)
    println(map)
    return -1
}   

with(input()) {
    println(p1(this)) // 458
}
