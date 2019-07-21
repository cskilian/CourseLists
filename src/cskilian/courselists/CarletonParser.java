package cskilian.courselists;
import javax.net.ssl.HttpsURLConnection;
import java.util.Collection;
import java.io.*;
import java.net.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class CarletonParser implements Parser{
    private final String USER_AGENT = "Mozilla/5.0";
    private String termCode;
    private String sessionId;
    /**
     * compares the 3 digit course section numbers and checks if course is a child of node
     * @param course Course of the intended parent
     * @param node Course of the intended child
     * @return true, if they're related, else false
     */
    @Override
    public boolean subordinate(Course course, Course node) {
        if (CourseTree.root.crn == node.crn && course.type.equals("Lecture")) {
            return true;
        } else if (course.subject.equals(node.subject)) {
            if (node.type.equals("Lecture")) {
                if (course.type.equals("Tutorial")) {
                    return (node.section.charAt(0) == course.section.charAt(0) || course.section.charAt(0) == 'T');
                } else if (course.type.equals("Laboratory")) {
                    return (node.section.charAt(0) == course.section.charAt(0) || course.section.charAt(0) == 'L');
                }
            }
        }
        return false;
    }
    /**
     * fetches the courses online from a course subject, and course number
     * @param subject String of subject
     * @param number String of number
     * @param term String of term
     * @return Collection<Course> of valid courses
     * @throws Exception thrown for IO or parsing problems
     */
    @Override
    public Collection<Course> makeCourses(String subject, String number, String term) throws Exception {
        // getTermCode
        getTermCodeAndSessionID(term);
        // getCourses
        return getCourses(subject, number, term);
    }
    /**
     * helper for getting a term code and session id to make a valid post request
     * @param term String of term
     * @throws Exception thrown for IO or parsing errors
     */
    private void getTermCodeAndSessionID(String term) throws Exception {
        // make a connection
        URL url = new URL("https://central.carleton.ca/prod/bwysched.p_select_term?wsea_code=EXT");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine = null;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains(term)) {
                this.termCode = getValue(inputLine);
            }
            if (inputLine.contains("session_id")) {
                this.sessionId = getValue(inputLine);
            }
        }
    }
    /**
     * makes a post request for getting the courses from online
     * @param subject String of subject
     * @param number String of number
     * @param term String of term
     * @return Collection<Course> of valid course list
     * @throws Exception thrown for IO or parsing problems
     */
    private Collection<Course> getCourses(String subject, String number,String term) throws Exception {
        StringBuilder buffer = new StringBuilder(); // holds the web page that we get back
        // make a connection and fetch the page
        URL url = new URL("https://central.carleton.ca/prod/bwysched.p_course_search");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setDoOutput(true);
        String parameters = "wsea_code=EXT&term_code=" + termCode + "&session_id=" + sessionId + "&ws_numb=&sel_aud=dummy" +
                "&sel_subj=dummy&sel_camp=dummy&sel_sess=dummy&sel_attr=dummy&sel_levl=dummy&sel_schd=dummy&sel_insm=dummy" +
                "&sel_link=dummy&sel_wait=dummy&sel_day=dummy&sel_begin_hh=dummy&sel_begin_mi=dummy&sel_begin_am_pm=dummy" +
                "&sel_end_hh=dummy&sel_end_mi=dummy&sel_end_am_pm=dummy&sel_instruct=dummy&sel_special=dummy&sel_resd=dummy" +
                "&sel_breadth=dummy&sel_levl=&sel_subj=" + subject + "&sel_number=" + number +
                "&sel_crn=&sel_special=O&sel_sess=&sel_schd=&sel_instruct=&sel_begin_hh=0&sel_begin_mi=0" +
                "&sel_begin_am_pm=a&sel_end_hh=0&sel_end_mi=0&sel_end_am_pm=a&sel_day=m&sel_day=t&sel_day=w&sel_day=r" +
                "&sel_day=f&sel_day=s&sel_day=u&block_button=";
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(parameters);
        out.flush();
        String inputLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        out.close();
        return htmlToCourses(buffer.toString(), term);
    }
    /**
     * creates the course data from the raw html
     * @param buffer String of the html we got back
     * @param term String of the term
     * @return Collection<Course> of the valid courses
     * @throws Exception thrown if parsing problems are encountered
     */
    private Collection<Course> htmlToCourses(String buffer, String term) throws Exception {
        // holds parsed courses
        Collection<Course> courses = new LinkedList<Course>();
        // parse in raw html
        Document doc = Jsoup.parse(buffer);
        Elements tables = doc.getElementsByTag("table");
        Elements rows = tables.get(2).getElementsByTag("tr");
        // check if we have any courses
        if (rows.get(0).toString().contains("DRAFT TIMETABLE")) { return courses; }
        // parse each course from the htmls rows
        boolean parsingCourse = false;
        int crn = -1;
        String subject = null;
        String section = null;
        String type = null;
        String instructor = null;
        int rank = 0;
        Set<Block> schedule = null;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getElementsByTag("td").size() >= 2 && rows.get(i).getElementsByTag("td").get(1).text().equals("Open")) {
                // set flag to true
                parsingCourse = true;
                // parse crn, subject, section, type, instructor
                crn = Integer.parseInt(rows.get(i).getElementsByTag("td").get(2).getElementsByTag("font").get(0).text());
                subject = rows.get(i).getElementsByTag("td").get(3).getElementsByTag("font").get(0).text();
                section = rows.get(i).getElementsByTag("td").get(4).text();
                type = rows.get(i).getElementsByTag("td").get(7).text();
                instructor = rows.get(i).getElementsByTag("td").get(10).text();
                schedule = new LinkedHashSet<Block>();
            } else if (rows.get(i).getElementsByTag("td").size() >= 2 && rows.get(i).getElementsByTag("td").get(1).html().contains("Meeting Date:")) {
                // parse dates
                String rowText = rows.get(i).getElementsByTag("td").get(1).text();
                int startTime = Integer.parseInt(rowText.split("Time:")[1].split("Building:")[0].split(" - ")[0].trim().replace(":",""));
                int endTime = Integer.parseInt(rowText.split("Time:")[1].split("Building:")[0].split(" - ")[1].trim().replace(":",""));
                if (rowText.contains("Mon")) {
                    schedule.add(new Block(Day.Monday, term, startTime, endTime));
                }
                if (rowText.contains("Tue")) {
                    schedule.add(new Block(Day.Tuesday, term, startTime, endTime));
                }
                if (rowText.contains("Wed")) {
                    schedule.add(new Block(Day.Wednesday, term, startTime, endTime));
                }
                if (rowText.contains("Thu")) {
                    schedule.add(new Block(Day.Thursday, term, startTime, endTime));
                }
                if (rowText.contains("Fri")) {
                    schedule.add(new Block(Day.Friday, term, startTime, endTime));
                }
                if (rowText.contains("Sat")) {
                    schedule.add(new Block(Day.Saturday, term, startTime, endTime));
                }
                if (rowText.contains("Sun")) {
                    schedule.add(new Block(Day.Sunday, term, startTime, endTime));
                }
            } else if (parsingCourse) {
                // if everything went well create a course from the parsed data
                if (crn >= 0 && subject != null && section != null && type != null && instructor != null && schedule != null) {
                    courses.add(new Course(crn, subject, section, type, instructor, rank, schedule));
                }
                // reset every variable to its default state and turn off flag
                parsingCourse = false;
                crn = -1;
                subject = null;
                section = null;
                type = null;
                instructor = null;
                schedule = null;
            }
        }
        return courses;
    }
    // helper for extracting the termCode or sessionId from a html line
    private String getValue(String inputLine) {
        String value = "";
        for (String chunk : inputLine.split(" ")) {
            if (chunk.contains("value")) {
                String[] chunks = chunk.split("=");
                value = chunks[1];
                value = value.split("\"")[1];
            }
        }
        return value;
    }
}
