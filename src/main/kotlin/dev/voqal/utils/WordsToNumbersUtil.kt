package dev.voqal.utils

import java.util.*

/**
 * [Copied from here](https://github.com/jgraham0325/words-to-numbers)
 */
object WordsToNumbersUtil {

    private val allowedStrings: List<String> = mutableListOf(
        "and", "zero", "a", "one", "two", "three", "four", "five",
        "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
        "seventeen", "eighteen", "nineteen", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty",
        "ninety", "hundred", "thousand", "million", "billion", "trillion"
    )

    /**
     * Main driver method. Converts textual numbers (e.g. twenty five) to
     * integers (e.g. 25)
     *
     * Does not currently cater for decimal points. e.g. "five point two"
     *
     * @param inputText
     */
    fun convertTextualNumbersInDocument(inputText: String): String {
        // splits text into words and deals with hyphenated numbers. Use linked
        // list due to manipulation during processing
        var words = cleanAndTokenizeText(inputText)

        // replace all the textual numbers
        words = replaceTextualNumbers(words)

        // put spaces back in and return the string. Should be the same as input
        // text except from textual numbers
        return wordListToString(words)
    }

    /**
     * Does the replacement of textual numbers, processing each word at a time
     * and grouping them before doing the conversion
     *
     * @param words
     * @return
     */
    private fun replaceTextualNumbers(words: MutableList<String>): MutableList<String> {
        // holds each group of textual numbers being processed together. e.g.
        // "one" or "five hundred and two"

        val processingList: MutableList<String> = LinkedList()

        var i = 0
        while (i < words.size || !processingList.isEmpty()) {
            // caters for sentences only containing one word (that is a number)

            var word = ""
            if (i < words.size) {
                word = words[i]
            }

            // strip word of all punctuation to make it easier to process
            val wordStripped = word.replace("[^a-zA-Z\\s]".toRegex(), "").lowercase(Locale.getDefault())

            // 2nd condition: skip "and" words by themselves and at start of num
            if (allowedStrings.contains(wordStripped) && !(processingList.size == 0 && wordStripped == "and")) {
                words.removeAt(i) // remove from main list, will process later
                processingList.add(word)
            } else if (processingList.size > 0) {
                // found end of group of textual words to process

                //if "and" is the last word, add it back in to original list

                val lastProcessedWord = processingList[processingList.size - 1]
                if (lastProcessedWord == "and") {
                    words.add(i, "and")
                    processingList.removeAt(processingList.size - 1)
                }

                // main logic here, does the actual conversion
                var wordAsDigits = convertWordsToNum(processingList).toString()

                wordAsDigits = retainPunctuation(processingList, wordAsDigits)
                words.add(i, wordAsDigits.toString())

                processingList.clear()
                i += 2
            } else {
                i++
            }
        }

        return words
    }

    /**
     * Retain punctuation at the start and end of a textual number.
     *
     *
     * e.g. (seventy two) -> (72)
     *
     * @param processingList
     * @param wordAsDigits
     * @return
     */
    private fun retainPunctuation(processingList: List<String>, wordAsDigits: String): String {
        var wordAsDigits = wordAsDigits
        val lastWord = processingList[processingList.size - 1]
        val lastChar = lastWord.trim { it <= ' ' }[lastWord.length - 1]
        if (!Character.isLetter(lastChar)) {
            wordAsDigits += lastChar
        }

        val firstWord = processingList[0]
        val firstChar = firstWord.trim { it <= ' ' }[0]
        if (!Character.isLetter(firstChar)) {
            wordAsDigits = firstChar.toString() + wordAsDigits
        }

        return wordAsDigits
    }

    /**
     * Splits up hyphenated textual words. e.g. twenty-two -> twenty two
     *
     * @param sentence
     * @return
     */
    private fun cleanAndTokenizeText(sentence: String): MutableList<String> {
        val words: MutableList<String> =
            LinkedList(Arrays.asList(*sentence.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()))

        // remove hyphenated textual numbers
        for (i in words.indices) {
            val str = words[i]
            if (str.contains("-")) {
                val splitWords = Arrays.asList(*str.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray())

                // just check the first word is a textual number. Caters for
                // "twenty-five," without having to strip the comma
                if (splitWords.size > 1 && allowedStrings.contains(splitWords[0])) {
                    words.removeAt(i)
                    words.addAll(i, splitWords)
                }
            }
        }

        return words
    }

