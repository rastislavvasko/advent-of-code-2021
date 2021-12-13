// https://adventofcode.com/2021/day/13

import java.io.File

fun input() = File("input/aoc-13.txt").readLines().let { lines ->
    val del = lines.indexOf("")
    val points = lines.subList(0, del).map { l ->
        l.split(',') }.map { Point(it[0].toInt(), it[1].toInt())
    }
    val folds = lines.subList(del + 1, lines.size).map { l -> 
        val (dim, num) = l.split('=')
        if (dim.last() == 'x') Point(num.toInt(), 0) else Point(0, num.toInt())
    }
    points.toSet() to folds
}

data class Point(val x: Int, val y: Int)

// Part 1.

fun p1(points: Set<Point>, folds: List<Point>): Int {
    fun Set<Point>.fold(fold: Point) = mapNotNullTo(mutableSetOf<Point>()) { p ->
        when (fold.x != 0) {
            true -> if (p.x < fold.x) p else Point(fold.x - (p.x - fold.x), p.y)
            else -> if (p.y < fold.y) p else Point(p.x, fold.y - (p.y - fold.y))
        }
    }

    return points.fold(folds.first()).size
}

// Part 2.

fun p2(points: Set<Point>, folds: List<Point>): List<String> {
    fun Set<Point>.fold(fold: Point) = mapNotNullTo(mutableSetOf<Point>()) { p ->
        when (fold.x != 0) {
            true -> if (p.x < fold.x) p else Point(fold.x - (p.x - fold.x), p.y)
            else -> if (p.y < fold.y) p else Point(p.x, fold.y - (p.y - fold.y))
        }
    }

    fun Set<Point>.format(): List<String> {
        val m = maxOf { it.y + 1 }
        val n = maxOf { it.x + 1 }
        return (0 until m).map { y ->
            val arr = CharArray(n) { ' ' }
            filter { it.y == y }.forEach { p -> arr[p.x] = '#' }
            String(arr)
        }
    }

    return folds.fold(points) { acc, f -> acc.fold(f) }.format()
}

with(input()) {
    val (points, folds) = this
    println(p1(points, folds)) // 827.
    p2(points, folds).forEach { println(it) } // EAHKRECP.
}
