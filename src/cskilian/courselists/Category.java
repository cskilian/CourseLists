package cskilian.courselists;
import java.util.SortedSet;
import java.util.TreeSet;
// Category is a dummy node type in the tree for determining recursion behaviour in the tree. We create combinations
// of all the children at a Category node level
public class Category implements Comparable<Category> {
    public final String type;
    private final SortedSet<Course> courses;
    /**
     * Constructor
     * @param type String of type
     */
    public Category(String type) {
        this.type = type;
        this.courses = new TreeSet<Course>();
    }
    /**
     * gets the children of category
     * @return SortedSet<Course> of children
     */
    public SortedSet<Course> getCourses() {
        return this.courses;
    }
    public boolean equals(Category other) {
        return this.type.equals(other.type);
    }
    public int compareTo(Category other) {
        return this.type.compareTo(other.type);
    }
}
