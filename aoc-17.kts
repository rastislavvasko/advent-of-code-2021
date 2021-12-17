// https://adventofcode.com/2021/day/17

import java.io.File
import kotlin.math.sign

fun input() = File("input/aoc-17.txt").readLines().single().let { line ->
    val (x, y) = line.substringAfter(": ").split(", ").map { s ->
        s.substringAfter("=").split("..").map { it.toInt() }
    }
    Rect(Point(x[0], y[1]), Point(x[1], y[0]))
}

data class Point(val x: Int, val y: Int)

data class Rect(val a: Point, val b: Point)

data class State(val pos: Point, val vel: Point)

fun Point.inside(r: Rect) = r.a.x <= x && x <= r.b.x && r.a.y >= y && y >= r.b.y

fun State.step() = State(Point(pos.x + vel.x, pos.y + vel.y), Point(vel.x - vel.x.sign, vel.y - 1))

fun <S, T> product(s: Iterable<S>, t: Iterable<T>) =
    s.asSequence().flatMap { l -> t.map { r -> l to r } }

// Part 1.

fun p1(target: Rect): Int {
    fun State.shoot(): Int {
        var state = this
        var max = pos.y
        var hit = false
        while (state.pos.x <= target.b.x && state.pos.y >= target.b.y) {
            if (state.pos.inside(target)) hit = true
            if (state.pos.y > max) max = state.pos.y
            state = state.step()
        }
        return if (hit) max else pos.y
    }

    val xrange = 0..target.b.x
    val yrange = 0..-target.b.y
    return product(xrange, yrange).maxOf { (x, y) -> State(Point(0, 0), Point(x, y)).shoot() }
}

// Part 2.

fun p2(target: Rect): Int {
    fun State.shoot(): Boolean {
        var state = this
        var hit = false
        while (state.pos.x <= target.b.x && state.pos.y >= target.b.y) {
            if (state.pos.inside(target)) hit = true
            state = state.step()
        }
        return hit
    }

    val xrange = 0..target.b.x
    val yrange = target.b.y..-target.b.y
    return product(xrange, yrange).count { (x, y) -> State(Point(0, 0), Point(x, y)).shoot() }
}

with(input()) {
    println(p1(this)) // 2775
    println(p2(this)) // 1566
}
