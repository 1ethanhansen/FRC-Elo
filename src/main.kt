import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt


//simple class with team number, name, and elo rating
class Team (var number: Int,
            var name: String,
            var rating: Double = 1000.0)

const val defaultFilePath = "C:\\Users\\Ethan Hansen\\Documents\\GitHub\\FRC-Elo\\elo.txt"

val emptyTeam = Team(0, "0")

val redAlliance = Array(3){emptyTeam}
val blueAlliance = Array(3){emptyTeam}
val teamsByRank = mutableListOf<Team>()

fun main() {
    //Constantly runs
    while (true) {
        print("What do you want to do? run (m)atch (d)isplay ratings\n(l)oad from file (s)ave to file ")
        val commandStr = readLine()
        when (commandStr?.toLowerCase()) {
            "m" -> runMatch()
            "d" -> printRankings()
            "l" -> loadFromFile()
            "s" -> saveToFile()
        }
    }
}

fun runMatch() {
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
            println("Hey that's null idiot")    //Not nice but idc
            return
        }
        val inputNumber = inputStr.toInt()

        //try to find team, otherwise add to list
        if (teamsByRank.find {it.number == inputNumber} != null) {
            val foundName = teamsByRank.find {it.number == inputNumber}!!.name
            val foundRating = teamsByRank.find {it.number == inputNumber}!!.rating
            println("We have team $inputNumber, team $foundName with an elo rating of ${foundRating.roundToInt()}")
        } else {
            print("Enter the team name: ")
            val inputName: String = readLine()!!

            val newTeam = Team(inputNumber, inputName)

            teamsByRank.add(0, newTeam)
        }

        //Keep track of who is on which alliance
        if (allianceInt == 0) {
            redAlliance[driverStationInt - 1] = teamsByRank.find {it.number == inputNumber}!!
        } else {
            blueAlliance[driverStationInt - 1] = teamsByRank.find {it.number == inputNumber}!!
        }
    }

    //Find red and blue alliance average elo ratings
    var redAllianceAverage = 0.0
    var blueAllianceAverage = 0.0
    redAlliance.forEach { redAllianceAverage += it.rating }
    blueAlliance.forEach { blueAllianceAverage += it.rating }
    redAllianceAverage /= 3
    blueAllianceAverage /= 3

    //Find chances of winning
    val redChance = 1.0 / (1 + 10.0.pow((blueAllianceAverage - redAllianceAverage) / 400.0))
    val blueChance = 1 - redChance
    println("Red alliance has a ${redChance * 100}% chance of winning, giving blue alliance a " +
            "${blueChance * 100 }% chance of winning")

    //get actual match info
    println("Enter R for red alliance winning or B for blue alliance winning")
    val winningStr = readLine()?.toUpperCase()
    val redScore = if(winningStr == "R") 1 else 0
    val blueScore = if(winningStr == "B") 1 else 0

    //update elo ratings
    val newRedRating = redAllianceAverage + 64 * (redScore - redChance)
    val newBlueRating = blueAllianceAverage + 64 * (blueScore - blueChance)
    redAlliance.forEach { it.rating = (it.rating + newRedRating) / 2 }
    blueAlliance.forEach { it.rating = (it.rating + newBlueRating) / 2 }
}

fun printRankings() {
    //Sort teams by rating
    teamsByRank.sortByDescending { it.rating }

    teamsByRank.forEach { println("${it.rating.roundToInt().toString().padEnd(5, '-')}-" +
            "-${it.name.padEnd(20, '-')}" +
            "-${it.number.toString().padEnd(5, '-')}") }

    saveToFile()
}

fun loadFromFile() {
    var readInputs: List<String> = File(defaultFilePath).readLines()

    for (i in readInputs) {
        val m_readDataList = i.split('\t')

        val readTeam = Team(m_readDataList[0].toInt(), m_readDataList[1], m_readDataList[2].toDouble())

        teamsByRank.add(0, readTeam)
    }
}

fun saveToFile() {
    File(defaultFilePath).printWriter().use { out -> teamsByRank.forEach { out.println(
        "${it.number}\t${it.name}\t${it.rating}") }}
}
