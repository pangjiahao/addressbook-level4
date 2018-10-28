package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     * Ignores case, but a full word match is required.
     * <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     *
     * @param sentence cannot be null
     * @param word     cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
            .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns a detailed message of the t, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     *
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Checks if sentence contains {@code word}, or words similar to {@code word}
     *
     * @param tolerance higher tolerance means only very similar words will match, value between 0 to 100
     * @param sentence sentence to be checked
     * @param word that is being searched for, does not need to be single word
     * @return whether words are similar within the tolerance
     */
    public static boolean containsWordFuzzy(String sentence, String word, int tolerance) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        preppedWord = preppedWord.toLowerCase();

        String preppedSentence = sentence.toLowerCase();

        return partialRatioTest(preppedSentence, preppedWord, tolerance)
                || tokenSetRatioTest(sentence, word, tolerance);
    }

    /**
     * Tests 2 strings if they are similar within the specified tolerance
     * Uses partial ratio test to check similarity
     * @param tolerance strings of higher similarity will return True
     *
     * @return boolean of whether the strings are similar enough
     */
    private static boolean partialRatioTest(String s1, String s2, int tolerance) {
        return computePartialRatio(s1, s2) >= tolerance;
    }

    /**
     * Tests 2 strings if they are similar within the specified tolerance
     * Uses token set ratio test to check similarity
     * @param tolerance strings of higher similarity will return True
     *
     * @return boolean of whether the strings are similar enough
     */
    private static boolean tokenSetRatioTest(String s1, String s2, int tolerance) {
        return computeTokenSetRatio(s1, s2) >= tolerance;
    }

    /**
     * Computes the partial ratio test between 2 strings.
     * Similar strings return a higher number
     *
     * @return int between 0 and 100
     */
    public static int computePartialRatio(String string1, String string2) {
        return FuzzySearch.partialRatio(string1, string2);
    }

    /**
     * Computes the token set ratio test between 2 strings.
     * Similar strings return a higher number
     *
     * @return int between 0 and 100
     */
    public static int computeTokenSetRatio(String string1, String string2) {
        return FuzzySearch.tokenSetRatio(string1, string2);
    }
}
