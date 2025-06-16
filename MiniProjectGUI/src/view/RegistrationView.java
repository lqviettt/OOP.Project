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
import model.CreditBasedStudent;
import model.PartTimeStudent;
import model.Student;

public class RegistrationView {

    private StudentManagementSystemGUI smsGUI;

    private ChoiceBox<Student> studentChoiceBox;
    private ChoiceBox<Course> courseChoiceBox;
    private TextArea courseDetailsArea;
    private Label messageLabel;
    private VBox mainLayout;

    public RegistrationView(StudentManagementSystemGUI smsGUI) {
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
                return student != null ? student.getStudentID() + " - " + student.getName() + (student instanceof PartTimeStudent ? " (VHVL)" : " (TC)") : "Chọn sinh viên";
            }
            @Override
            public Student fromString(String string) { return null; }
        });
        refreshStudentChoices(); // Initial load
        studentChoiceBox.getSelectionModel().selectFirst();

        courseChoiceBox = new ChoiceBox<>();
        courseChoiceBox.setConverter(new javafx.util.StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getCourseID() + " - " + course.getCourseName() : "Chọn khóa học";
            }
            @Override
            public Course fromString(String string) { return null; }
        });
        refreshCourseChoices(); // Initial load
        courseChoiceBox.getSelectionModel().selectFirst();

        courseDetailsArea = new TextArea();
        courseDetailsArea.setEditable(false);
        courseDetailsArea.setWrapText(true);
        courseDetailsArea.setPrefRowCount(10);

        courseChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            if (newCourse != null) {
                displayCourseDetails(newCourse);
            } else {
                courseDetailsArea.clear();
            }
            messageLabel.setText(""); // Xóa thông báo khi chọn một mục mới
            messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định
        });

        formGrid.addRow(0, new Label("Sinh viên:"), studentChoiceBox);
        formGrid.addRow(1, new Label("Khóa học:"), courseChoiceBox);
        formGrid.add(new Label("Chi tiết khóa học:"), 0, 2);
        formGrid.add(courseDetailsArea, 0, 2, 2, 1);
        formGrid.add(messageLabel, 0, 3, 2, 1);

        Button registerButton = new Button("Đăng ký môn học");
        registerButton.setOnAction(e -> registerCourse());

        Button clearButton = new Button("Xóa form");
        clearButton.setOnAction(e -> clearFormFieldsOnly());

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(registerButton, clearButton);

        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(new Label("ĐĂNG KÝ MÔN HỌC"), new Separator(), formGrid, buttonBox);
    }

    // Make these methods public so they can be called from outside
    public void refreshStudentChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentChoiceBox.setItems(students);
        studentChoiceBox.getItems().add(0, null);
        studentChoiceBox.getSelectionModel().select(0); // Select "Chọn sinh viên" after refresh
    }

    public void refreshCourseChoices() {
        ObservableList<Course> courses = FXCollections.observableArrayList(smsGUI.getAllCourses());
        courseChoiceBox.setItems(courses);
        courseChoiceBox.getItems().add(0, null);
        courseChoiceBox.getSelectionModel().select(0); // Select "Chọn khóa học" after refresh
    }

    private void displayCourseDetails(Course course) {
        if (course == null) {
            courseDetailsArea.clear();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(course.getCourseID()).append("\n");
        sb.append("Tên: ").append(course.getCourseName()).append("\n");
        sb.append("Tín chỉ: ").append(course.getCredits()).append("\n");
        sb.append("Trọng số GK: ").append(course.getMidtermWeight()).append("%, CK: ").append(course.getFinalWeight()).append("%\n");
        sb.append("Sức chứa: ").append(course.getCurrentEnrollment()).append("/").append(course.getMaxCapacity()).append("\n");
        if (course.getPrerequisite() != null) {
            sb.append("Tiên quyết: ").append(course.getPrerequisite().getCourseID()).append(" - ").append(course.getPrerequisite().getCourseName()).append("\n");
        } else {
            sb.append("Tiên quyết: Không có\n");
        }
        courseDetailsArea.setText(sb.toString());
    }

    private void clearFormFieldsOnly() {
        studentChoiceBox.getSelectionModel().select(0);
        courseChoiceBox.getSelectionModel().select(0);
        courseDetailsArea.clear();
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);
    }

    private void registerCourse() {
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);

        Student selectedStudent = studentChoiceBox.getSelectionModel().getSelectedItem();
        Course selectedCourse = courseChoiceBox.getSelectionModel().getSelectedItem();

        if (selectedStudent == null || selectedCourse == null) {
            messageLabel.setText("Vui lòng chọn sinh viên và khóa học.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (selectedStudent instanceof PartTimeStudent) {
            messageLabel.setText("Sinh viên hệ vừa học vừa làm không được phép tự đăng ký môn học.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (selectedStudent instanceof CreditBasedStudent) {
            CreditBasedStudent creditStudent = (CreditBasedStudent) selectedStudent;
            boolean success = smsGUI.registerStudentForCourse(creditStudent.getStudentID(), selectedCourse.getCourseID());

            if (success) {
                messageLabel.setText("Đăng ký môn học thành công!");
                messageLabel.setTextFill(Color.GREEN);
                // No need to clear form here, let the user see the success message
            } else {
                messageLabel.setText("Đăng ký môn học thất bại! (Kiểm tra sức chứa, môn tiên quyết, đã đăng ký...)");
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    public Parent getRegistrationView() {
        return mainLayout;
    }
}