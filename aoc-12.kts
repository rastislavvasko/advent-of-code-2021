// https://adventofcode.com/2021/day/12

import java.io.File
import kotlin.collections.ArrayDeque

data class Cave(val name: String) {
    val edges = mutableSetOf<Cave>()
    val isBig = name[0] in 'A'..'Z'
}

fun input() = File("input/aoc-12.txt").readLines().run {
    val caves = mutableMapOf<String, Cave>()
    forEach { line ->
        val (c1, c2) = line.split('-', limit = 2).map { name -> caves.getOrPut(name) { Cave(name) } }
        c1.edges.add(c2)
        c2.edges.add(c1)
    }
    caves["start"]!!
}

// Part 1.

fun p1(start: Cave): Int {
    data class Path(val path: List<Cave>) {
        fun next() = path.last().edges.asSequence().filter { c -> c.isBig || c !in path }.map { c -> Path(path + c) }
    }

    val paths = mutableListOf<Path>()

    val q = ArrayDeque<Path>().apply { add(Path(listOf(start))) }
    while (q.isNotEmpty()) {
        val p = q.removeLast()

        if (p.path.last().name == "end") {
            paths += p
        } else {
            q.addAll(p.next())
        }
    }

    return paths.size
}

// Part 2.

fun p2(start: Cave): Int {
    data class Path(val path: List<Cave>, val twice: Boolean) {
        fun next() = path.last().edges.asSequence().mapNotNull { c -> 
            when { 
                c.isBig -> Path(path + c, twice)
                c !in path -> Path(path + c, twice)
                !twice && c.name != "start" -> Path(path + c, true)
                else -> null
            }
        }
    }

    val paths = mutableListOf<Path>()

    val q = ArrayDeque<Path>().apply { add(Path(listOf(start), false)) }
    while (q.isNotEmpty()) {
        val p = q.removeLast()

        if (p.path.last().name == "end") {
            paths += p
        } else {
            q.addAll(p.next())
        }
    }

    return paths.size
}

with(input()) {
    println(p1(this)) // 3369
    println(p2(this)) // 85883
}
