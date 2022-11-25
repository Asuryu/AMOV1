package pt.isec.amov.tp1

class Game {
    var board : ArrayList<String> = ArrayList()
    var level : Int = 0

    constructor(){
        generateBoard()
    }

    fun generateBoard() {
//        boardNumbers.clear()
//        boardSigns.clear()
//        for (i in 0..8) {
//            boardNumbers.add((0..9).random())
//        }
//        for (i in 0..3) {
//            boardSigns.add(when ((0..3).random()) {
//                0 -> "+"
//                1 -> "-"
//                2 -> "*"
//                else -> "/"
//            })
//        }

        // generate board like this (variable board)
        // 0 x 0 x 0
        // x   x   x
        // 0 x 0 x 0
        // x   x   x
        // 0 x 0 x 0
        // where 0 is a number and x is a sign

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