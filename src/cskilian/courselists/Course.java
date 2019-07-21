package cskilian.courselists;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
public class Course implements Comparable<Course> {
    // this is the unique 5-digit course id or registration number
    public final int crn;
    // this is the subject including the course number, COMP1405
    public final String subject;
    // this is the 1, 2 or 3 alphanumeric letter B2, B3 that is used to determine course hierarchies
    public final String section;
    // type of course, Lecture, Tutorial, Lab
    public final String type;
    public final String instructor;
    public final Set<Block> schedule;
    private int rank;
    private final SortedSet<Category> categories;
    // the default constructor will create a dummy root node for the course tree
    public Course() {
        crn = -1;
        subject = "root";
        section = null;
        type = null;
        instructor = null;
        rank = Integer.MIN_VALUE;
        schedule = null;
        categories = new TreeSet<Category>();
    }

    /**
     * Constructor
     * @param crn int of the 6 digit course registration number
     * @param subject String of the subject
     * @param section String of the 3-lettre alphanumeric section code
     * @param type String of the course type (lecture, tutorial, laboratory)
     * @param instructor String of the instructor's name
     * @param rank int of the rank
     * @param schedule Set<Block> of the schedule blocks
     */
    public Course(int crn, final String subject, final String section, final String type, final String instructor, int rank, final Set<Block> schedule) {
        this.crn = crn;
        this.subject = subject;
        this.section = section;
        this.type = type;
        this.instructor = instructor;
        this.rank = rank;
        this.schedule = new LinkedHashSet<Block>(schedule);
        categories = new TreeSet<Category>();
    }
    /**
     * checks if the 2 courses are equal based on matching crn or if the subject, section and types are equal
     * @param other Course that is compared to
     * @return true if they're equal, false otherwise
     */
    public boolean equals(Course other) {
        // this is for the dummy node
        if (crn == -1) { return false; }
        return crn == other.crn || (subject.equals(other.subject) && section.equals(other.section) && type.equals(other.type));
    }
    /**
     * returns the difference of their crns
     * @param other Course that is compared to
     * @return int of the difference between the 2 crns
     */
    public int compareTo(Course other) {
        return this.crn - other.crn;
    }
    /**
     * Getter for rank
     * @return int of rank
     */
    public int getRank() {
        return this.rank;
    }
    /**
     * Setter for rank
     * @param rank int of the rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }
    /**
     * getter for its children nodes
     * @return Set<Category> of the children
     */
    public Set<Category> getCategories() {
        return this.categories;
    }
    @Override
    public String toString() {
        if (subject.equals("root")) {
            return "root\n";
        }
        String temp = crn + " " + subject + " " + section + " " + type + " Instructor: " + instructor;
        for (Block b : schedule) {
            temp += "\n" + b.toString();
        }
        temp += "\n";
        return temp;
    }
}
