package cskilian.courselists;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
public class Timetable {
    private static PriorityQueue<Set<Course>> choices;
    /**
     * adds courses into the course tree and calculates the priorities
     * @param courses Collection<Course> of valid courses
     */
    public static void add(Collection<Course> courses) {
        CourseTree.add(courses);
        makeChoices();
    }
    /**
     * adds 1 course into the course tree and calculates the priorities
     * @param course Course of a valid course
     */
    public static void add(Course course) {
        CourseTree.add(course);
        makeChoices();
    }
    /**
     * removes a course based on the course number
     * @param crn int of the course registration number
     */
    public static void remove(int crn) {
        CourseTree.remove(crn);
        makeChoices();
    }
    /**
     * generates the valid course listings
     */
    public static void makeChoices() {
        // set up the priority queue according to sorter
        if (CourseLists.sorter == SortingAlgorithm.MaxMin) {
            choices = new PriorityQueue<Set<Course>>(10, new MaxMinComparator());
        } else if (CourseLists.sorter == SortingAlgorithm.Max) {
            choices = new PriorityQueue<Set<Course>>(10, new MaxComparator());
        }
        // add all non-conflicting course combinations to p-queue
        choices.addAll(makeChoices(CourseTree.root));
    }
    /**
     * clears the course tree
     */
    public static void clear() {
        CourseTree.root = new Course();
    }
    /**
     * getter for the valid course choices
     * @return Collection<Set<Course>> of valid course sets
     */
    public static Collection<Set<Course>> getChoices() {
        return choices;
    }
    // recursively descend on the course tree and make all combination of non-conflicting courses
    /**
     * recursively generates valid course combinations
     * @param node Course is the root
     * @return List<SortedSet<Course>> of valid course combinations
     */
    private static List<SortedSet<Course>> makeChoices(Course node) {
        if (node.getCategories().isEmpty()) {
            List<SortedSet<Course>> courses = new LinkedList<SortedSet<Course>>();
            if (node != CourseTree.root) {
                courses.add(new TreeSet<Course>());
                courses.get(courses.size() - 1).add(node);
            }
            return courses;
        } else {
            List<SortedSet<Course>> courses = new LinkedList<SortedSet<Course>>();
            if (node != CourseTree.root) {
                courses.add(new TreeSet<Course>());
                courses.get(courses.size() - 1).add(node);
            }
            for (Category category : node.getCategories()) {
                List<SortedSet<Course>> temp = new LinkedList<SortedSet<Course>>();
                for (Course course : category.getCourses()) {
                    temp.addAll(makeChoices(course));
                }
                courses = combine(courses, temp);
            }
            return courses;
        }
    }

    /**
     * helper for combining 2 lists of course combinations
     * @param list1 List<SortedSet<Course>> of a list of course combinations
     * @param list2 List<SortedSet<Course>> of a list of course combinations
     * @return List<SortedSet<Course>> of course combinations
     */
    private static List<SortedSet<Course>> combine(List<SortedSet<Course>> list1, List<SortedSet<Course>> list2) {
        List<SortedSet<Course>> out = new LinkedList<SortedSet<Course>>();
        if ((list1.isEmpty() || list1 == null) && (list2.isEmpty() || list2 == null)) {
            return out;
        } else if (list1.isEmpty() || list1 == null) {
            out.addAll(list2);
            return out;
        } else if (list2.isEmpty() || list2 == null) {
            out.addAll(list1);
            return out;
        } else {
            for (Set<Course> i : list1) {
                for (Set<Course> j : list2) {
                    if (!schedulesConflict(i, j)) {
                        out.add(new TreeSet<Course>());
                        out.get(out.size() - 1).addAll(i);
                        out.get(out.size() - 1).addAll(j);
                    }
                }
            }
            return out;
        }
    }
    /**
     * checks if 2 course sets have conflicting courses
     * @param s1 Set<Course> set of courses
     * @param s2 Set<Course> set of other courses
     * @return true if 2 sets conflict, else false
     */
    private static boolean schedulesConflict(final Set<Course> s1, final Set<Course> s2) {
        for (Course i : s1) {
            for (Course j : s2) {
                if (coursesConflict(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks if 2 courses conflict
     * @param c1 Course 1
     * @param c2 Course 2
     * @return true if they conflict, else false
     */
    private static boolean coursesConflict(final Course c1, final Course c2) {
        for (Block b1 : c1.schedule) {
            for (Block b2 : c2.schedule) {
                if (b1.conflicts(b2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
