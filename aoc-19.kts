// https://adventofcode.com/2021/day/19

import java.io.File
import kotlin.math.abs
import kotlin.math.min

fun input() = File("input/aoc-19.txt").readLines()

data class Beacon(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "($x, $y, $z)"
}

data class Scanner(val beacons: Set<Beacon>)

data class Transformation(val orientation: Beacon.() -> Beacon, val rotation: Beacon.() -> Beacon) {
    fun apply(b: Beacon) = b.orientation().rotation()

    fun apply(s: Scanner): Scanner = Scanner(s.beacons.mapTo(mutableSetOf<Beacon>(), ::apply))
}

fun parse(lines: List<String>): MutableList<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var beacons = mutableSetOf<Beacon>()
    lines.forEach { line ->
        when {
            line.isEmpty() -> scanners += Scanner(beacons).also { beacons = mutableSetOf<Beacon>() }
            !line.startsWith("---") -> line.split(',').map { it.toInt() }.let { beacons += Beacon(it[0], it[1], it[2]) }
        }
    }
    return scanners.apply { add(Scanner(beacons)) }
}

fun <T> product(s: Iterable<T>, t: Iterable<T>) = s.asSequence().flatMap { l -> t.map { r -> l to r } }

fun <T> product(s: Sequence<T>, t: Sequence<T>) = s.flatMap { l -> t.map { r -> l to r } }

fun createNormalisations(): Sequence<Transformation> {
    val o1: Beacon.() -> Beacon = { Beacon(x, y, z) } // x
    val o2: Beacon.() -> Beacon = { Beacon(-x, -y, z) } // -x
    val o3: Beacon.() -> Beacon = { Beacon(-y, x, z) } // y
    val o4: Beacon.() -> Beacon = { Beacon(y, -x, z) } // -y
    val o5: Beacon.() -> Beacon = { Beacon(-z, y, x) } // z
    val o6: Beacon.() -> Beacon = { Beacon(z, y, -x) } // -z
    val orientations = listOf(o1, o2, o3, o4, o5, o6)

    val r1: Beacon.() -> Beacon = { Beacon(x, y, z) } // 0
    val r2: Beacon.() -> Beacon = { Beacon(x, z, -y) } // 90
    val r3: Beacon.() -> Beacon = { Beacon(x, -y, -z) } // 180
    val r4: Beacon.() -> Beacon = { Beacon(x, -z, y) } // 270
    val rotations = listOf(r1, r2, r3, r4)

    return product(orientations, rotations).map { (o, r) -> Transformation(o, r) }
}

fun Beacon.match(b: Beacon, transformations: List<Transformation>) = transformations.filter { t -> this == t.apply(b) }

fun Scanner.align(s: Scanner, dimen: Beacon.() -> Int): Sequence<Int> {
    val d1 = beacons.asSequence().mapTo(mutableSetOf()) { it.dimen() }
    val d2 = s.beacons.asSequence().mapTo(mutableSetOf()) { it.dimen() }
    val candidates = product(d1, d2).map { (a, b) -> a - b }

    return candidates.filter { (d1 intersect d2.map { x -> it + x }).size >= min(d1.size, 11) }.distinct()
}

fun Scanner.findTranslation(s: Scanner): Transformation? {
    val dxSet = align(s) { x }
    val dySet = align(s) { y }
    val dzSet = align(s) { z }

    for (dx in dxSet) {
        for (dy in dySet) {
            for (dz in dzSet) {
                val translation = Transformation({ Beacon(x, y, z) }) { Beacon(x + dx, y + dy, z + dz) }
                val t = translation.apply(s)

                if (beacons.intersect(t.beacons).size >= 12) {
                   return translation
                }
            }
        }
    }
    return null
}

// Part 1.

fun p1(input: List<String>): Int {
    val scanners = parse(input)
    val normalisations = createNormalisations()

    fun Scanner.match(s: Scanner): Scanner? {
        normalisations.forEach { normalisation -> 
            val t = normalisation.apply(s)
            findTranslation(t)?.let { translation -> return translation.apply(t) }
        }
        return null
    }

    val matched = mutableSetOf<Scanner>(scanners[0])
    val pending = (scanners - scanners[0]).toMutableSet()

    fun step(): Boolean {
        pending.forEach { p -> 
            matched.forEach { s -> 
                s.match(p)?.let { t ->
                    matched += t
                    pending -= p
                    return true
                }
            }
        }
        return false
    }

    while (pending.isNotEmpty() && step()) {}

    return matched.flatMap { it.beacons }.distinct().size
}

// Part 2.

fun p2(input: List<String>): Int {
    val scanners = parse(input)
    val normalisations = createNormalisations()

    fun Scanner.match(s: Scanner): Pair<Scanner, Beacon>? {
        normalisations.forEach { normalisation -> 
            val t = normalisation.apply(s)
            findTranslation(t)?.let { translation -> return translation.apply(t) to translation.apply(normalisation.apply(Beacon(0, 0, 0))) }
        }
        return null
    }

    val matched = mutableSetOf<Scanner>(scanners[0])
    val pending = (scanners - scanners[0]).toMutableSet()
    val pos = mutableSetOf<Beacon>().apply { add(Beacon(0, 0, 0)) }

    fun step(): Boolean {
        pending.forEach { p ->
            matched.forEach { s ->
                s.match(p)?.let { (t, b) ->
                    matched += t
                    pending -= p
                    pos += b
                    return true
                }
            }
        }
        return false
    }

    while (pending.isNotEmpty() && step()) {}

    return product(pos, pos).maxOf { (a, b) -> abs(a.x - b.x) + abs(a.y - b.y) + (a.z - b.z) }
}

with(input()) {
    println(p1(this)) // 308
    println(p2(this)) // 12124
}