    /**
     * Creates string including spaces from a list of words
     *
     * @param list
     * @return
     */
    private fun wordListToString(list: List<String>): String {
        val result = StringBuilder("")
        for (i in list.indices) {
            val str = list[i]
            if (i == 0 && str != null) {
                result.append(list[i])
            } else if (str != null) {
                result.append(" " + list[i])
            }
        }

        return result.toString()
    }

    /**
     * Logic for taking a textual number string and converting it into a number
     * e.g. twenty five -> 25
     *
     *
     * This relies on there only being one textual number being processed. Steps
     * prior to this deal with breaking a paragraph down into individual textual
     * numbers, which could consist of a number of words.
     *
     * @param input
     * @return
     */
    private fun convertWordsToNum(words: List<String>): Long {
        var finalResult: Long = 0
        var intermediateResult: Long = 0
        for (str in words) {
            // clean up string for easier processing
            val str = str.lowercase(Locale.getDefault()).replace("[^a-zA-Z\\s]".toRegex(), "")
            if (str.equals("zero", ignoreCase = true)) {
                intermediateResult += 0
            } else if (str.equals("one", ignoreCase = true) || str.equals("a", ignoreCase = true)) {
                intermediateResult += 1
            } else if (str.equals("two", ignoreCase = true)) {
                intermediateResult += 2
            } else if (str.equals("three", ignoreCase = true)) {
                intermediateResult += 3
            } else if (str.equals("four", ignoreCase = true)) {
                intermediateResult += 4
            } else if (str.equals("five", ignoreCase = true)) {
                intermediateResult += 5
            } else if (str.equals("six", ignoreCase = true)) {
                intermediateResult += 6
            } else if (str.equals("seven", ignoreCase = true)) {
                intermediateResult += 7
            } else if (str.equals("eight", ignoreCase = true)) {
                intermediateResult += 8
            } else if (str.equals("nine", ignoreCase = true)) {
                intermediateResult += 9
            } else if (str.equals("ten", ignoreCase = true)) {
                intermediateResult += 10
            } else if (str.equals("eleven", ignoreCase = true)) {
                intermediateResult += 11
            } else if (str.equals("twelve", ignoreCase = true)) {
                intermediateResult += 12
            } else if (str.equals("thirteen", ignoreCase = true)) {
                intermediateResult += 13
            } else if (str.equals("fourteen", ignoreCase = true)) {
                intermediateResult += 14
            } else if (str.equals("fifteen", ignoreCase = true)) {
                intermediateResult += 15
            } else if (str.equals("sixteen", ignoreCase = true)) {
                intermediateResult += 16
            } else if (str.equals("seventeen", ignoreCase = true)) {
                intermediateResult += 17
            } else if (str.equals("eighteen", ignoreCase = true)) {
                intermediateResult += 18
            } else if (str.equals("nineteen", ignoreCase = true)) {
                intermediateResult += 19
            } else if (str.equals("twenty", ignoreCase = true)) {
                intermediateResult += 20
            } else if (str.equals("thirty", ignoreCase = true)) {
                intermediateResult += 30
            } else if (str.equals("forty", ignoreCase = true)) {
                intermediateResult += 40
            } else if (str.equals("fifty", ignoreCase = true)) {
                intermediateResult += 50
            } else if (str.equals("sixty", ignoreCase = true)) {
                intermediateResult += 60
            } else if (str.equals("seventy", ignoreCase = true)) {
                intermediateResult += 70
            } else if (str.equals("eighty", ignoreCase = true)) {
                intermediateResult += 80
            } else if (str.equals("ninety", ignoreCase = true)) {
                intermediateResult += 90
            } else if (str.equals("hundred", ignoreCase = true)) {
                intermediateResult *= 100
            } else if (str.equals("thousand", ignoreCase = true)) {
                intermediateResult *= 1000
                finalResult += intermediateResult
                intermediateResult = 0
            } else if (str.equals("million", ignoreCase = true)) {
                intermediateResult *= 1000000
                finalResult += intermediateResult
                intermediateResult = 0
            } else if (str.equals("billion", ignoreCase = true)) {
                intermediateResult *= 1000000000
                finalResult += intermediateResult
                intermediateResult = 0
            } else if (str.equals("trillion", ignoreCase = true)) {
                intermediateResult *= 1000000000000L
                finalResult += intermediateResult
                intermediateResult = 0
            }
        }

        finalResult += intermediateResult
        intermediateResult = 0
        return finalResult
    }
}
