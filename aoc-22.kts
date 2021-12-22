// https://adventofcode.com/2021/day/22

import java.io.File
import kotlin.math.min
import kotlin.math.max

fun input() = File("input/aoc-22.txt").readLines().map(::process)

data class Point(val x: Int, val y: Int, val z: Int)

data class Cube(val a: Point, val b: Point)

data class Step(val cube: Cube, val enabled: Boolean)

fun process(line: String): Step {
    val (def, dimens) = line.split(" ")
    val (x, y, z) = dimens.split(",").map { it.split("=").last().split("..").map { v -> v.toInt() } }

    val a = Point(x[0], y[0], z[0])
    val b = Point(x[1], y[1], z[1])
    return Step(Cube(a, b), def == "on")
}

// Part 1.

fun p1(steps: List<Step>): Int {
    operator fun Cube.contains(p: Point) = p.x >= a.x && p.y >= a.y && p.z >= a.z && p.x <= b.x && p.y <= b.y && p.z <= b.z

    val a = Point(-50, -50, -50)
    val b = Point(50, 50, 50)
    val s = Point(b.x - a.x + 1, b.y - a.y + 1, b.z - a.z + 1)
    val data = Array(s.z) { Array(s.y) { BooleanArray(s.x) { false } } }
    
    fun get(p: Point) = data[p.z - a.z][p.y - a.y][p.x - a.x]

    fun set(p: Point, v: Boolean) { data[p.z - a.z][p.y - a.y][p.x - a.x] = v }

    fun iterate(fn: (Point) -> Unit) {
        for (x in a.x..b.x) {
            for (y in a.y..b.y) {
                for (z in a.z..b.z) {
                    fn(Point(x, y, z))
                }
            }
        }
    }

    steps.forEach { step ->
        iterate { p -> if (p in step.cube) set(p, step.enabled) }
    }

    var sum = 0
    iterate { p -> if (get(p)) sum += 1 }
    return sum
}

// Part 2.

fun p2(steps: List<Step>): Long {
    fun min(a: Point, b: Point) = Point(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))

    fun max(a: Point, b: Point) = Point(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))

    fun Cube.clamp(c: Cube) = Cube(max(a, c.a), min(b, c.b))

    fun overlap(c1: Cube, c2: Cube): Boolean {
        if (c1.b.x < c2.a.x) return false
        if (c1.a.x > c2.b.x) return false
        if (c1.b.z < c2.a.z) return false
        if (c1.a.z > c2.b.z) return false
        if (c1.b.y < c2.a.y) return false
        if (c1.a.y > c2.b.y) return false
        return true
    }

    fun Cube.isValid() = a.x <= b.x && a.y <= b.y && a.z <= b.z

    fun Cube.subtract(cube: Cube): List<Cube> {
        val c = cube.clamp(this)

        val x = listOf(Pair(a.x, c.a.x - 1), Pair(c.a.x, c.b.x), Pair(c.b.x + 1, b.x))
        val y = listOf(Pair(a.y, c.a.y - 1), Pair(c.a.y, c.b.y), Pair(c.b.y + 1, b.y))
        val z = listOf(Pair(a.z, c.a.z - 1), Pair(c.a.z, c.b.z), Pair(c.b.z + 1, b.z))

        val splits = mutableListOf<Cube>()
        x.forEach { (ax, bx) ->
            y.forEach { (ay, by) ->
                z.forEach { (az, bz) -> 
                    val a = Point(ax, ay, az)
                    val b = Point(bx, by, bz)
                    val split = Cube(a, b)
                    if (split.isValid() && split != c) splits += split
                }
            }
        }
        return splits
    }

    fun Cube.size() = (b.x - a.x + 1L) * (b.y - a.y + 1L) * (b.z - a.z + 1L)

    var enabled = listOf<Cube>()

    steps.forEach { step ->
        enabled = enabled.flatMap { cube ->
            if (overlap(cube, step.cube)) {
                cube.subtract(step.cube)
            } else {
                listOf(cube)
            }
        }
        if (step.enabled) enabled += step.cube
    }

    return enabled.fold(0L) { sum, cube -> sum + cube.size() }
}   

with(input()) {
    println(p1(this)) // 551693
    println(p2(this)) // 1165737675582132
}
