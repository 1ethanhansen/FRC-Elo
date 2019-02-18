import kotlin.math.pow

//Binary search tree class straight from Baeldung with Teams instead of keys
class Node (var key: Team,
            var left: Node? = null,
            var right: Node? = null)
{
    fun find(value: Int): Node? = when {
        this.key.number > value -> left?.find(value)
        this.key.number < value -> right?.find(value)
        else -> this
    }

    fun insert(value: Team) {
        if (value.number > this.key.number) {
            if (this.right == null) {
                this.right = Node(value)
            } else {
                this.right?.insert(value)
            }
        } else if (value.number < this.key.number) {
            if (this.left == null) {
                this.left = Node(value)
            } else {
                this.left?.insert(value)
            }
        }
    }
}

//simple class with team number, name, and elo rating
class Team (var number: Int,
            var name: String,
            var rating: Double = 1000.0)

fun main() {
    //Always start with us as the root node
    val root = Node(Team(4043, "NerdHerd", 1300.0))
    val redAlliance: Array<Int> = arrayOf(0, 1, 2)
    val blueAlliance: Array<Int> = arrayOf(0, 1, 2)

    //Constantly runs
    while (true) {
        //in a match there are six teams
        for (i: Int in 0..5) {
            //finding which alliance color and station
            val driverStationInt = i % 3 + 1
            val allianceInt = i / 3
            print("Enter the team number in ${if(allianceInt == 0) "RED" else "BLUE"} " +
                    "alliance station $driverStationInt : ")

            //PRS
            val inputStr = readLine()
            if (inputStr == null) {
                println("Hey that's null idiot")
                return
            }
            val inputNumber = inputStr.toInt()
            if (allianceInt == 0) {
                redAlliance[driverStationInt - 1] = inputNumber
            } else {
                blueAlliance[driverStationInt - 1] = inputNumber
            }


            //try to find team, otherwise add to BST
            if (root.find(inputNumber) != null) {
                val foundName = root.find(inputNumber)?.key!!.name
                val foundRating = root.find(inputNumber)?.key!!.rating
                println("We have team $inputNumber, team $foundName with an elo rating of $foundRating")
            } else {
                print("Enter the team name: ")
                val inputName: String = readLine()!!

                val newTeam = Team(inputNumber, inputName)

                root.insert(newTeam)
            }
        }

        //Find red and blue alliance average elo ratings
        var redAllianceAverage = 0.0
        var blueAllianceAverage = 0.0
        redAlliance.forEach { redAllianceAverage += root.find(it)?.key!!.rating }
        blueAlliance.forEach { blueAllianceAverage += root.find(it)?.key!!.rating }
        redAllianceAverage /= 3
        blueAllianceAverage /= 3

        //Find chances of winning
        var redChance = 1.0 / (1 + 10.0.pow((blueAllianceAverage - redAllianceAverage) / 400.0))
        var blueChance = 1 - redChance
        println("Red alliance has a ${redChance * 100}% chance of winning, giving blue alliance a " +
                "${blueChance * 100 } chance of winning")

        //get actual match info
        println("Enter R for red alliance winning or B for blue alliance winning")
        val winningStr = readLine()?.toUpperCase()
        val redScore = if(winningStr == "R") 1 else 0
        val blueScore = if(winningStr == "B") 1 else 0

        //update elo ratings
        val newRedRating = redAllianceAverage + 32 * (redScore - redChance)
        val newBlueRating = blueAllianceAverage + 32 * (blueScore - blueChance)

        redAlliance.forEach { root.find(it)?.key!!.rating = (root.find(it)?.key!!.rating + newRedRating) / 2 }
        blueAlliance.forEach { root.find(it)?.key!!.rating = (root.find(it)?.key!!.rating + newBlueRating) / 2 }
    }
}