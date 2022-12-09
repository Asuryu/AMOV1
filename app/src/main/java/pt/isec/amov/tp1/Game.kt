package pt.isec.amov.tp1

import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.core.view.children
import pt.isec.amov.tp1.databinding.ActivityGameBinding
import kotlin.random.Random

class Game : java.io.Serializable{

    companion object {
        private const val serialVersionUID = 1L
    }

    // selecionar linha errada em landscape e se rodar para portrait a linha ainda fica selecionada (FIX)

    var board : ArrayList<ArrayList<String>> = ArrayList()
    var expressions : HashMap<String, Int> = HashMap()

    val GAME_TIME = 1000L
    var level : Int = 0
    var points : Int = 0
    var minRange : Int = 1
    var maxRange : Int = 10
    var operators : ArrayList<String> = ArrayList( listOf("+") )
    var correctAnswersNeeded : Int = 1
    var correctAnswers : Int = 0

    var timerColor : ArrayList<String> = ArrayList( listOf("#ff931c", "#03befc", "#926ad4", "#8c3f75", "#3f8c70") )
    lateinit var timer : CountDownTimer
    var timeLeft : Long = GAME_TIME

    var randomGenerator = Random(System.currentTimeMillis())

    var binding : ActivityGameBinding
    var context : GameActivity

    constructor(context: GameActivity, binding: ActivityGameBinding) {
        this.binding = binding
        this.context = context
        nextLevel(false)
        generateBoard()
        timer = getTimerObject(GAME_TIME)
        timer.start()
    }

    // generate copy constructor
    constructor(game: Game, context: GameActivity, binding: ActivityGameBinding, selectedPieces: ArrayList<Int>) {
        this.board = game.board
        this.expressions = game.expressions
        this.level = game.level
        this.points = game.points
        this.minRange = game.minRange
        this.maxRange = game.maxRange
        this.operators = game.operators
        this.correctAnswersNeeded = game.correctAnswersNeeded
        this.correctAnswers = game.correctAnswers
        this.timer = game.timer
        this.timeLeft = game.timeLeft
        this.timerColor = game.timerColor
        this.randomGenerator = game.randomGenerator
        this.binding = binding
        this.context = context

        timer = getTimerObject(timeLeft)
        timer.start()
    }

    fun getTimerObject (time: Long) : CountDownTimer {
        return object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = (millisUntilFinished / 1000).toString()
                timeLeft = millisUntilFinished
            }

            override fun onFinish() {
                binding.timer.text = "0"
                timeLeft = 0
                context.showEndGameScreen()
            }
        }
    }

    fun nextLevel(generateBoard : Boolean = true) {
        level++
        when(level){
            1 -> {
                minRange = 1
                maxRange = 10
                operators = ArrayList( listOf("+") )
                correctAnswersNeeded = 1
            }
            2 -> {
                minRange = 1
                maxRange = 10
                operators = arrayListOf("+", "-")
                correctAnswersNeeded = 2
            }
            3 -> {
                minRange = 1
                maxRange = 10
                operators = arrayListOf("+", "-", "*")
                correctAnswersNeeded = 3
            }
            4 -> {
                minRange = 10
                maxRange = 99
                operators = arrayListOf("+", "-", "*", "/")
                correctAnswersNeeded = 5
            }
            5 -> {
                minRange = 100
                maxRange = 999
                operators = arrayListOf("+", "-", "*", "/")
                correctAnswersNeeded = 6
            }
            6 -> {
                minRange = 100
                maxRange = 999
                operators = arrayListOf("+", "-", "*", "/")
                correctAnswersNeeded = 6
                level = 5
            }
        }
        context.selectedPieces.clear()

        Log.i("Asuryu", "Level: $level")
        context.binding.level.text = context.getString(R.string.nivel_placeholder, level)
        context.binding.timer.background.setTint(android.graphics.Color.parseColor(timerColor[level-1]))
        correctAnswers = 0
        if (generateBoard) generateBoard()
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
        expressions.clear()

        for(i in 0..4){
            var row : ArrayList<String> = ArrayList()
            for(j in 0..4){
                if(i % 2 == 0){
                    if(j % 2 == 0){
                        row.add(getRandomNumber().toString())
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

        updateBoard()

        parseBoard()
        Log.i("Asuryu", expressions.toString())
    }

    fun updateBoard(){
        for(i in 0..4){
            for(j in 0..4){
                val id = context.resources.getIdentifier("piece${i}_${j}", "id", context.packageName)
                val cell = context.findViewById<TextView>(id)
                cell.text = board[i][j]
                cell.alpha = 1f
            }
        }
    }

    // function to get a random operator
    fun getRandomOperator() : String{
        return operators[randomGenerator.nextInt(operators.size)]
    }

    fun getRandomNumber() : Int{
        return randomGenerator.nextInt(minRange, maxRange)
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
        var result = 0
        var operator = '+'
        var number = ""
        for(i in 0 until expression.length){
            if(expression[i] == '+' || expression[i] == '-' || expression[i] == '*' || expression[i] == '/'){
                when(operator){
                    '+' -> result += number.toInt()
                    '-' -> result -= number.toInt()
                    '*' -> result *= number.toInt()
                    '/' -> result /= number.toInt()
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
            '/' -> result /= number.toInt()
        }
        return result
    }

    // check if expression is the greatest in the map
    // if it is, give 2 points and return true
    // if it is the second greatest, give 1 point and return true
    // if it is not the greatest, return false
    fun checkExpression(expression : String) : Boolean{
        var result = false
        var max = 0
        var secondMax = 0
        for((key, value) in expressions){
            if(value > max){
                secondMax = max
                max = value
            } else if(value > secondMax){
                secondMax = value
            }
        }
        if(expressions[expression] == max){
            points += 2
            binding.playerPoints.text = context.getString(R.string.points_placeholder, points)
            expressions.remove(expression)
            correctAnswers++
            context.addedPiecesRound.clear()
            checkLevel()
            return true
        } else if(expressions[expression] == secondMax){
            points += 1
            binding.playerPoints.text = context.getString(R.string.points_placeholder, points)
            expressions.remove(expression)

            for(i in context.addedPiecesRound){
                i.alpha = 0.2f
            }
            context.addedPiecesRound.clear()

            correctAnswers++
            checkLevel()

            return true
        } else {

            for(i in 0..4){
                for(j in 0..4){
                    val id = context.resources.getIdentifier("piece${i}_${j}", "id", context.packageName)
                    val cell = context.findViewById<TextView>(id)
                    if(cell.text == expression){
                        cell.alpha = 1f
                    }
                }
            }
            context.selectedPieces.clear()
            context.addedPiecesRound.clear()

            generateBoard()
            return false
        }
    }

    fun checkLevel(){
        if(correctAnswers == correctAnswersNeeded){
            nextLevel()
        }
    }

}