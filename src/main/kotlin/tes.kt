import com.google.gson.Gson
import java.io.File

val gson = Gson()
val data = gson.fromJson(File("/Users/fsfaysalcse/Documents/ProtocallBuf/src/main/kotlin/quran.json").reader(), Quran::class.java)

data class Quran(val verses: List<Verse>)
data class Verse(val page_number: Int, val words: List<Word>)
data class Word(val line_number: Int, val text_uthmani: String, val code_v1: String)

fun versesInPage(pageNo: Int): List<Verse> {
    return data.verses.filter { it.page_number == pageNo }
}

fun allLines(pageNo: Int): List<Pair<Int, List<String>>> {
    var curLine = -1
    var line = mutableListOf<String>()
    val lines = mutableListOf<Pair<Int, List<String>>>()
    for (verse in versesInPage(pageNo)) {
        for (word in verse.words) {
            if (word.line_number != curLine) {
                if (line.isNotEmpty()) {
                    lines.add(curLine to line)
                }
                curLine = word.line_number
                line = mutableListOf()
            }
            line.add(word.code_v1)
        }
    }
    if (line.isNotEmpty()) {
        lines.add(curLine to line)
    }
    return lines
}

fun main(args: Array<String>) {
    val pageNo = 3
    val dataLines = allLines(pageNo)
    val refLines = (1..15)

    var dataLineIdx = 0

    for (lineA in refLines) {
        if (lineA + 2 == dataLines[dataLineIdx].first) {
            println("surah name")
        } else if (lineA + 1 == dataLines[dataLineIdx].first) {
            println("basmallah")
        } else if (lineA == dataLines[dataLineIdx].first) {
            println(dataLines[dataLineIdx].second.joinToString(" "))
            dataLineIdx++
        }
    }
}


