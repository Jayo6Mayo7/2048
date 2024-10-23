import kotlin.random.Random
fun main() {
    data class MetaSquare(var x: Int, var y: Int, var value: Int, var occupied: Boolean = false, var combined: Boolean = false)
    val metaGrid = MutableList(4) { MutableList(4) { MetaSquare(x = -1, y = -1, value = -1) } }
    fun makeSquare() {
        while (true) {
            val proto = MetaSquare(x = Random.nextInt(0, 4), y = Random.nextInt(0, 4), value = -1)
            if (!metaGrid[proto.x][proto.y].occupied) {
                metaGrid[proto.x][proto.y] = MetaSquare(x = proto.x, y = proto.y, value = if (Random.nextInt(1, 10) == 0) {4} else {2}, occupied = true)
                break
            }
        }
    }
    var change = true
    fun omegaMove(xDif: Int, yDif: Int) {
        change = false
        var yVar = 0
        repeat(4) {
            var xVar = 0
            repeat(4) {
                when (metaGrid[xVar][yVar].value) {
                    -1 -> {metaGrid[xVar][yVar].occupied = false; metaGrid[xVar][yVar].combined = false; ++xVar}
                    else -> {metaGrid[xVar][yVar].occupied = true; metaGrid[xVar][yVar].combined = false; ++xVar}
                }
            }
            ++yVar
        }
        var x = -1
        var y = -1
        when (xDif) {
            -1 -> { x = 0; y = 0 }
            1 -> { x = 3; y = 3 }
        }
        when (yDif) {
            -1 -> { y = 0; x = 0 }
            1 -> { y = 3; x = 3 }
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
                                    if (xDif != 0 && yDif != 0) {
                                        metaGrid[metaX][metaY] = MetaSquare(-1, -1, -1, occupied = false)
                                        metaGrid[metaX + xDif][metaY + yDif] = MetaSquare(metaX + xDif, metaY + yDif, metaGrid[metaX][metaY].value, occupied = true)
                                    }
                                    metaGrid[metaX + xDif][metaY + yDif] = metaGrid[metaX][metaY]
                                    metaGrid[metaX][metaY] = MetaSquare(metaX, metaY, -1, occupied = false, combined = false)
                                    metaX += xDif; metaY += yDif
                                    change = true
                                } else if (metaGrid[metaX + xDif][metaY + yDif].value == metaGrid[metaX][metaY].value && !metaGrid[metaX + xDif][metaY + yDif].combined) {
                                    metaGrid[metaX + xDif][metaY + yDif] = MetaSquare(metaGrid[metaX + xDif][metaY + yDif].x, metaGrid[metaX + xDif][metaY + yDif].y, metaGrid[metaX + xDif][metaY + yDif].value * 2, combined = true, occupied = true)
                                    metaGrid[metaX][metaY] = MetaSquare(-1,-1,-1, occupied = false)
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
                -1 -> { ++x; y = 0 }
                1 -> { --x; y = 3 }
            }
            when (xDif) {
                -1 -> { ++y; x = 0 }
                1 -> { --y; x = 3 }
            }
        }
    }
    fun map() {
        var y = 3
        repeat(4) {
            var x = 0
            print("\n")
            repeat(4) {
                if (metaGrid[x][y].value != -1) {
                    print(metaGrid[x][y].value)
                    repeat(5 - metaGrid[x][y].value.toString().length) { print(" ") }
                } else print("-    ")
                ++x
            }
            --y
        }
    }
    makeSquare()
    while (true) {
        if (change) {map()}
        when (readln()) {
            "w", "up" ->    { omegaMove(0, 1);  if (change) { makeSquare() } }
            "s", "down" ->  { omegaMove(0, -1); if (change) { makeSquare() } }
            "d", "right" -> { omegaMove(1, 0);  if (change) { makeSquare() } }
            "a", "left" ->  { omegaMove(-1, 0); if (change) { makeSquare() } }
        }
    }
}