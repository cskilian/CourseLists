package cskilian.courselists;
import java.util.Collection;
public interface Parser {
    boolean subordinate(Course course, Course node);
    Collection<Course> makeCourses(String subject, String number, String term) throws Exception;
}
