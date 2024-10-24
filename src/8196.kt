import kotlin.math.abs
import kotlin.random.Random

fun main() {
    println("Enter a board size: ")
    val size = readln().toInt()
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

    val metaGrid = MutableList(size) { MutableList(size) { MetaSquare(x = -1, y = -1, value = -1) } }

    fun startSquare(): ProtoSquare {
        while (true) {
            val x = Random.nextInt(0, size)
            val y = Random.nextInt(0, size)
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
        repeat(size) {
            repeat(size) {
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
        repeat(size) {
            repeat(size) {
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
        repeat(size) {
            repeat(size) {
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

    var change = true
    fun omegaMove(xDif: Int, yDif: Int) {
        change = false
        clearCombinations()
        refreshOccupied()
        var x = -1
        var y = -1
        when (xDif) {
            -1 -> {
                x = 0; y = 0
            }

            1 -> {
                x = size - 1; y = size - 1
            }
        }
        when (yDif) {
            -1 -> {
                y = 0; x = 0
            }

            1 -> {
                y = size - 1; x = size - 1
            }
        }
        repeat(size) {
            repeat(size) {
                if (metaGrid[x][y].value != -1) {
                    var metaX = x
                    var metaY = y
                    repeat(size - 1) {
                        if (!metaGrid[metaX][metaY].combined) {
                            if (-1 < metaX + xDif && metaX + xDif < size && -1 < metaY + yDif && metaY + yDif < size) {
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
                    --x; y = size - 1
                }
            }
            when (xDif) {
                -1 -> {
                    ++y; x = 0
                }

                1 -> {
                    --y; x = size - 1
                }
            }
        }
        clearCombinations()
        refreshOccupied()
    }

    fun map() {
        var x = 0
        var y = size - 1
        repeat(size) {
            print("\n")
            repeat(size) {
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
        repeat(size) {
            repeat(size) {
                if (metaGrid[x][y].value > 0) {
                    metaGrid[x][y].value *= multiplierValue
                }
                ++x
            }
            ++y
            x = 0
        }
    }

    fun occupyCheck() {
        refreshOccupied()
        val x = readln().toInt()
        val y = readln().toInt()
        println(metaGrid[x][y].occupied)
    }

    while (true) {
        if (change) {if (spawning) {makeSquare()}; map(); change = false}
        when (readln()) {
            "w", "up" -> { omegaMove(0, 1) }
            "s", "down" -> { omegaMove(0, -1) }
            "d", "right" -> { omegaMove(1, 0) }
            "a", "left" -> { omegaMove(-1, 0) }
            "make" -> {makeSquare(); map()}
            "remove" -> {removeSquare(); map()}
            "value" -> {valueSquare(); map()}
            "clear" -> {clearGrid(); map()}
            "spawning" -> toggleSpawning()
            "spawns" -> setSpawns()
            "multiplier" -> {multiplier(); map()}
            "occ" -> occupyCheck()
        }
    }
}