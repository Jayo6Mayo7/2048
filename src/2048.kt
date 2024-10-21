import kotlin.math.abs
import kotlin.random.Random

fun main() {
    var spawnOne = -1
    var spawnOneChance = -1
    var spawnTwo = -1
    var spawnTwoChance = -1
    val spawnChange = readln().toBoolean()
    if (spawnChange) {
        print("s1: ")
        spawnOne = readln().toInt()
        print("s1c: ")
        spawnOneChance = readln().toInt()
        print("s2: ")
        spawnTwo = readln().toInt()
        print("s2c: ")
        spawnTwoChance = readln().toInt()
    }
    else {
        spawnOne = 2
        spawnOneChance = 90
        spawnTwo = 4
        spawnTwoChance = 10
    }
    data class OcuSquare(var occupied: Boolean = false, var combined: Boolean = false)
    data class ProtoSquare(var X: Int, var Y: Int)

    val ocuGrid = MutableList(4) { MutableList(4) { OcuSquare() } }

    fun occupy(x: Int, y: Int) {
        ocuGrid[x][y].occupied = true
    }

    fun unoccupy(x: Int, y: Int) {
        ocuGrid[x][y].occupied = false
    }

    fun encrypt(x: Int, y: Int): Int {
        var power = 1
        repeat(x) {
            power *= 2
        }
        val z = power + 5 * y
        return z
    }

    fun startSquare(): ProtoSquare {
        while (true) {
            val x = Random.nextInt(0, 4)
            val y = Random.nextInt(0, 4)
            if (!ocuGrid[x][y].occupied) {
                occupy(x, y)
                val returner = ProtoSquare(X = x, Y = y)
                return returner
            }
        }
    }

    fun startValue(): Int {
        val value = Random.nextInt(1, 100)
        if (value <= spawnOneChance) {
            return spawnOne.toInt()
        } else if (value >= spawnTwoChance)
            return spawnTwo.toInt()
        return -1
    }

    data class Square(var x: Int, var y: Int, var value: Int = startValue())

    val squareGrid = MutableList(4) { MutableList(4) { Square(-1, -1, -1) } }
    fun makeSquare() {
        val proto = startSquare()
        val placeSquare = Square(x = proto.X, y = proto.Y)
        squareGrid[placeSquare.x][placeSquare.y] = placeSquare
    }
    fun removeSquare() {
        val xInput = readln().toInt()
        val yInput = readln().toInt()
        ocuGrid[xInput][yInput] = OcuSquare(false, false)
        squareGrid[xInput][yInput] = Square(-1,-1,-1)
    }
    fun valueSquare() {
        val xInput = readln().toInt()
        val yInput = readln().toInt()
        val valueInput = readln().toInt()
        squareGrid[xInput][yInput].value = valueInput
    }

    fun refreshOccupied() {
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                when (squareGrid[x][y].value) {
                    -1 -> unoccupy(x,y)
                    else -> occupy(x,y)
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
                ocuGrid[x][y].combined = false
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
                squareGrid[x][y] = Square(-1,-1,-1)
                ocuGrid[x][y] = OcuSquare(false, false)
                ++x
            }
            ++y
            x = 0
        }
    }

    fun combine(x1: Int, y1: Int, x2: Int, y2: Int) {
        squareGrid[x1][y1].value *= 2
        unoccupy(x2, y2)
        squareGrid[x2][y2] = Square(-1, -1, -1)
        clearCombinations()
        ocuGrid[x1][y1].combined = true
        //x1y1 = square that increases
        //x2y2 = square that goes away
    }

    fun squareUp(x: Int, y: Int, up: Int) {
        if (squareGrid[x][y].value != -1) {
            unoccupy(x, y)
            occupy(x, y + up)
            val newValue = squareGrid[x][y].value
            squareGrid[x][y + up] = Square(x, y + up, newValue)
            squareGrid[x][y] = Square(-1, -1, -1)
        }
    }
    fun squareDown(x: Int, y: Int, down: Int) {
        if (squareGrid[x][y].value != -1) {
            unoccupy(x, y)
            occupy(x, y - down)
            val newValue = squareGrid[x][y].value
            squareGrid[x][y - down] = Square(x, y - down, newValue)
            squareGrid[x][y] = Square(-1, -1, -1)
        }
    }
    fun squareRight(x: Int, y: Int, right: Int) {
        if (squareGrid[x][y].value != -1) {
            unoccupy(x,y)
            occupy(x+right,y)
            val newValue = squareGrid[x][y].value
            squareGrid[x+right][y] = Square(x+right, y, newValue)
            squareGrid[x][y] = Square(-1,-1,-1)
        }
    }
    fun squareLeft(x: Int, y: Int, left: Int) {
        if (squareGrid[x][y].value != -1) {
            unoccupy(x,y)
            occupy(x-left,y)
            val newValue = squareGrid[x][y].value
            squareGrid[x-left][y] = Square(x-left, y, newValue)
            squareGrid[x][y] = Square(-1,-1,-1)
        }
    }

    fun metaUp() {
        clearCombinations()
        refreshOccupied()
        var x = 0
        var y = 3
        repeat(4) {
            repeat(4) {
                if (squareGrid[x][y].value != -1) {
                    if (y == 2) {
                        if (!ocuGrid[x][y + 1].occupied) {
                            squareUp(x, y, 1)
                        } else if (squareGrid[x][y + 1].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x][y + 1].combined) {
                                combine(x, y + 1, x, y)
                            }
                        }
                        //move 0
                    } else if (y == 1) {
                        if (!ocuGrid[x][y + 1].occupied) {
                            if (!ocuGrid[x][y + 2].occupied) {
                                squareUp(x, y, 2)
                            } else if (squareGrid[x][y + 2].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x][y + 2].combined) {
                                    combine(x, y + 2, x, y)
                                }
                                squareUp(x, y, 1)
                            } else {
                                squareUp(x, y, 1)
                            }
                        }
                        else if (squareGrid[x][y+1].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x][y+1].combined) {
                                combine(x,y+1,x,y)
                            }
                        }
                        //move 0
                    } else if (y == 0) {
                        if (!ocuGrid[x][y + 1].occupied) {
                            if (!ocuGrid[x][y + 2].occupied) {
                                if (!ocuGrid[x][y + 3].occupied) {
                                    squareUp(x, y, 3)
                                } else if (squareGrid[x][y + 3].value == squareGrid[x][y].value) {
                                    if (!ocuGrid[x][y + 3].combined) {
                                        combine(x, y + 3, x, y)
                                    }
                                    squareUp(x, y, 2)
                                } else {
                                    squareUp(x, y, 2)
                                }
                            } else if (squareGrid[x][y + 2].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x][y + 2].combined) {
                                    combine(x, y + 2, x, y)
                                }
                                squareUp(x, y, 1)
                            } else {
                                squareUp(x, y, 1)
                            }
                        }
                        else if (squareGrid[x][y+1].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x][y+1].combined) {
                                combine(x,y+1,x,y)
                            }
                        }
                        //move 0
                    }
                }
                --y
            }
            ++x
            y = 3
        }
        clearCombinations()
        refreshOccupied()
    }
    fun metaDown() {
        clearCombinations()
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                if (squareGrid[x][y].value != -1) {
                    if (y == 1) {
                        if (!ocuGrid[x][y - 1].occupied) {
                            squareDown(x, y, 1)
                        } else if (squareGrid[x][y - 1].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x][y - 1].combined) {
                                combine(x, y - 1, x, y)
                            }
                        }
                        //move 0
                    } else if (y == 2) {
                        if (!ocuGrid[x][y - 1].occupied) {
                            if (!ocuGrid[x][y - 2].occupied) {
                                squareDown(x, y, 2)
                            } else if (squareGrid[x][y - 2].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x][y - 2].combined) {
                                    combine(x, y - 2, x, y)
                                }
                                squareDown(x, y, 1)
                            } else {
                                squareDown(x, y, 1)
                            }
                        }
                        else if (squareGrid[x][y - 1].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x][y - 1].combined) {
                                combine(x, y - 1, x, y)
                            }
                        }
                        //move 0
                    } else if (y == 3) {
                        if (!ocuGrid[x][y - 1].occupied) {
                            if (!ocuGrid[x][y - 2].occupied) {
                                if (!ocuGrid[x][y - 3].occupied) {
                                    squareDown(x, y, 3)
                                } else if (squareGrid[x][y - 3].value == squareGrid[x][y].value) {
                                    if (!ocuGrid[x][y - 3].combined) {
                                        combine(x, y - 3, x, y)
                                    }
                                    squareDown(x, y, 2)
                                } else {
                                    squareDown(x, y, 2)
                                }
                            } else if (squareGrid[x][y - 2].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x][y - 2].combined) {
                                    combine(x, y - 2, x, y)
                                }
                                squareDown(x, y, 1)
                            } else {
                                squareDown(x, y, 1)
                            }
                        }
                        else if (squareGrid[x][y - 1].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x][y - 1].combined) {
                                combine(x, y - 1, x, y)
                            }
                        }
                        //move 0
                    }
                }
                ++y
            }
            ++x
            y = 0
        }
        clearCombinations()
    }
    fun metaRight() {
        clearCombinations()
        refreshOccupied()
        var x = 3
        var y = 0
        repeat(4) {
            repeat(4) {
                if (squareGrid[x][y].value != -1) {
                    if (x == 2) {
                        if (!ocuGrid[x+1][y].occupied) {
                            squareRight(x, y, 1)
                        } else if (squareGrid[x+1][y].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x+1][y].combined) {
                                combine(x+1, y, x, y)
                            }
                        }
                        //move 0
                    } else if (x == 1) {
                        if (!ocuGrid[x+1][y].occupied) {
                            if (!ocuGrid[x+2][y].occupied) {
                                squareRight(x, y, 2)
                            } else if (squareGrid[x+2][y].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x+2][y].combined) {
                                    combine(x+2, y, x, y)
                                }
                                squareRight(x, y, 1)
                            } else {
                                squareRight(x, y, 1)
                            }
                        }
                        else if (squareGrid[x+1][y].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x+1][y].combined) {
                                combine(x+1,y,x,y)
                            }
                        }
                        //move 0
                    } else if (x == 0) {
                        if (!ocuGrid[x+1][y].occupied) {
                            if (!ocuGrid[x+2][y].occupied) {
                                if (!ocuGrid[x+3][y].occupied) {
                                    squareRight(x, y, 3)
                                } else if (squareGrid[x+3][y].value == squareGrid[x][y].value) {
                                    if (!ocuGrid[x+3][y].combined) {
                                        combine(x+3, y, x, y)
                                    }
                                    squareRight(x, y, 2)
                                } else {
                                    squareRight(x, y, 2)
                                }
                            }
                            else if (squareGrid[x+2][y].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x+2][y].combined) {
                                    combine(x+2, y, x, y)
                                }
                                squareRight(x, y, 1)
                            } else {
                                squareRight(x, y, 1)
                            }
                        }
                        else if (squareGrid[x+1][y].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x+1][y].combined) {
                                combine(x+1,y,x,y)
                            }
                        }
                        //move 0
                    }
                }
                --x
            }
            ++y
            x = 3
        }
        clearCombinations()
        refreshOccupied()
    }
    fun metaLeft() {
        clearCombinations()
        refreshOccupied()
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                if (squareGrid[x][y].value != -1) {
                    if (x == 1) {
                        if (!ocuGrid[x-1][y].occupied) {
                            squareLeft(x, y, 1)
                        } else if (squareGrid[x-1][y].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x-1][y].combined) {
                                combine(x-1, y, x, y)
                            }
                        }
                        //move 0
                    } else if (x == 2) {
                        if (!ocuGrid[x-1][y].occupied) {
                            if (!ocuGrid[x-2][y].occupied) {
                                squareLeft(x, y, 2)
                            } else if (squareGrid[x-2][y].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x-2][y].combined) {
                                    combine(x-2, y, x, y)
                                }
                                squareLeft(x, y, 1)
                            } else {
                                squareLeft(x, y, 1)
                            }
                        }
                        else if (squareGrid[x-1][y].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x-1][y].combined) {
                                combine(x-1,y,x,y)
                            }
                        }
                        //move 0
                    } else if (x == 3) {
                        if (!ocuGrid[x-1][y].occupied) {
                            if (!ocuGrid[x-2][y].occupied) {
                                if (!ocuGrid[x-3][y].occupied) {
                                    squareLeft(x, y, 3)
                                } else if (squareGrid[x-3][y].value == squareGrid[x][y].value) {
                                    if (!ocuGrid[x-3][y].combined) {
                                        combine(x-3, y, x, y)
                                    }
                                    squareLeft(x, y, 2)
                                } else {
                                    squareLeft(x, y, 2)
                                }
                            }
                            else if (squareGrid[x-2][y].value == squareGrid[x][y].value) {
                                if (!ocuGrid[x-2][y].combined) {
                                    combine(x-2, y, x, y)
                                }
                                squareLeft(x, y, 1)
                            } else {
                                squareLeft(x, y, 1)
                            }
                        }
                        else if (squareGrid[x-1][y].value == squareGrid[x][y].value) {
                            if (!ocuGrid[x-1][y].combined) {
                                combine(x-1,y,x,y)
                            }
                        }
                        //move 0
                    }
                }
                ++x
            }
            ++y
            x = 0
        }
        clearCombinations()
        refreshOccupied()
    }

    fun above(x: Int, y: Int, value: Int): Boolean {
        if (ocuGrid[x][y+value].occupied) {
            return true
        }
        return false
    }
    fun below(x: Int, y: Int, value: Int): Boolean {
        if (ocuGrid[x][y-value].occupied) {
            return true
        }
        return false
    }

    fun realUp() {
        clearCombinations()
        refreshOccupied()
        var x = 0
        var y = 3
        repeat(4) {
            repeat(4) {
                if (y<3&&!above(x,y,1)) {
                    if (y<2&&!above(x,y,2)) {
                        if (y<1&&!above(x,y,3)) {
                            squareUp(x,y,3)
                        }
                        else if(y<1&&above(x,y,3)) {
                            if ((squareGrid[x][y+3].value == squareGrid[x][y].value) && !ocuGrid[x][y+3].combined) {
                                combine(x,y+3,x,y)
                            }
                            if ((squareGrid[x][y+3].value != squareGrid[x][y].value) || ocuGrid[x][y+3].combined) {
                                squareUp(x,y,2)
                            }
                        }
                    }
                    else if (y<2&&above(x,y,2)) {
                        if ((squareGrid[x][y+2].value == squareGrid[x][y].value) && !ocuGrid[x][y+2].occupied) {
                            combine(x,y+2,x,y)
                        }
                        if ((squareGrid[x][y+2].value != squareGrid[x][y].value) || ocuGrid[x][y+2].combined) {
                            squareUp(x,y,1)
                        }
                    }
                }
                else if (y<3&&above(x,y,1)) {
                    if ((squareGrid[x][y+1].value == squareGrid[x][y].value) && !ocuGrid[x][y+1].occupied) {
                        combine(x,y+1,x,y)
                    }
                    if ((squareGrid[x][y+1].value != squareGrid[x][y].value) || ocuGrid[x][y+1].combined) {
                        //move 0
                    }
                }
                --y
            }
            ++x
            y = 3
        }
    }
    fun realDown() {
        clearCombinations()
        refreshOccupied()
        var x = 0
        var y = 0
        repeat(4) {
            repeat(4) {
                if (y>0&&!below(x,y,1)) {
                    if (y>1&&!below(x,y,2)) {
                        if (y>2&&!below(x,y,3)) {
                            squareUp(x,y,3)
                        }
                        else if(y>2&&below(x,y,3)) {
                            if ((squareGrid[x][y-3].value == squareGrid[x][y].value) && !ocuGrid[x][y-3].combined) {
                                combine(x,y-3,x,y)
                            }
                            if ((squareGrid[x][y-3].value != squareGrid[x][y].value) || ocuGrid[x][y-3].combined) {
                                squareUp(x,y,2)
                            }
                        }
                    }
                    else if (y>1&&below(x,y,2)) {
                        if ((squareGrid[x][y-2].value == squareGrid[x][y].value) && !ocuGrid[x][y-2].occupied) {
                            combine(x,y-2,x,y)
                        }
                        if ((squareGrid[x][y-2].value != squareGrid[x][y].value) || ocuGrid[x][y-2].combined) {
                            squareUp(x,y,1)
                        }
                    }
                }
                else if (y>1&&below(x,y,1)) {
                    if ((squareGrid[x][y-1].value == squareGrid[x][y].value) && !ocuGrid[x][y-1].occupied) {
                        combine(x,y-1,x,y)
                    }
                    if ((squareGrid[x][y-1].value != squareGrid[x][y].value) || ocuGrid[x][y-1].combined) {
                        //move 0
                    }
                }
                ++y
            }
            ++x
            y = 0
        }
    }

    fun map() {
        var x = 0
        var y = 3
        repeat(4) {
            print("\n")
            repeat(4) {
                if (squareGrid[x][y].value != -1) {
                    print(squareGrid[x][y].value)
                } else print("-")git i
                repeat(5-abs(squareGrid[x][y].value).toString().length) {
                    print(" ")
                }
                ++x
            }
            --y
            x = 0
        }
    }
    print("spawn: ")
    val spawning = readln().toBoolean()
    while (true) {
        if (spawning) {makeSquare()}
        map()
        val yo = readln()
        if (yo == "w" || yo == "up") {metaUp()}
        if (yo == "s" || yo == "down") {metaDown()}
        if (yo == "d" || yo == "right") {metaRight()}
        if (yo == "a" || yo == "left") {metaLeft()}
        if (yo == "m" || yo == "make") {makeSquare()}
        if (yo == "r" || yo == "remove") {removeSquare()}
        if (yo == "v" || yo == "value") {valueSquare()}
        if (yo == "clear") {clearGrid()}
        if (yo == "stop") {break}
    }
}