// https://adventofcode.com/2021/day/15

import java.io.File
import kotlin.collections.ArrayDeque

fun input() = File("input/aoc-15.txt").readLines()

data class Point(val x: Int, val y: Int)

data class Matrix(val m: Int, val n: Int, private val def: Int = 0) : Iterable<Point> {
    private val data = Array(m) { Array<Int>(n) { def } }

    operator fun get(p: Point) = data[p.y][p.x]

    operator fun set(p: Point, z: Int) { data[p.y][p.x] = z }

    fun adj(p: Point) = sequenceOf(
        Point(p.x - 1, p.y),
        Point(p.x, p.y - 1),
        Point(p.x + 1, p.y),
        Point(p.x, p.y + 1),
    ).filter { q -> q.x >= 0 && q.x < n && q.y >= 0 && q.y < n }

    override fun iterator(): Iterator<Point> = object : Iterator<Point> {
        private var i = 0

        override fun next() = Point(i % n, i / n).also { i++ }

        override fun hasNext() = i < m*n
    }

    override fun toString() = data.map { r -> r.joinToString("") }.joinToString("\n")
}

fun Matrix(lines: List<String>) = Matrix(lines.size, lines.first().length).apply {
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c -> this[Point(x, y)] = c.toString().toInt() }
    }
}

// Part 1.

fun p1(input: List<String>): Int {
    val map = Matrix(input)

    val s = Point(0, 0)
    val risk = Matrix(map.m, map.n, Int.MAX_VALUE).apply { this[s] = 0 }
    val queue = ArrayDeque<Point>().apply { add(s) }
    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()
        map.adj(p).forEach { q ->
            if (risk[q] > risk[p] + map[q]) {
                risk[q] = risk[p] + map[q]
                queue += q
            }
        }
    }

    return risk[Point(map.n - 1, map.m - 1)]
}

// Part 2.

fun p2(input: List<String>): Int {
    fun enlarge(map: Matrix) = Matrix(map.m * 5, map.n * 5).also { big ->
        map.forEach { p -> 
            for (dx in 0..4) {
                for (dy in 0..4) {
                    val x = p.x + map.n * dx
                    val y = p.y + map.m * dy
                    big[Point(x, y)] = ((map[p] + (dx + dy) - 1) % 9) + 1
                }
            }
        }
    }

    val map = enlarge(Matrix(input))

    val s = Point(0, 0)
    val risk = Matrix(map.m, map.n, Int.MAX_VALUE).apply { this[s] = 0 }
    val queue = ArrayDeque<Point>().apply { add(s) }
    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()
        map.adj(p).forEach { q ->
            if (risk[q] > risk[p] + map[q]) {
                risk[q] = risk[p] + map[q]
                queue += q
            }
        }
    }

    return risk[Point(map.n - 1, map.m - 1)]
}

with(input()) {
    println(p1(this)) // 462
    println(p2(this)) // 2846
}
