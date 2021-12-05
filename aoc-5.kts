// https://adventofcode.com/2021/day/5

import java.io.File
import kotlin.math.min
import kotlin.math.max

data class Point(val x: Int, val y: Int)

data class Line(val a: Point, val b: Point)

fun input() = File("input/aoc-5.txt").readLines().map { line ->
    fun String.toPoint() = split(',').let { Point(it[0].toInt(), it[1].toInt()) }
    line.split(" -> ").let { Line(it[0].toPoint(), it[1].toPoint()) }
}

// Part 1.

fun p1(lines: List<Line>): Int {
    val m = mutableMapOf<Point, Int>()
    var res = 0
    lines.forEach { (a, b) ->
        val points = when { 
            a.x == b.x -> (min(a.y, b.y)..max(a.y, b.y)).map { y -> Point(a.x, y) }
            a.y == b.y -> (min(a.x, b.x)..max(a.x, b.x)).map { x -> Point(x, a.y) }
            else -> null
        }
        points?.forEach { p -> 
            m[p] = (m[p] ?: 0) + 1
            if (m[p] == 2) res++
        }
    }
    return res
}

// Part 2.

fun p2(lines: List<Line>): Int {
    val m = mutableMapOf<Point, Int>()
    var res = 0
    lines.forEach { line ->
        val (a, b) = when {
            line.a.x <= line.b.x -> line.a to line.b
            else -> line.b to line.a
        }
        val points = when { 
            a.x == b.x -> (min(a.y, b.y)..max(a.y, b.y)).map { y -> Point(a.x, y) }
            a.y == b.y -> (min(a.x, b.x)..max(a.x, b.x)).map { x -> Point(x, a.y) }
            a.y < b.y -> (0..(b.y - a.y)).map { d -> Point(a.x + d, a.y + d) }
            else -> (0..(a.y - b.y)).map { d -> Point(a.x + d, a.y - d) }
        }
        points.forEach { p -> 
            m[p] = (m[p] ?: 0) + 1
            if (m[p] == 2) res++
        }
    }
    return res
}

val lines = input()
println(p1(lines))
println(p2(lines))
