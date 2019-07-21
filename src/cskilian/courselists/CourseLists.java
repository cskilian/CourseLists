package cskilian.courselists;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import java.util.Set;
import java.util.function.UnaryOperator;

public class CourseLists extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 64;
    private static final int MAX_COURSES = 10;
    private static final String WINDOW_TITLE = "CourseLists";
    private static final String WARNING_TEXT = "Warning!\n This program does not attempt to warn about or" +
            " resolve registration errors.\n It assumes you're eligible to take the courses you select.\n It only generates" +
            " valid schedules based on your ranking of each section.";
    private static final String CARLETON = "Carleton";
    private static final String MAXMIN_COMPARATOR = "Maximize ranking, minimize spread";
    private static final String MAX_COMPARATOR = "Maximize ranking";
    private static final String WINTER = "Winter";
    private static final String FALL = "Fall";
    private static final String SUMMER = "Summer";
    private static final int SPACING = 50;
    private static final int PADDING = 25;
    protected static SortingAlgorithm sorter;
    protected static Parser parser;
    private static String term;
    private static Page page;
    private static Stage stage;
    private String[][] courseRequests = new String[MAX_COURSES][2];
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setTitle(WINDOW_TITLE);
        this.stage.setWidth(WIDTH);
        this.stage.setHeight(HEIGHT);
        this.stage.show();
        setPage(Page.Setup);
    }
    public static void main(String[] args) {
        initDefaultSettings();
        Application.launch();
        System.out.println(Timetable.getChoices());
    }
    /**
     * Sets the sorting algorithm
     * @param sorter a valid sorting algorithm defined in SortingAlgorithm class with an
     *               appropriate comparator
     */
    public static void setSortingAlgorithm(SortingAlgorithm sorter) {
        CourseLists.sorter = sorter;
        Timetable.makeChoices();
    }

    /**
     * Sets the parser
     * @param parser a valid parser object that implements Parser interface
     */
    public static void setParser(Parser parser) {
        CourseLists.parser = parser;
        Timetable.clear();
    }
    /**
     * initializes parser, sorter and the term
     */
    private static void initDefaultSettings() {
        setSortingAlgorithm(SortingAlgorithm.MaxMin);
        setParser(new CarletonParser());
        term = FALL;
    }
    /**
     * makes a control panel that contains the back and forward buttons
     * @return a HBox object with the back and forward buttons
     */
    private HBox controlPanel() {
        Button back = new Button("Back");
        back.setMinHeight(BUTTON_HEIGHT);
        back.setMinWidth(BUTTON_WIDTH);
        back.setOnAction(actionEvent -> goBack());
        if (page == Page.Setup) {
            back.setVisible(false);
        }
        Button next = new Button("Next");
        next.setMinHeight(BUTTON_HEIGHT);
        next.setMinWidth(BUTTON_WIDTH);
        next.setOnAction(actionEvent -> nextPage());
        if (page == Page.Output) {
            next.setVisible(false);
        }
        HBox hBox = new HBox(back, next);
        hBox.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setSpacing(SPACING);
        return hBox;
    }
    /**
     * draws the 1st page of the app, the setup panel
     */
    private void makeSetupPanel() {
        HBox cPanel = controlPanel();
        Text warning = new Text(WARNING_TEXT);
        warning.setWrappingWidth(WIDTH - 50);
        warning.setTextAlignment(TextAlignment.CENTER);
        Label parserLabel = new Label("Select your school: ");
        ComboBox<String> parserBox = new ComboBox<String>();
        parserBox.setId("parserBox");
        parserBox.getItems().add(CARLETON);
        parserBox.setValue(CARLETON);
        Label sorterLabel = new Label("Select sorting algorithm: ");
        ComboBox<String> sorterBox = new ComboBox<String>();
        sorterBox.setId("sorterBox");
        sorterBox.getItems().add(MAXMIN_COMPARATOR);
        sorterBox.getItems().add(MAX_COMPARATOR);
        sorterBox.setValue(MAXMIN_COMPARATOR);
        Label termLabel = new Label("Select term: ");
        ComboBox<String> termBox = new ComboBox<String>();
        termBox.setId("termBox");
        termBox.getItems().add(FALL);
        termBox.getItems().add(WINTER);
        termBox.getItems().add(SUMMER);
        termBox.setValue(FALL);
        VBox sPanel = new VBox(cPanel, warning, parserLabel, parserBox, sorterLabel, sorterBox, termLabel, termBox);
        sPanel.setAlignment(Pos.BASELINE_CENTER);
        sPanel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        sPanel.setSpacing(SPACING);
        Scene scene = new Scene(sPanel);
        this.stage.setScene(scene);
        this.stage.show();
    }
    /**
     * draws the 2nd page of the app, where course requests are entered
     */
    private void makeRequestsPanel() {
        HBox cPanel = controlPanel();
        Label label1 = new Label("4-letter course code | ");
        Label label2 = new Label("4-digit course number");
        HBox labels = new HBox(label1, label2);
        labels.setAlignment(Pos.BASELINE_CENTER);
        VBox rPanel = new VBox(cPanel, labels);
        for (int i = 0; i < 10; i++) {
            TextField left = new TextField();
            left.setPrefWidth(100);
            TextField right = new TextField();
            right.setPrefWidth(100);
            HBox courseRow = new HBox(left, right);
            courseRow.setAlignment(Pos.BASELINE_CENTER);
            courseRow.setId("courseRow" + i);
            rPanel.getChildren().add(courseRow);
        }
        rPanel.setAlignment(Pos.BASELINE_CENTER);
        rPanel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        rPanel.setSpacing(SPACING);
        ScrollPane scrollPane = new ScrollPane(rPanel);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane);
        this.stage.setScene(scene);
        this.stage.show();
    }
    /**
     *
     */
    private void makeLoadingPanel() {
        Text loading = new Text("Loading");
        loading.setWrappingWidth(WIDTH - 100);
        loading.setTextAlignment(TextAlignment.CENTER);
        VBox ePanel = new VBox(loading);
        ePanel.setAlignment(Pos.BASELINE_CENTER);
        ePanel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        ePanel.setSpacing(SPACING);
        Scene scene = new Scene(ePanel);
        this.stage.setScene(scene);
        this.stage.show();
    }
    /**
     * draws the 3rd page of the app, where course sections are ranked
     */
    private void makeRankingsPanel() {
        HBox cPanel = controlPanel();
        Label label = new Label("Rank");
        VBox sPanel = new VBox();
        sPanel.setAlignment(Pos.TOP_LEFT);
        sPanel.setId("sPanel");
        for (Course course : CourseTree.getAll()) {
            HBox hBox = new HBox();
            Text courseText = new Text(course.toString());
            courseText.setWrappingWidth(WIDTH - 150);
            courseText.setTextAlignment(TextAlignment.JUSTIFY);
            TextField tField = new TextField();
            tField.setId("" + course.crn);
            tField.setMaxWidth(40);
            tField.setMinWidth(40);
            UnaryOperator<TextFormatter.Change> integerFilter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("-?([1-9][0-9]*)?|0?")) {
                    return change;
                }
                return null;
            };
            tField.setTextFormatter(
                    new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
            hBox.getChildren().add(tField);
            hBox.getChildren().add(courseText);
            sPanel.getChildren().add(hBox);
        }
        VBox rPanel = new VBox(cPanel, label, sPanel);
        if (CourseTree.getAll().isEmpty()) {
            rPanel.getChildren().add(new Label("No courses available"));
        }
        rPanel.setAlignment(Pos.BASELINE_CENTER);
        rPanel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        rPanel.setSpacing(SPACING);
        ScrollPane scrollPane = new ScrollPane(rPanel);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane);
        this.stage.setScene(scene);
        this.stage.show();
    }
    /**
     * draws the 4th page of the app, where the valid sets of courses are shown
     */
    private void makeOutputPanel() {
        HBox cPanel = controlPanel();
        Label result = new Label("Results");
        VBox lPanel = new VBox();
        int i = 1;
        for (Set<Course> cSet : Timetable.getChoices()) {
            HBox hBox = new HBox();
            hBox.getChildren().add(new Label("" + i + ". "));
            for (Course c : cSet) {
                TextField tf = new TextField("" + c.crn);
                tf.setMaxWidth(80);
                hBox.getChildren().add(tf);
            }
            lPanel.getChildren().add(hBox);
            i++;
        }
        if (Timetable.getChoices().isEmpty()) {
            lPanel.getChildren().add(new Label("No courses available"));
        }
        VBox oPanel = new VBox(cPanel, result, lPanel);
        oPanel.setAlignment(Pos.BASELINE_CENTER);
        oPanel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        oPanel.setSpacing(SPACING);
        ScrollPane scrollPane = new ScrollPane(oPanel);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane);
        this.stage.setScene(scene);
        this.stage.show();
    }
    /**
     * draws the error page of the app
     */
    private void makeErrorPanel() {
        Text error = new Text("Something went wrong! Ensure you're connected to the internet, your firewall allows " +
        "outbound connections and that you entered valid course codes");
        error.setWrappingWidth(WIDTH - 100);
        error.setTextAlignment(TextAlignment.CENTER);
        Button ok = new Button("OK");
        ok.setOnAction(actionEvent -> setPage(Page.Requests));
        VBox ePanel = new VBox(error, ok);
        ePanel.setAlignment(Pos.BASELINE_CENTER);
        ePanel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        ePanel.setSpacing(SPACING);
        Scene scene = new Scene(ePanel);
        this.stage.setScene(scene);
        this.stage.show();
    }
    /**
     * this sets the page of the app to be shown
     * @param page is a valid Page enum
     */
    private void setPage(Page page) {
        switch(page) {
            case Setup:
                this.page = Page.Setup;
                makeSetupPanel();
                break;
            case Requests:
                this.page = Page.Requests;
                Timetable.clear();
                makeRequestsPanel();
                break;
            case Rankings:
                this.page = Page.Rankings;
                makeRankingsPanel();
                break;
            case Output:
                this.page = Page.Output;
                makeOutputPanel();
                break;
        }
    }
    /**
     * this flips to the next screen(page)
     */
    private void nextPage() {
        switch(page) {
            case Setup:
                inputSetupPageData();
                setPage(Page.Requests);
                break;
            case Requests:
                inputRequestsPageData();
                makeLoadingPanel();
                Thread webRequestWorker = new Thread() {
                    @Override
                    public void run() {
                        try {
                            fetchCourses();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setPage(Page.Rankings);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    makeErrorPanel();
                                }
                            });
                        }
                    }
                };
                webRequestWorker.start();
                break;
            case Rankings:
                inputRankingsPageData();
                setPage(Page.Output);
                break;
            case Output:
                setPage(Page.Output);
                break;
        }
    }

    /**
     * this flips back to the previous screen (page)
     */
    private void goBack() {
        switch(page) {
            case Setup:
                setPage(Page.Setup);
                break;
            case Requests:
                setPage(Page.Setup);
                break;
            case Rankings:
                setPage(Page.Requests);
                break;
            case Output:
                setPage(Page.Rankings);
                break;
        }
    }
    /**
     * this takes the form data and sets the parser, sorter and the term
     */
    private void inputSetupPageData() {
        switch ((String) ((ComboBox) stage.getScene().lookup("#parserBox")).getValue()) {
            case CARLETON:
                setParser(new CarletonParser());
                break;
        }
        switch ((String) ((ComboBox) stage.getScene().lookup("#sorterBox")).getValue()) {
            case MAX_COMPARATOR:
                setSortingAlgorithm(SortingAlgorithm.Max);
                break;
            case MAXMIN_COMPARATOR:
                setSortingAlgorithm(SortingAlgorithm.MaxMin);
                break;
        }
        term = (String) ((ComboBox) stage.getScene().lookup("#termBox")).getValue();
    }
    /**
     * this inputs the form data into courseRequests for use after the form has been discarded
     */
    private void inputRequestsPageData() {
        for (int i = 0; i < MAX_COURSES; i++) {
            String subject = ((TextField) (((HBox) stage.getScene().lookup("#courseRow" + i)).getChildren().get(0))).getText().trim().toUpperCase();
            String code = ((TextField) (((HBox) stage.getScene().lookup("#courseRow" + i)).getChildren().get(1))).getText().trim();
            if (!subject.isEmpty() && !code.isEmpty()) {
                courseRequests[i][0] = subject;
                courseRequests[i][1] = code;
            } else {
                courseRequests[i][0] = "";
                courseRequests[i][1] = "";
            }
        }
    }
    /**
     * this takes the form data and sets the rankings of the courses
     */
    private void inputRankingsPageData() {
        for (Node node : ((VBox) stage.getScene().lookup("#sPanel")).getChildren()) {
            TextField tf = ((TextField) ((HBox) node).getChildren().get(0));
            CourseTree.get(Integer.parseInt(tf.getId())).setRank(Integer.parseInt(tf.getText()));
        }
        Timetable.makeChoices();
    }
    /**
     * this fetches the courses from the web and adds it to the timetable
     */
    private void fetchCourses() throws Exception {
        for (int i = 0; i < MAX_COURSES; i++) {
            String cCode = courseRequests[i][0];
            String cNumber = courseRequests[i][1];
            if (!cCode.isEmpty() && !cNumber.isEmpty()) {
                Timetable.add(parser.makeCourses(courseRequests[i][0], courseRequests[i][1], term));
            }
        }
    }
}
