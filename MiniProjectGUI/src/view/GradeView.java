package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Course;
import model.Student;

public class GradeView {

    private StudentManagementSystemGUI smsGUI;

    private ChoiceBox<Student> studentChoiceBox;
    private ChoiceBox<Course> courseChoiceBox;
    private TextField midtermScoreField;
    private TextField finalScoreField;
    private Label messageLabel;
    private VBox mainLayout;

    public GradeView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        initializeUI();
    }

    private void initializeUI() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));

        messageLabel = new Label("");
        messageLabel.setTextFill(Color.BLUE);

        studentChoiceBox = new ChoiceBox<>();
        studentChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên";
            }
            @Override
            public Student fromString(String string) { return null; }
        });

        courseChoiceBox = new ChoiceBox<>();
        courseChoiceBox.setConverter(new javafx.util.StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getCourseID() + " - " + course.getCourseName() : "Chọn khóa học";
            }
            @Override
            public Course fromString(String string) { return null; }
        });

        refreshStudentAndCourseChoices(); // Initial load
        studentChoiceBox.getSelectionModel().selectFirst();
        courseChoiceBox.getSelectionModel().selectFirst();

        midtermScoreField = new TextField();
        midtermScoreField.setPromptText("Điểm giữa kỳ");
        finalScoreField = new TextField();
        finalScoreField.setPromptText("Điểm cuối kỳ");

        formGrid.addRow(0, new Label("Sinh viên:"), studentChoiceBox);
        formGrid.addRow(1, new Label("Khóa học:"), courseChoiceBox);
        formGrid.addRow(2, new Label("Điểm giữa kỳ:"), midtermScoreField);
        formGrid.addRow(3, new Label("Điểm cuối kỳ:"), finalScoreField);
        formGrid.add(messageLabel, 0, 4, 2, 1);

        Button submitButton = new Button("Nhập điểm");
        submitButton.setOnAction(e -> submitGrade());

        Button clearButton = new Button("Xóa form");
        clearButton.setOnAction(e -> clearFormFieldsOnly());

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(submitButton, clearButton);

        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(new Label("NHẬP ĐIỂM"), new Separator(), formGrid, buttonBox);
    }

    // Make public for external refresh
    public void refreshStudentAndCourseChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentChoiceBox.setItems(students);
        studentChoiceBox.getItems().add(0, null);
        studentChoiceBox.getSelectionModel().select(0); // Select default

        ObservableList<Course> courses = FXCollections.observableArrayList(smsGUI.getAllCourses());
        courseChoiceBox.setItems(courses);
        courseChoiceBox.getItems().add(0, null);
        courseChoiceBox.getSelectionModel().select(0); // Select default
    }

    private void clearFormFieldsOnly() {
        studentChoiceBox.getSelectionModel().select(0);
        courseChoiceBox.getSelectionModel().select(0);
        midtermScoreField.clear();
        finalScoreField.clear();
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);
    }

    private void submitGrade() {
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);

        try {
            Student selectedStudent = studentChoiceBox.getSelectionModel().getSelectedItem();
            Course selectedCourse = courseChoiceBox.getSelectionModel().getSelectedItem();

            if (selectedStudent == null || selectedCourse == null) {
                messageLabel.setText("Vui lòng chọn sinh viên và khóa học.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            double midtermScore = Double.parseDouble(midtermScoreField.getText());
            double finalScore = Double.parseDouble(finalScoreField.getText());

            if (midtermScore < 0 || midtermScore > 10 || finalScore < 0 || finalScore > 10) {
                messageLabel.setText("Điểm phải nằm trong khoảng từ 0 đến 10.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            smsGUI.inputGrade(selectedStudent.getStudentID(), selectedCourse.getCourseID(), midtermScore, finalScore);
            messageLabel.setText("Nhập điểm thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            messageLabel.setText("Điểm giữa kỳ và cuối kỳ phải là số hợp lệ.");
            messageLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi nhập điểm: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    public Parent getGradeInputView() {
        return mainLayout;
    }
}