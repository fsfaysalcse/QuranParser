import Models.Verse
import Models.Verses
import Models.Word
import com.google.gson.Gson
import java.io.File
import java.lang.Thread.yield

/*
*
* */

//https://api.quran.com/api/v4/verses/by_page/1?language=en&words=false&fields=verse_key%2Cverse_id%2Cpage_number%2Clocation%2Ctext_uthmani%2Ccode_v1%2Cqpc_uthmani_hafs%26mushaf%3D2%26filter_page_words
//https://api.quran.com/api/v4/verses/by_page/1?language=en&words=false&fields=verse_key%2Cverse_id%2Cpage_number%2Clocation%2Ctext_uthmani%2Ccode_v2%2Cqpc_uthmani_hafs%26mushaf%3D2%26filter_page_words
//https://api.quran.com/api/v4/verses/by_page/1?language=en&words=false&fields=verse_key%2Cverse_id%2Cpage_number%2Clocation%2Ctext_uthmani%2Ccode_v2%2Cqpc_uthmani_hafs%26mushaf%3D2%26filter_page_words

class WordIterator(val verses: Iterator<Verse>) : Iterator<Word> {

    var words: Iterator<Word>? = null
    private lateinit var curVerse: Verse

    override fun hasNext(): Boolean {
        if (verses.hasNext() || words?.hasNext() == false) {
            return true
        }
        return false
    }

    override fun next(): Word {
        if (words == null || !words!!.hasNext()){
           curVerse = verses.next()
           words = curVerse.words.iterator()
       }
        return words!!.next()
    }
}

/*
class LineIterator(pageNumber: Int) : Iterator<String> {
    var words: Iterator<Word>
    var lastWord: String = ""
    var curLineNumber = 0

    init {
        val rawJson =
            readFileAsTextUsingInputStream("/home/faysal/Desktop/quranauto/quran.json")
        val data = Gson().fromJson(rawJson, Verses::class.java)
        val verses = data.verses.groupBy { it.page_number }["$pageNumber"]!!.iterator()
        words = WordIterator(verses)
    }

    override fun hasNext(): Boolean {
        return words.hasNext()
    }

    override fun next(): String {
        var curLine = lastWord
        while (true) {
            val word = words.next()
            if (word.line_number != curLineNumber) {
                lastWord = word.text_uthmani
                curLineNumber = word.line_number
                return curLine
            } else {
                curLine += " " + word.text_uthmani
            }
        }
    }
}


 */

fun main(args: Array<String>) {

    val pageNumber = 1
    val rawJson =
        readFileAsTextUsingInputStream("src/main/quran.json")
    val data = Gson().fromJson(rawJson, Verses::class.java)
    val verses = data.verses.groupBy { it.page_number }["$pageNumber"]!!.iterator()


    val words = WordIterator(verses)
    //print(words.hasNext())
    //print(words.next())


    while (words.hasNext()) {
        print(words.next())
        print("\n")
    }


    /*
    val lineIterator = LineIterator(600)
    while (lineIterator.hasNext()) {
        print(lineIterator.next())
    }

     */
}

/*fun main(args: Array<String>) {

    var lastChapterId = 0
    // for (index in 600..600) {
    val index = 604
    val rawJson =
        readFileAsTextUsingInputStream("/home/faysal/Desktop/quranauto/quran.json")
    val data = Gson().fromJson(rawJson, Verses::class.java)
    val quran = data.verses.groupBy { it.page_number }
    val list = quran["$index"]
    val lastPage = quran["${index - 1}"]!!.first()

    println(index)

    var lastLineIndex = lastPage.words.first().line_number
    lastChapterId = lastPage.chapter_id


    val builder = StringBuilder()
    list?.forEach {
        var lastWord = list.last().words.last()
        if (lastChapterId != it.chapter_id) {
            if (it.verse_key == "${it.chapter_id}:1") {
                builder.append("\nNewChapter\nBismillah\n")
                lastWord = it.words.last()
            }
            lastChapterId = it.chapter_id
        }
        it.words.forEach { word ->
            if (lastLineIndex != word.line_number) {
                lastLineIndex = word.line_number

                if (word.line_number != lastWord.line_number) {
                    builder.append("\n")
                }
            }
            builder.append(word.code_v1)
        }
    }

    println(builder.toString())
    // }


}*/

fun readFileAsTextUsingInputStream(fileName: String) = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)

fun wordsByLineNumber(verses: List<Verse>): MutableList<List<Word>> {
    val lines: MutableList<List<Word>> = mutableListOf()
    var curlingNum: Int = -1
    var curling = mutableListOf<Word>()
    verses.forEach { verse ->
        verse.words.forEach { word ->
            if (curlingNum != word.line_number) {
                if (curling.isNotEmpty()) {
                    lines.add(curling)
                }
                curlingNum = word.line_number
                curling = mutableListOf()
            }
            curling.add(word)
        }
    }
    lines.add(curling)
    return lines
}

