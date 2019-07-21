package cskilian.courselists;
public class Block {
    public final Day day;
    // term is one of Fall, Winter, Summer for Karlton
    public final String term;
    // start is the 24-hour representation of the time stored in an int 09:35 is 935
    public final int start;
    // end is the 24-hour representation of the time stored in an int, 13:35 is 1335
    public final int end;
    /**
     * Constructor
     * @param day valid Day enum
     * @param term valid String
     * @param start int representing 24 hour time
     * @param end int representing 24 hour time
     */
    public Block(Day day, String term, int start, int end) {
        this.day = day;
        this.term = term;
        this.start = start;
        this.end = end;
    }
    /**
     * checks if the 2 blocks are equal
     * @param other Block that is compared to
     * @return true if they're equal, false otherwise
     */
    public boolean equals(final Block other) {
        return this.day == other.day && this.term.equals(other.term) && this.start == other.start && this.end == other.end;
    }

    /**
     * checks if 2 time blocks conflict with eachother
     * @param other Block that is compared to
     * @return true if they're equal, false otherwise
     */
    public boolean conflicts(final Block other) {
        return this.day == other.day && this.term.equals(other.term) && this.start <= other.end && this.end >= other.start;
    }
    @Override
    public String toString() {
        String startMinutes = start % 100 < 10 ? "0" + start % 100 : "" + start % 100;
        String endMinutes = end % 100 < 10 ? "0" + end % 100 : "" + end % 100;
        return day + " " + start / 100 + ":" + startMinutes + "-" + end / 100 + ":" + endMinutes;
    }
}
