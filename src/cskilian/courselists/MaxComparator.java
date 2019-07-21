package cskilian.courselists;
import java.util.Comparator;
import java.util.Set;
public class MaxComparator implements Comparator<Set<Course>> {
    /**
     * compares 2 course sets
     * @param s1 Set<Course> of 1 set of courses
     * @param s2 Set<Course> of other set of courses
     * @return
     */
    public int compare(Set<Course> s1, Set<Course> s2) {
        int sumRank1 = 0;
        int sumRank2 = 0;
        // add up ranks in l1
        for (Course course : s1) {
            sumRank1 += course.getRank();
        }
        // add up ranks in l2
        for (Course course : s2){
            sumRank2 += course.getRank();
        }
        // take their difference
        return sumRank2 - sumRank1;
    }
}
