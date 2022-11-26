package pt.isec.amov.tp1

class Game {
    var board : ArrayList<String> = ArrayList()
    val GAME_TIME = 60000L
    var level : Int = 1
    var points : Int = 0

    constructor(){
        generateBoard()
    }

    fun generateBoard() {
        for (i in 0..4) {
            for (j in 0..4) {
                if (i % 2 == 0 && j % 2 == 0) {
                    board.add((0..9).random().toString())
                } else if (i % 2 == 1 && j % 2 == 1) {
                    board.add(" ")
                } else {
                    board.add(
                        when ((0..3).random()) {
                            0 -> "+"
                            1 -> "-"
                            2 -> "x"
                            else -> "/"
                        }
                    )
                }
            }
        }
    }
}