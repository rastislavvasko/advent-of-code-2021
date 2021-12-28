// https://adventofcode.com/2021/day/23

import java.io.File
import kotlin.collections.ArrayDeque
import kotlin.collections.binarySearchBy
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun input() = File("input/aoc-23.txt").readLines()

data class Map(private val s: String) {
    operator fun get(x: Int, y: Int) = s[y * 13 + x]

    val homes get() = s.length / 13 - 3
    val lastHomeY get() = homes + 1

    fun pods() = IntArray(homes * 4).apply {
        s.forEachIndexed { i, c ->
            if (c == 'A' || c == 'B' || c == 'C' || c == 'D') {
                var j = (size / 4) * (c - 'A')
                while (this[j] != 0) j++
                this[j] = i
            }
        }
    }

    fun move(x1: Int, y1: Int, x2: Int, y2: Int): Map {
        check(this[x2, y2] == '.')
        val sb = StringBuilder(s)
        sb.set(y2 * 13 + x2, this[x1, y1])
        sb.set(y1 * 13 + x1, '.')
        return Map(sb.toString())
    }

    override fun toString() = s.chunked(13).joinToString("\n")
}

data class State(val map: Map, val energy: Int) {
    var prev: State? = null

    override fun toString() = "$map energy=$energy"
}

fun process(lines: List<String>): State {
    val s = lines.map { it.padEnd(13, '#').replace(' ', '#') }.joinToString("")
    return State(Map(s), 0)
}

class PriorityQueue() {
    private val list = ArrayDeque<State>()

    val size get() = list.size

    fun add(state: State) {
        val pos = list.binarySearchBy(state.energy) { it.energy }
        if (pos >= 0) {
            list.add(pos + 1, state)
        } else {
            list.add(-pos - 1, state)
        }
    }

    fun addAll(states: Sequence<State>) {
        states.forEach { add(it) }
    }

    fun next() = list.removeFirst()
}

data class Path(val x1: Int, val y1: Int, val x2: Int, val y2: Int)

val Char.cost get() = when (this - 'A') {
    0 -> 1
    1 -> 10
    2 -> 100
    3 -> 1000
    else -> error("")
}

val Char.homeX get() = (3 + 2 * (this - 'A'))

val homesX = listOf(3, 5, 7, 9)
val hallsX = sequenceOf(1, 2, 4, 6, 8, 10, 11)

fun State.same(x1: Int, y1: Int, x2: Int, y2: Int) = x1 == x2 && y1 == y2

fun State.isClear(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    if (x1 == x2) {
        // Valid y stretch.
        (min(y1, y2)..max(y1, y2)).forEach { y ->
            if (same(x1, y, x1, y1)) return@forEach
            if (map[x1, y] != '.') return false
        }
    } else {
        // Valid x stretch.
        (min(x1, x2)..max(x1, x2)).forEach { x ->
            if (same(x, 1, x1, y1)) return@forEach
            if (map[x, 1] != '.') return false
        }

        // Valid y1 stretch.
        (1..y1).forEach { y ->
            if (same(x1, y, x1, y1)) return@forEach
            if (map[x1, y] != '.') return false
        }

        // Valid y2 stretch.
        (1..y2).forEach { y ->
            if (same(x2, y, x1, y1)) return@forEach
            if (map[x2, y] != '.') return false
        }
    }
    return true
}

fun State.isValid(path: Path): Boolean {
    val x1 = path.x1
    val y1 = path.y1
    val x2 = path.x2
    val y2 = path.y2

    // Noop.
    if (x1 == x2 && y1 == y2) return false

    // Can't stop outside of house.
    if (y2 == 1 && x2 in homesX) return false

    // Can't enter other home.
    if (y2 > 1 && x2 != map[x1, y1].homeX) return false

    return isClear(x1, y1, x2, y2)
}

fun dist(x1: Int, y1: Int, x2: Int, y2: Int) = when {
    x1 == x2 -> abs(y1 - y2)
    else -> abs(x1 - x2) + (y1 - 1) + (y2 - 1)
}

fun State.move(path: Path): State {
    val map2 = map.move(path.x1, path.y1, path.x2, path.y2)
    val dist = dist(path.x1, path.y1, path.x2, path.y2)
    val cost = map[path.x1, path.y1].cost
    return State(map2, dist * cost)
}

fun State.moves(pod: Int): Sequence<State> {
    val px = pod % 13
    val py = pod / 13
    val homeX = map[px, py].homeX
    val paths: Sequence<Path> = when {
        py == 1 -> {
            // In hall. Go home.
            (2..map.lastHomeY).asSequence().map { y -> Path(px, py, homeX, y) }.filter { path ->
                val yBelow = (path.y2 + 1)..map.lastHomeY
                path.y2 == map.lastHomeY || yBelow.all { y -> map[path.x2, y] == map[px, py] }
            }
        }
        py > 1 && px == homeX -> {
            // In own home. Go to hall if below is someone else.
            val yBelow = (py + 1)..map.lastHomeY
            if (py < map.lastHomeY && yBelow.any { y -> map[px, y] != map[px, py] }) {
                hallsX.asSequence().map { x -> Path(px, py, x, 1) }
            } else {
                emptySequence()
            }
        }
        py > 1 && px != homeX -> {
            // In other home. Go to hall, if above empty.
            if ((2..py - 1).all { y -> map[px, y] == '.' }) {
               hallsX.asSequence().map { x -> Path(px, py, x, 1) }
            } else {
                emptySequence()
            }
        }
        else -> emptySequence()
    }

    return paths.filter { isValid(it) }.distinct().map { move(it) }
}

// Part 1.

fun p1(input: List<String>): Int {
    val win = process("""
        #############
        #...........#
        ###A#B#C#D###
        ###A#B#C#D###
        #############
    """.trimIndent().split("\n")).map

    val queue = PriorityQueue().apply { add(process(input)) }
    val visited = mutableSetOf<Map>()
    val cache = mutableMapOf<Map, Int>().withDefault { Int.MAX_VALUE }
    while (queue.size > 0) {
        val state = queue.next()
        visited += state.map

        state.map.pods().forEach { pod ->
            state.moves(pod).forEach { move ->
                if (move.map !in visited) {
                    val energy = state.energy + move.energy
                    if (energy < cache.getValue(move.map)) {
                        cache[move.map] = energy
                        queue.add(State(move.map, energy))
                    }
                }
            }
        }
    }
    return cache.getValue(win)
}

// Part 2.

fun p2(input: List<String>): Int {
    val largeInput = input.toMutableList().apply {
        add(3, "###D#C#B#A#")
        add(4, "###D#B#A#C#")
    }
    val win = process("""
        #############
        #...........#
        ###A#B#C#D###
        ###A#B#C#D###
        ###A#B#C#D###
        ###A#B#C#D###
        #############
    """.trimIndent().split("\n")).map

    val queue = PriorityQueue().apply { add(process(largeInput)) }
    val visited = mutableSetOf<Map>()
    val cache = mutableMapOf<Map, Int>().withDefault { Int.MAX_VALUE }
    while (queue.size > 0) {
        val state = queue.next()
        visited += state.map

        state.map.pods().forEach { pod ->
            state.moves(pod).forEach { move ->
                if (move.map !in visited) {
                    val energy = state.energy + move.energy
                    if (energy < cache.getValue(move.map)) {
                        cache[move.map] = energy
                        queue.add(State(move.map, energy))
                    }
                }
            }
        }
    }
    return cache.getValue(win)
}

with(input()) {
    println(p1(this)) // 15237
    println(p2(this)) // 47509
}
