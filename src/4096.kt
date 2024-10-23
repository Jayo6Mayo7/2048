import kotlin.math.abs
import kotlin.random.Random

fun main() {
    println("NOT MAIN")
    var spawnOne = 2
    var spawnOneChance = 90
    var spawnTwo = 2
    var spawnTwoChance = 10

    data class MetaSquare(
        var x: Int,
        var y: Int,
        var value: Int,
        var occupied: Boolean = false,
        var combined: Boolean = false
    )

    data class ProtoSquare(var protoX: Int, var protoY: Int)

    val metaGrid = MutableList(4) { MutableList(4) { MetaSquare(x = -1, y = -1, value = -1) } }

    fun startSquare(): ProtoSquare {
        while (true) {
            val x = Random.nextInt(0, 4)
            val y = Random.nextInt(0, 4)
            if (!metaGrid[x][y].occupied) {
                metaGrid[x][y].occupied = true
                val returner = ProtoSquare(protoX = x, protoY = y)
                return returner
            }
        }
    }

    fun startValue(): Int {
        val value = Random.nextInt(1, 100)
        if (value <= spawnOneChance) {
            return spawnOne
        } else if (value >= spawnTwoChance)
            return spawnTwo
        return -1
    }

    fun makeSquare() {
        val proto = startSquare()
        val placeMetaSquare = MetaSquare(x = proto.protoX, y = proto.protoY, value = startValue())
        metaGrid[placeMetaSquare.x][placeMetaSquare.y] = placeMetaSquare
    }

    fun removeSquare() {
        val xInput = readln().toInt()
        val yInput = readln().toInt()
        val valueInput = metaGrid[xInput][yInput].value
        metaGrid[xInput][yInput] = MetaSquare(xInput, yInput, valueInput, occupied = false, combined = false)
        metaGrid[xInput][yInput] = MetaSquare(-1, -1, -1)
    }

    fun valueSquare() {
        val xInput = readln().toInt()
        val yInput = readln().toInt()
        val valueInput = readln().toInt()
        metaGrid[xInput][yInput].value = valueInput
        /*
        when (metaGrid[xInput][yInput].value) {
            -1 -> metaGrid[xInput][yInput].occupied = false
            else -> metaGrid[xInput][yInput].occupied = true
        }

         */
    }

    fun refreshOccupied() {
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                when (metaGrid[x][y].value) {
                    -1 -> metaGrid[x][y].occupied = false
                    else -> metaGrid[x][y].occupied = true
                }
                ++x
            }
            ++y
            x = 0
        }
    }

    fun clearCombinations() {
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                metaGrid[x][y].combined = false
                ++x
            }
            ++y
            x = 0
        }
    }

    fun clearGrid() {
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                metaGrid[x][y] = MetaSquare(-1, -1, -1)
                val valueInput = metaGrid[x][y].value
                metaGrid[x][y] = MetaSquare(x, y, valueInput, occupied = false, combined = false)
                ++x
            }
            ++y
            x = 0
        }
    }

    fun combine(x1: Int, y1: Int, x2: Int, y2: Int) {
        metaGrid[x1][y1].value *= 2
        metaGrid[x2][y2].occupied = false
        metaGrid[x2][y2] = MetaSquare(-1, -1, -1)
        clearCombinations()
        metaGrid[x1][y1].combined = true
        //x1y1 = square that increases
        //x2y2 = square that goes away
    }

    fun squareMeta(x: Int, y: Int, xMove: Int, yMove: Int) {
        if (metaGrid[x][y].value != -1) {
            if (xMove != 0 && yMove != 0) {
                metaGrid[x][y].occupied = false
                metaGrid[x + xMove][y + yMove].occupied = true
                val newValue = metaGrid[x][y].value
                metaGrid[x + xMove][y + yMove] = MetaSquare(x + xMove, y + yMove, newValue)
                metaGrid[x][y] = MetaSquare(-1, -1, -1)
            }
        }
        refreshOccupied()
    }

    var change = false
    fun omegaMove(xDif: Int, yDif: Int) {
        change = false
        clearCombinations()
        refreshOccupied()
        var x = -1
        var y = -1
        var w = -1
        //0 = vertical
        //1 = horizontal
        var direction = 0
        when (xDif) {
            -1 -> {
                x = 0; y = 0; w = -1; direction = 1
            }

            1 -> {
                x = 3; y = 3; w = 1; direction = 1
            }
        }
        when (yDif) {
            -1 -> {
                y = 0; x = 0; w = -1
            }

            1 -> {
                y = 3; x = 3; w = 1
            }
        }
        repeat(4) {
            repeat(4) {
                if (metaGrid[x][y].value != -1) {
                    var metaX = x
                    var metaY = y
                    repeat(3) {
                        if (!metaGrid[metaX][metaY].combined) {
                            if (-1 < metaX + xDif && metaX + xDif < 4 && -1 < metaY + yDif && metaY + yDif < 4) {
                                if (!metaGrid[metaX + xDif][metaY + yDif].occupied) {
                                    squareMeta(metaX, metaY, xDif, yDif)
                                    metaGrid[metaX][metaY].occupied = false
                                    metaGrid[metaX][metaY].combined = false
                                    val value = metaGrid[metaX][metaY].value
                                    metaGrid[metaX][metaY].value = -1
                                    metaX += xDif
                                    metaY += yDif
                                    metaGrid[metaX][metaY].occupied = true
                                    metaGrid[metaX][metaY].value = value
                                    change = true
                                } else if (metaGrid[metaX + xDif][metaY + yDif].value == metaGrid[metaX][metaY].value && !metaGrid[metaX + xDif][metaY + yDif].combined) {
                                    combine(metaX + xDif, metaY + yDif, metaX, metaY)
                                    change = true
                                }
                            }
                        }
                    }
                }
                when (xDif) {
                    -1 -> ++x
                    1 -> --x
                }
                when (yDif) {
                    -1 -> ++y
                    1 -> --y
                }
            }
            when (yDif) {
                -1 -> {
                    ++x; y = 0
                }

                1 -> {
                    --x; y = 3
                }
            }
            when (xDif) {
                -1 -> {
                    ++y; x = 0
                }

                1 -> {
                    --y; x = 3
                }
            }
        }
        clearCombinations()
        refreshOccupied()
    }

    fun map() {
        var x = 0
        var y = 3
        repeat(4) {
            print("\n")
            repeat(4) {
                if (metaGrid[x][y].value != -1) {
                    print(metaGrid[x][y].value)
                } else print("-")
                var length = 4
                if (metaGrid[x][y].value >= 0) {
                    length = 5 - metaGrid[x][y].value.toString().length
                }
                if (metaGrid[x][y].value < -1) {
                    length = 4 - abs(metaGrid[x][y].value).toString().length
                }
                repeat(length) {
                    print(" ")
                }
                ++x
            }
            --y
            x = 0
        }
    }

    var spawning = true
    fun toggleSpawning() {
        spawning = !spawning
        println("Spawning is now $spawning")
    }

    fun setSpawns() {
        print("s1: ")
        spawnOne = readln().toInt()
        print("s1c: ")
        spawnOneChance = readln().toInt()
        print("s2: ")
        spawnTwo = readln().toInt()
        print("s2c: ")
        spawnTwoChance = readln().toInt()
    }

    fun multiplier() {
        val multiplierValue = readln().toInt()
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                if (metaGrid[x][y].value > 0) {
                    metaGrid[x][y].value *= multiplierValue
                }
                ++x
            }
            ++y
            x = 0
        }
    }

    fun delete() {
        refreshOccupied()
        val x = readln().toInt()
        val y = readln().toInt()
        println(metaGrid[x][y].occupied)
    }

    makeSquare()
    while (true) {
        map()
        val yo = readln()
        when (yo) {
            "w", "up" -> { omegaMove(0, 1); if (spawning && change) { makeSquare() } }
            "s", "down" -> { omegaMove(0, -1); if (spawning && change) { makeSquare() } }
            "d", "right" -> { omegaMove(1, 0); if (spawning && change) { makeSquare() } }
            "a", "left" -> { omegaMove(-1, 0); if (spawning && change) { makeSquare() } }
            "make" -> makeSquare()
            "remove" -> removeSquare()
            "value" -> valueSquare()
            "clear" -> clearGrid()
            "spawning" -> toggleSpawning()
            "spawns" -> setSpawns()
            "multiplier" -> multiplier()
            "occ" -> delete()

        }
    }
}