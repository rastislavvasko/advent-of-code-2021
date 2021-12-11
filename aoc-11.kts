// https://adventofcode.com/2021/day/11

import java.io.File
import kotlin.collections.ArrayDeque

fun input() = File("input/aoc-11.txt").readLines()

data class Point(val x: Int, val y: Int)

data class Matrix(val n: Int, private val def: Int = 0) : Iterable<Point> {
    private val data = Array(n) { Array<Int>(n) { def } }

    operator fun get(p: Point) = data[p.y][p.x]

    operator fun set(p: Point, z: Int) { data[p.y][p.x] = z }

    fun adj(p: Point) = sequenceOf(
        Point(p.x, p.y - 1),
        Point(p.x + 1, p.y - 1),
        Point(p.x + 1, p.y),
        Point(p.x + 1, p.y + 1),
        Point(p.x, p.y + 1),
        Point(p.x - 1, p.y + 1),
        Point(p.x - 1, p.y),
        Point(p.x - 1, p.y - 1),
    ).filter { q -> q.x >= 0 && q.x < n && q.y >= 0 && q.y < n }

    override fun iterator(): Iterator<Point> = object : Iterator<Point> {
        private var i = 0

        override fun next() = Point(i % n, i / n).also { i++ }

        override fun hasNext() = i < n*n
    }

    override fun toString() = data.map { r -> r.joinToString("") }.joinToString("\n")
}

fun Matrix(lines: List<String>) = Matrix(lines.size).apply {
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c -> this[Point(x, y)] = c.toString().toInt() }
    }
}

// Part 1.

fun p1(input: List<String>): Int {
    val dumbos = Matrix(input)

    fun flash(p: Point) = dumbos.adj(p).filter { ++dumbos[it] > 9 }

    fun step(): Int {
        val done = mutableSetOf<Point>()
        val pending = ArrayDeque<Point>()

        dumbos.forEach { p -> if (++dumbos[p] > 9) pending.addLast(p) }

        while (pending.isNotEmpty()) {
            val p = pending.removeLast()
            if (done.add(p)) pending += flash(p)
        }

        dumbos.forEach { p -> if (dumbos[p] > 9) dumbos[p] = 0 }

        return done.size
    }

    return (0 until 100).sumOf { step() }
}

// Part 2.

fun p2(input: List<String>): Int {
    val dumbos = Matrix(input)

    fun flash(p: Point) = dumbos.adj(p).filter { ++dumbos[it] > 9 }

    fun step(): Boolean {
        val done = mutableSetOf<Point>()
        val pending = ArrayDeque<Point>()

        dumbos.forEach { p -> if (++dumbos[p] > 9) pending.addLast(p) }

        while (pending.isNotEmpty()) {
            val p = pending.removeLast()
            if (done.add(p)) pending += flash(p)
        }

        dumbos.forEach { p -> if (dumbos[p] > 9) dumbos[p] = 0 }

        return dumbos.all { p -> dumbos[p] == 0 }
    }

    return generateSequence(1, Int::inc).first { step() }
}

with(input()) {
    println(p1(this)) // 1691
    println(p2(this)) // 216
}
