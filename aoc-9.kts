// https://adventofcode.com/2021/day/9

import java.io.File
import kotlin.collections.ArrayDeque

data class Point(val x: Int, val y: Int)

data class Matrix(val m: Int, val n: Int, private val def: Int = 0) {
    private val data = Array(m) { Array<Int>(n) { def } }

    operator fun get(x: Int, y: Int): Int = data[y][x]

    operator fun set(x: Int, y: Int, z: Int) { data[y][x] = z }

    fun adj(x: Int, y: Int) = sequenceOf(
        Point(x, y - 1),
        Point(x + 1, y),
        Point(x, y + 1),
        Point(x - 1, y),
    ).filter { p -> p.x >= 0 && p.x < n && p.y >= 0 && p.y < m }

    fun isLow(x: Int, y: Int): Boolean {
        val z = data[y][x]
        return adj(x, y).all { p -> z < data[p.y][p.x] }
    }

    fun lows(): List<Point> = mutableListOf<Point>().apply {
        forEach { x, y, _ -> if (isLow(x, y)) this += Point(x, y) }
    }

    fun forEach(callback: (Int, Int, Int) -> Unit) {
        (0 until m).forEach { y ->
            (0 until n).forEach { x -> callback(x, y, data[y][x]) }
        }
    }
}

fun input() = File("input/aoc-9.txt").readLines().let { lines ->
    Matrix(lines.size, lines.first().length).also { map ->
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                map[x, y] = c.toString().toInt()
            }
        }
    }
}

// Part 1.

fun p1(map: Matrix): Int {
    return map.lows().sumOf { (x, y) -> map[x, y] + 1 }
}

// Part 2.

fun p2(map: Matrix): Int {
    val marked = Matrix(map.m, map.n, -1)
    map.forEach { x, y, z -> if (z == 9) marked[x, y] = -2 }

    val lows = map.lows()
    lows.forEachIndexed { i, s ->
        val deck = ArrayDeque<Point>().apply { add(s) }
        while (!deck.isEmpty()) {
            val p = deck.removeFirst()
            if (marked[p.x, p.y] != -1) continue
            marked[p.x, p.y] = i
            marked.adj(p.x, p.y).forEach { r -> if (marked[r.x, r.y] == -1) deck.add(r) }
        }
    }

    val basins = IntArray(lows.size)
    marked.forEach { _, _, z -> if (z >= 0) basins[z]++ }
    basins.sortDescending()

    return basins.take(3).reduce(Int::times)
}

with(input()) {
    println(p1(this))
    println(p2(this))
}
