package cskilian.courselists;
import java.util.Comparator;
import java.util.Set;
public class MaxMinComparator implements Comparator<Set<Course>> {
    /**
     * compares 2 course sets
     * @param s1 Set<Course> of 1 set of courses
     * @param s2 Set<Course> of the 2nd set of courses
     * @return int of their difference
     */
    public int compare(Set<Course> s1, Set<Course> s2) {
        int sumRank1 = 0;
        float variance1 = 0;
        int sumRank2 = 0;
        float variance2 = 0;
        for (Course c : s1) {
            sumRank1 += c.getRank();
            variance1 += c.getRank() * c.getRank();
        }
        for (Course c : s2){
            sumRank2 += c.getRank();
            variance2 += c.getRank() * c.getRank();
        }
        // compute variance of s1 and l2
        variance1 -= sumRank1 * sumRank1 / s1.size();
        variance2 -= sumRank2 * sumRank2 / s2.size();
        variance1 /= s1.size();
        variance2 /= s2.size();
        // add a 0.1 in case variance is 0
        variance1 += 0.1;
        variance2 += 0.1;
        float ret = (sumRank2 / variance2) - (sumRank1 / variance1);
        // we need to turn the return value into an int
        return (-1 < ret && ret < 1) ? (int) ret * 10: (int) ret * 100;
    }
}
