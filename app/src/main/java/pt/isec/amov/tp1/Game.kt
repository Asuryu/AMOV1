package pt.isec.amov.tp1

import android.util.Log
import kotlin.random.Random

class Game : java.io.Serializable{

    companion object {
        private const val serialVersionUID = 1L
    }

    var board : ArrayList<ArrayList<String>> = ArrayList()
    var expressions : HashMap<String, Int> = HashMap()
    val GAME_TIME = 10000L
    var level : Int = 1
    var points : Int = 0
    var randomGenerator = Random(System.currentTimeMillis())

    constructor(){
        generateBoard()
    }


    // function to generate a 5x5 board
    // the board should look like
    // 1 x 1 x 1
    // x . x . x
    // 1 x 1 x 1
    // x . x . x
    // 1 x 1 x 1
    // where 1 is a random number between 1 and 9
    // and x is a random operator between +, -, *
    // and . is a empty space " "
    fun generateBoard(){
        board.clear()
        for(i in 0..4){
            var row : ArrayList<String> = ArrayList()
            for(j in 0..4){
                if(i % 2 == 0){
                    if(j % 2 == 0){
                        row.add(randomGenerator.nextInt(1, 10).toString())
                    } else {
                        row.add(getRandomOperator())
                    }
                } else {
                    if(j % 2 == 0){
                        row.add(getRandomOperator())
                    } else {
                        row.add(" ")
                    }
                }
            }
            board.add(row)
        }
        parseBoard()
        Log.i("Asuryu", expressions.toString())
    }

    // function to get a random operator
    fun getRandomOperator() : String{
        var random = randomGenerator.nextInt(1, 4)
        when(random){
            1 -> return "+"
            2 -> return "-"
            3 -> return "*"
        }
        return "+"
    }

    fun parseBoard(){
        expressions.clear()
        for(i in 0..4){
            if(i % 2 == 0){
                var expression = ""
                for(j in 0..4){
                    if(j % 2 == 0){
                        expression += board[i][j]
                    } else {
                        expression += board[i][j]
                    }
                }
                expressions.put(expression, evaluateExpression(expression))
            }
        }

        for(i in 0..4){
            if(i % 2 == 0){
                var expression = ""
                for(j in 0..4){
                    if(j % 2 == 0){
                        expression += board[j][i]
                    } else {
                        expression += board[j][i]
                    }
                }
                expressions.put(expression, evaluateExpression(expression))
            }
        }

        for(i in 4 downTo 0){
            if(i % 2 == 0){
                var expression = ""
                for(j in 4 downTo 0){
                    if(j % 2 == 0){
                        expression += board[i][j]
                    } else {
                        expression += board[i][j]
                    }
                }
                expressions.put(expression, evaluateExpression(expression))
            }
        }

        for(i in 4 downTo 0){
            if(i % 2 == 0){
                var expression = ""
                for(j in 4 downTo 0){
                    if(j % 2 == 0){
                        expression += board[j][i]
                    } else {
                        expression += board[j][i]
                    }
                }
                expressions.put(expression, evaluateExpression(expression))
            }
        }
    }

    // function to evaluate an expression
    // the expression is a string with numbers and operators
    // example: 1+2*3
    // the function must return the result of the expression
    fun evaluateExpression(expression : String) : Int{
        Log.i("Asuryu", expression)
        var result = 0
        var operator = '+'
        var number = ""
        for(i in 0 until expression.length){
            if(expression[i] == '+' || expression[i] == '-' || expression[i] == '*'){
                when(operator){
                    '+' -> result += number.toInt()
                    '-' -> result -= number.toInt()
                    '*' -> result *= number.toInt()
                }
                operator = expression[i]
                number = ""
            } else {
                number += expression[i]
            }
        }
        when(operator){
            '+' -> result += number.toInt()
            '-' -> result -= number.toInt()
            '*' -> result *= number.toInt()
        }
        return result
    }


}