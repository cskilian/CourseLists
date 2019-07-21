package cskilian.courselists;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
public class CourseTree {
    // the default Course constructor creates a dummy root node
    public static Course root = new Course();
    /**
     * adds a collection of courses to the tree
     * @param courses Collection<Course> of a list of courses
     */
    public static void add(Collection<Course> courses) {
        // adds lectures before its children. It shouldn't be needed, but safer for avoiding any edge cases
        for (Course course : courses.stream().filter(c -> c.type.equals("Lecture")).collect(Collectors.toList())) {
            add(course);
        }
        for (Course course :
                courses.stream().filter(c -> !(c.type.equals("Lecture"))).collect(Collectors.toList())) {
            add(course);
        }
    }
    /**
     * adds a course to the tree
     * @param course Course that is added to the tree
     */
    public static void add(Course course) {
        add(course, root);
    }
    /**
     * removes a course with the given course registration number
     * @param crn int of a course registration number
     */
    public static void remove(int crn) {
        Course course = get(crn);
        if (course != null) {
            remove(course, root);
        }
    }
    /**
     * removes a course by subject, section and type
     * @param subject String of the subject (ie. COMP1805)
     * @param section String of the section (ie. B1)
     * @param type String of type (ie. Lecture)
     */
    public static void remove(final String subject, final String section, final String type) {
        Course course = get(subject, section, type);
        if (course != null) {
            remove(course, root);
        }
    }
    /**
     * gets a course from the tree with the given course registration number
     * @param crn int of the course registration number
     * @return Course if the course is found, null otherwise
     */
    public static Course get(int crn) {
        return get(crn, root, null);
    }
    /**
     * gets a course from the tree with the given subject, section and type
     * @param subject String of subject(ie. COMP1805)
     * @param section String of section (ie. B1)
     * @param type String of type (ie. Lecture)
     * @return Course if the course is found, null otherwise
     */
    public static Course get(final String subject, final String section, final String type) {
        return get(subject, section, type, root, null);
    }
    /**
     * returns all courses in the tree
     * @return Collection<Course> of all courses in the tree, list is empty if tree contains no courses
     */
    public static Collection<Course> getAll() {
        Collection<Course> courses = new LinkedList<Course>();
        getAll(root, courses);
        return(courses);
    }
    /**
     * cheks if the tree contains a given course
     * @param course Course to be checked
     * @return true if it's in the tree, false otherwise
     */
    public static boolean contains(Course course) {
        return contains(course, root, false);
    }
    /**
     * checks if the tree contains a given course by registration number
     * @param crn int of the course registration number
     * @return true if it's in the tree, false otherwise
     */
    public static boolean contains(int crn) {
        return contains(crn, root, false);
    }
    /**
     *  checks if the tree contains a given course by subject, section and type
     * @param subject String of the subject (ie. COMP1805)
     * @param section String of the section (ie. B1)
     * @param type String of the type (ie. Lecture)
     * @return true if it's in the tree, false otherwise
     */
    public static boolean contains(final String subject, final String section, final String type) {
        return contains(subject, section, type, root, false);
    }
    /**
     * adds a course into the tree
     * @param course Course that is to be added into the tree
     * @param node Course of a node in the tree that we recursively traverse on
     */
    private static void add(Course course, Course node) {
        // base case if the node is null
        if (null == node) { return; }
        if (CourseLists.parser.subordinate(course, node)) {
            if (root == node) {
                // we're adding a lecture
                node.getCategories().add(new Category(course.subject));
                for (Category category : node.getCategories()) {
                    if (category.type.equals(course.subject)) {
                        category.getCourses().add(course);
                    }
                }
            } else {
                // we're adding a lab or tutorial
                node.getCategories().add(new Category(course.type));
                for (Category category : node.getCategories()) {
                    if (category.type.equals(course.type)) {
                        category.getCourses().add(course);
                    }
                }
            }
        } else {
            // recursively descend on the tree
            for (Category category : node.getCategories()) {
                for (Course c : category.getCourses()) {
                    add(course, c);
                }
            }
        }
    }
    /**
     * recursively traverses the tree and removes the node removed
     * @param removed Course of a node to be removed
     * @param node Course of a ndde in the tree to be traversed
     */
    private static void remove(Course removed, Course node) {
        Category removedCategory = null;
        // remove course if it's contained in the node's children
        for (Category category : node.getCategories()) {
            if (category.getCourses().contains(removed)) {
                category.getCourses().remove(removed);
                if (category.getCourses().isEmpty()) {
                    removedCategory = category;
                }
            }
        }
        // important to remove the category dummy node if it has no children
        if (removedCategory != null) {
            node.getCategories().remove(removedCategory);
        }
        for (Category category : node.getCategories()) {
            for (Course course : category.getCourses()) {
                remove(removed, course);
            }
        }
    }
    /**
     * recursively gets a course from the tree
     * @param crn int of the course registration number
     * @param node Course of a node in the tree to be traversed
     * @param acc Course of an accumulator node that will return the node with the given crn
     * @return Course if found, null otherwise
     */
    private static Course get(int crn, Course node, Course acc) {
        if (crn == node.crn) {
            acc = node;
        } else {
            for (Category category : node.getCategories()) {
                for (Course course : category.getCourses()) {
                    acc = get(crn, course, acc);
                }
            }
        }
        return acc;
    }
    /**
     * recursively gets a course from the tree
     * @param subject String of the course subject
     * @param section String of the course section
     * @param type String of the course type
     * @param node Course of the node of the tree to be traversed on
     * @param acc Course of an accumulator that contains the course
     * @return Course if found, null otherwise
     */
    private static Course get(final String subject, final String section, final String type, Course node, Course acc) {
        if (subject.equals(node.subject) && section.equals(node.section) && type.equals(node.type)) {
            acc = node;
        } else {
            for (Category category : node.getCategories()) {
                for (Course course : category.getCourses()) {
                    acc = get(subject, section, type, course, acc);
                }
            }
        }
        return acc;
    }
    /**
     * fetches all the courses in the tree and puts it in a list
     * @param node Course of the node of the tree to be traversed on
     * @param acc Collection<Course> is an existing collection that accumulates the nodes during traversal
     */
    private static void getAll(Course node, Collection<Course> acc) {
        if (node.getCategories().isEmpty() && node != root) {
            acc.add(node);
        } else {
            if (node != root) {
                acc.add(node);
            }
            for (Category category : node.getCategories()) {
                for (Course course : category.getCourses()) {
                    getAll(course, acc);
                }
            }
        }
    }
    /**
     * checks if the tree contains a course with the given crn
     * @param crn int of the course registration number
     * @param node Course of the node in a tree
     * @param flag boolean holding the return value
     * @return true if found, false otherwise
     */
    private static boolean contains(int crn, Course node, boolean flag) {
        if (crn == node.crn) {
            flag = true;
        } else {
            for (Category category : node.getCategories()) {
                for (Course course : category.getCourses()) {
                    flag = contains(crn, course, flag);
                }
            }
        }
        return flag;
    }
    /**
     * checks if the tree contains a course with the given subject, section, type
     * @param subject String of the subject (ie. COMP1805)
     * @param section String of the section (ie. B1)
     * @param type String of the type (ie. Lecture)
     * @param node Course of a node in the tree
     * @param flag boolean of an accumulator of the return value
     * @return true if found, false otherwise
     */
    private static boolean contains(final String subject, final String section, final String type, Course node, boolean flag) {
        if (subject.equals(node.subject) && section.equals(node.section) && type.equals(node.type)) {
            flag = true;
        } else {
            for (Category category : node.getCategories()) {
                for (Course course : category.getCourses()) {
                    flag = contains(subject, section, type, course, flag);
                }
            }
        }
        return flag;
    }
    /**
     * checks if the tree contains a given course
     * @param course Course that is to be checked in the tree
     * @param node Course of a node that we traverse on
     * @param flag accumulator of the return value
     * @return true if found, false otherwise
     */
    private static boolean contains(Course course, Course node, boolean flag) {
        if (course.equals(node)) {
            flag = true;
        } else {
            for (Category category : node.getCategories()) {
                for (Course c : category.getCourses()) {
                    flag = contains(course, c, flag);
                }
            }
        }
        return flag;
    }
}
