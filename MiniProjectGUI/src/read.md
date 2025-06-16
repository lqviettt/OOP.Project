package view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane; // Import BorderPane
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Course;
import model.Student;
import java.util.List;

public class CourseView {
private StudentManagementSystemGUI smsGUI;
private TableView<Course> courseTable;
private ObservableList<Course> courseObservableList;

    private TextField idField;
    private TextField nameField;
    private TextField creditsField;
    private TextField midtermWeightField;
    private TextField finalWeightField;
    private TextField maxCapacityField;
    private ChoiceBox<Course> prerequisiteChoiceBox;
    private Label messageLabel;

    // Các thành phần mới cho chức năng xem sinh viên đã đăng ký
    private TextArea enrolledStudentsArea;
    private Button viewEnrolledStudentsButton;

    private BorderPane mainLayout; // Thay đổi từ VBox sang BorderPane

    public CourseView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        initializeUI();
        loadCourseData(); // Tải dữ liệu ban đầu
    }

    private void initializeUI() {
        // Form nhập liệu
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));

        idField = new TextField();
        idField.setPromptText("ID Môn học");
        nameField = new TextField();
        nameField.setPromptText("Tên Môn học");
        creditsField = new TextField();
        creditsField.setPromptText("Số tín chỉ");
        midtermWeightField = new TextField();
        midtermWeightField.setPromptText("Trọng số giữa kỳ (%)");
        finalWeightField = new TextField();
        finalWeightField.setPromptText("Trọng số cuối kỳ (%)");
        maxCapacityField = new TextField();
        maxCapacityField.setPromptText("Số lượng tối đa");

        prerequisiteChoiceBox = new ChoiceBox<>();
        prerequisiteChoiceBox.setConverter(new javafx.util.StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getCourseID() + " - " + course.getCourseName() : "Không có môn tiên quyết";
            }

            @Override
            public Course fromString(String string) {
                return null;
            }
        });
        loadPrerequisiteChoices(); // Tải các môn học có thể làm tiên quyết

        messageLabel = new Label("");
        messageLabel.setTextFill(Color.BLUE); // Mặc định màu xanh

        formGrid.addRow(0, new Label("ID:"), idField);
        formGrid.addRow(1, new Label("Tên:"), nameField);
        formGrid.addRow(2, new Label("Tín chỉ:"), creditsField);
        formGrid.addRow(3, new Label("Trọng số GK:"), midtermWeightField);
        formGrid.addRow(4, new Label("Trọng số CK:"), finalWeightField);
        formGrid.addRow(5, new Label("SL Tối đa:"), maxCapacityField);
        formGrid.addRow(6, new Label("Tiên quyết:"), prerequisiteChoiceBox);
        formGrid.add(messageLabel, 0, 7, 2, 1);

        // Nút chức năng
        Button addButton = new Button("Thêm");
        addButton.setOnAction(e -> addCourse());
        Button updateButton = new Button("Cập nhật");
        updateButton.setOnAction(e -> updateCourse());
        Button deleteButton = new Button("Xóa");
        deleteButton.setOnAction(e -> deleteCourse());
        Button clearButton = new Button("Xóa form");
        clearButton.setOnAction(e -> clearFormFieldsOnly()); // Gọi phương thức mới để chỉ xóa các trường

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // Bảng hiển thị môn học
        courseTable = new TableView<>();
        TableColumn<Course, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("courseID"));
        TableColumn<Course, String> nameCol = new TableColumn<>("Tên Môn học");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        TableColumn<Course, Integer> creditsCol = new TableColumn<>("Tín chỉ");
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        TableColumn<Course, Integer> midtermWeightCol = new TableColumn<>("Trọng số GK");
        midtermWeightCol.setCellValueFactory(new PropertyValueFactory<>("midtermWeight"));
        TableColumn<Course, Integer> finalWeightCol = new TableColumn<>("Trọng số CK");
        finalWeightCol.setCellValueFactory(new PropertyValueFactory<>("finalWeight"));
        TableColumn<Course, Integer> capacityCol = new TableColumn<>("SL Tối đa");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("maxCapacity"));
        TableColumn<Course, String> prerequisiteCol = new TableColumn<>("Tiên quyết");
        prerequisiteCol.setCellValueFactory(cellData -> {
            Course prereq = cellData.getValue().getPrerequisite();
            return new ReadOnlyStringWrapper(prereq != null ? prereq.getCourseID() : "N/A");
        });
        TableColumn<Course, Integer> currentEnrollmentCol = new TableColumn<>("Đã ĐK");
        currentEnrollmentCol.setCellValueFactory(new PropertyValueFactory<>("currentEnrollment"));

        courseTable.getColumns().addAll(idCol, nameCol, creditsCol, midtermWeightCol, finalWeightCol, capacityCol, prerequisiteCol, currentEnrollmentCol);

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idField.setText(newSelection.getCourseID());
                nameField.setText(newSelection.getCourseName());
                creditsField.setText(String.valueOf(newSelection.getCredits()));
                midtermWeightField.setText(String.valueOf(newSelection.getMidtermWeight()));
                finalWeightField.setText(String.valueOf(newSelection.getFinalWeight()));
                maxCapacityField.setText(String.valueOf(newSelection.getMaxCapacity()));
                prerequisiteChoiceBox.getSelectionModel().select(newSelection.getPrerequisite());
                // Tự động hiển thị sinh viên đã đăng ký khi chọn môn học
                viewSelectedCourseEnrolledStudents();
                messageLabel.setText(""); // Xóa thông báo khi chọn một môn học mới
                messageLabel.setTextFill(Color.BLUE);
            } else {
                clearFormFieldsOnly(); // Khi không có lựa chọn nào, reset form
            }
        });

        // --- Phần xem sinh viên đã đăng ký môn học ---
        enrolledStudentsArea = new TextArea();
        enrolledStudentsArea.setEditable(false);
        enrolledStudentsArea.setWrapText(true);
        enrolledStudentsArea.setPromptText("Sinh viên đã đăng ký môn học được chọn sẽ hiển thị ở đây.");
        enrolledStudentsArea.setPrefRowCount(7);

        viewEnrolledStudentsButton = new Button("Xem SV đã ĐK");
        viewEnrolledStudentsButton.setOnAction(e -> viewSelectedCourseEnrolledStudents());


        // --- Cấu trúc layout bằng BorderPane ---
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // VBox cho phần bên trái (Form, Buttons, Table)
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(0, 10, 0, 0)); // Padding bên phải để tạo khoảng cách
        leftPanel.getChildren().addAll(
                new Label("QUẢN LÝ MÔN HỌC"),
                new Separator(),
                formGrid,
                buttonBox,
                courseTable
        );

        // VBox cho phần bên phải (Enrolled Students)
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(0, 0, 0, 10)); // Padding bên trái để tạo khoảng cách
        rightPanel.setPrefWidth(300); // Đặt chiều rộng ưu tiên cho panel bên phải
        rightPanel.getChildren().addAll(
                new Label("SINH VIÊN ĐÃ ĐĂNG KÝ (MÔN HỌC ĐANG CHỌN)"),
                new Separator(),
                viewEnrolledStudentsButton,
                enrolledStudentsArea
        );

        mainLayout.setCenter(leftPanel);
        mainLayout.setRight(rightPanel);
    }

    private void loadCourseData() {
        courseObservableList = FXCollections.observableArrayList(smsGUI.getAllCourses());
        courseTable.setItems(courseObservableList);
        loadPrerequisiteChoices(); // Cập nhật danh sách tiên quyết khi dữ liệu môn học thay đổi
    }

    private void loadPrerequisiteChoices() {
        ObservableList<Course> courses = FXCollections.observableArrayList(smsGUI.getAllCourses());
        prerequisiteChoiceBox.setItems(courses);
        prerequisiteChoiceBox.getItems().add(0, null); // Thêm tùy chọn "Không có môn tiên quyết"
        prerequisiteChoiceBox.getSelectionModel().select(0); // Chọn mặc định là null
    }

    private void clearFormFieldsOnly() { // Đổi tên để rõ ràng hơn
        idField.clear();
        nameField.clear();
        creditsField.clear();
        midtermWeightField.clear();
        finalWeightField.clear();
        maxCapacityField.clear();
        prerequisiteChoiceBox.getSelectionModel().select(0); // Chọn "Không có môn tiên quyết"
        messageLabel.setText(""); // Xóa thông báo khi người dùng nhấn nút "Xóa form"
        messageLabel.setTextFill(Color.BLUE);
        courseTable.getSelectionModel().clearSelection();
        enrolledStudentsArea.clear(); // Xóa nội dung khi clear form
    }

    private void addCourse() {
        messageLabel.setText(""); // Xóa thông báo cũ
        messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định

        try {
            String courseId = idField.getText();
            String name = nameField.getText();
            int credits = Integer.parseInt(creditsField.getText());
            int midtermWeight = Integer.parseInt(midtermWeightField.getText());
            int finalWeight = Integer.parseInt(finalWeightField.getText());
            int maxCapacity = Integer.parseInt(maxCapacityField.getText());
            Course prerequisite = prerequisiteChoiceBox.getSelectionModel().getSelectedItem();

            if (courseId.isEmpty() || name.isEmpty() || credits <= 0 || midtermWeight < 0 || finalWeight < 0 || maxCapacity <= 0) {
                messageLabel.setText("Vui lòng điền đầy đủ và đúng định dạng thông tin.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            if (midtermWeight + finalWeight != 100) {
                messageLabel.setText("Tổng trọng số giữa kỳ và cuối kỳ phải là 100%.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (smsGUI.findCourseById(courseId) != null) {
                messageLabel.setText("ID môn học đã tồn tại.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            Course newCourse = new Course(courseId, name, credits, midtermWeight, finalWeight, maxCapacity, prerequisite);
            smsGUI.addCourse(newCourse);
            loadCourseData(); // Tải lại dữ liệu sau khi thêm
            // Không clearFormFieldsOnly() ở đây để thông báo được hiển thị
            messageLabel.setText("Thêm môn học thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            messageLabel.setText("Tín chỉ, trọng số và sức chứa phải là số nguyên.");
            messageLabel.setTextFill(Color.RED);
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Lỗi: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi thêm môn học: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void updateCourse() {
        messageLabel.setText(""); // Xóa thông báo cũ
        messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định

        try {
            String courseId = idField.getText();
            Course existingCourse = smsGUI.findCourseById(courseId);

            if (existingCourse == null) {
                messageLabel.setText("Không tìm thấy môn học để cập nhật.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Kiểm tra các trường bắt buộc không được rỗng khi cập nhật
            if (nameField.getText().isEmpty() || creditsField.getText().isEmpty() ||
                    midtermWeightField.getText().isEmpty() || finalWeightField.getText().isEmpty() ||
                    maxCapacityField.getText().isEmpty()) {
                messageLabel.setText("Vui lòng điền đầy đủ thông tin.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            int credits = Integer.parseInt(creditsField.getText());
            int midtermWeight = Integer.parseInt(midtermWeightField.getText());
            int finalWeight = Integer.parseInt(finalWeightField.getText());
            int maxCapacity = Integer.parseInt(maxCapacityField.getText());

            if (credits <= 0 || midtermWeight < 0 || finalWeight < 0 || maxCapacity <= 0) {
                messageLabel.setText("Tín chỉ, trọng số và sức chứa phải là số nguyên dương.");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            if (midtermWeight + finalWeight != 100) {
                messageLabel.setText("Tổng trọng số giữa kỳ và cuối kỳ phải là 100%.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            existingCourse.setCourseName(nameField.getText());
            existingCourse.setCredits(credits);
            existingCourse.setMidtermWeight(midtermWeight);
            existingCourse.setFinalWeight(finalWeight);
            existingCourse.setMaxCapacity(maxCapacity);
            existingCourse.setPrerequisite(prerequisiteChoiceBox.getSelectionModel().getSelectedItem());

            loadCourseData(); // Tải lại dữ liệu sau khi cập nhật
            // Không clearFormFieldsOnly() ở đây để thông báo được hiển thị
            messageLabel.setText("Cập nhật môn học thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            messageLabel.setText("Tín chỉ, trọng số và sức chứa phải là số nguyên.");
            messageLabel.setTextFill(Color.RED);
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Lỗi: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi cập nhật môn học: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void deleteCourse() {
        messageLabel.setText(""); // Xóa thông báo cũ
        messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định

        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            smsGUI.removeCourse(selectedCourse.getCourseID());
            loadCourseData(); // Tải lại dữ liệu sau khi xóa
            clearFormFieldsOnly(); // Xóa form và thông báo khi xóa thành công
            messageLabel.setText("Xóa môn học thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("Vui lòng chọn môn học cần xóa.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    // Phương thức mới để hiển thị sinh viên đã đăng ký
    private void viewSelectedCourseEnrolledStudents() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            List<Student> enrolledStudents = smsGUI.getEnrolledStudentsInCourse(selectedCourse.getCourseID());
            StringBuilder sb = new StringBuilder();
            if (enrolledStudents.isEmpty()) {
                sb.append("Môn học này chưa có sinh viên nào đăng ký.");
            } else {
                sb.append("Các sinh viên đã đăng ký môn ").append(selectedCourse.getCourseName()).append(":\n");
                for (Student student : enrolledStudents) {
                    sb.append("- ").append(student.getStudentID()).append(": ").append(student.getName()).append("\n");
                }
            }
            enrolledStudentsArea.setText(sb.toString());
        } else {
            enrolledStudentsArea.setText("Vui lòng chọn môn học từ bảng để xem sinh viên đã đăng ký.");
        }
    }

    public Parent getCourseView() {
        return mainLayout; // Trả về layout đã được khởi tạo
    }
}
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

        refreshStudentAndCourseChoices();
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
        clearButton.setOnAction(e -> clearFormFieldsOnly()); // Gọi phương thức mới để chỉ xóa các trường nhập liệu

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(submitButton, clearButton);

        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(new Label("NHẬP ĐIỂM"), new Separator(), formGrid, buttonBox);
    }

    private void refreshStudentAndCourseChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentChoiceBox.setItems(students);
        studentChoiceBox.getItems().add(0, null);

        ObservableList<Course> courses = FXCollections.observableArrayList(smsGUI.getAllCourses());
        courseChoiceBox.setItems(courses);
        courseChoiceBox.getItems().add(0, null);
    }

    private void clearFormFieldsOnly() { // Phương thức mới để chỉ xóa các trường
        studentChoiceBox.getSelectionModel().select(0);
        courseChoiceBox.getSelectionModel().select(0);
        midtermScoreField.clear();
        finalScoreField.clear();
        messageLabel.setText(""); // Xóa thông báo khi người dùng nhấn nút "Xóa form"
        messageLabel.setTextFill(Color.BLUE);
    }

    private void submitGrade() {
        // Luôn xóa thông báo cũ trước khi hiển thị thông báo mới
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
            // Không gọi clearFormFieldsOnly() ở đây để thông báo được hiển thị
            // Bạn có thể cân nhắc gọi clearFormFieldsOnly() sau một khoảng thời gian ngắn
            // hoặc khi người dùng thực hiện hành động khác
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
        refreshStudentChoices();
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
        refreshCourseChoices();
        courseChoiceBox.getSelectionModel().selectFirst();

        courseDetailsArea = new TextArea();
        courseDetailsArea.setEditable(false);
        courseDetailsArea.setWrapText(true);
        courseDetailsArea.setPrefRowCount(5);

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
        formGrid.add(courseDetailsArea, 0, 3, 2, 1);
        formGrid.add(messageLabel, 0, 4, 2, 1);

        Button registerButton = new Button("Đăng ký môn học");
        registerButton.setOnAction(e -> registerCourse());

        Button clearButton = new Button("Xóa form");
        clearButton.setOnAction(e -> clearFormFieldsOnly()); // Gọi phương thức mới để chỉ xóa các trường nhập liệu

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(registerButton, clearButton);

        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(new Label("ĐĂNG KÝ MÔN HỌC"), new Separator(), formGrid, buttonBox);
    }

    private void refreshStudentChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentChoiceBox.setItems(students);
        studentChoiceBox.getItems().add(0, null);
    }

    private void refreshCourseChoices() {
        ObservableList<Course> courses = FXCollections.observableArrayList(smsGUI.getAllCourses());
        courseChoiceBox.setItems(courses);
        courseChoiceBox.getItems().add(0, null);
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

    private void clearFormFieldsOnly() { // Phương thức mới để chỉ xóa các trường
        studentChoiceBox.getSelectionModel().select(0);
        courseChoiceBox.getSelectionModel().select(0);
        courseDetailsArea.clear();
        messageLabel.setText(""); // Xóa thông báo khi người dùng nhấn nút "Xóa form"
        messageLabel.setTextFill(Color.BLUE);
    }

    private void registerCourse() {
        // Luôn xóa thông báo cũ trước khi hiển thị thông báo mới
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
                // Không gọi clearFormFieldsOnly() ở đây để thông báo được hiển thị
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
package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Student;

public class ReportView {

    private StudentManagementSystemGUI smsGUI;

    // Các thành phần cho chức năng kiểm tra tốt nghiệp
    private ChoiceBox<Student> studentGraduationChoiceBox;
    private Label graduationStatusLabel;

    // Các thành phần cho chức năng xem bảng điểm
    private ChoiceBox<Student> studentForTranscriptChoiceBox;
    private TextArea reportTextArea; // Đổi tên thành reportTextArea theo yêu cầu của bạn
    private Label transcriptMessageLabel; // Label mới cho thông báo bảng điểm

    public ReportView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        initializeUI();
    }

    private void initializeUI() {
        // --- Phần kiểm tra tốt nghiệp ---
        GridPane graduationGrid = new GridPane();
        graduationGrid.setHgap(10);
        graduationGrid.setVgap(10);
        graduationGrid.setPadding(new Insets(10));

        studentGraduationChoiceBox = new ChoiceBox<>();
        studentGraduationChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên";
            }

            @Override
            public Student fromString(String string) {
                return null;
            }
        });
        refreshStudentGraduationChoices();
        studentGraduationChoiceBox.getSelectionModel().selectFirst();

        Button checkGraduationButton = new Button("Kiểm tra Tốt nghiệp");
        checkGraduationButton.setOnAction(e -> checkGraduation());
        graduationStatusLabel = new Label("");
        graduationStatusLabel.setTextFill(Color.BLUE);

        graduationGrid.addRow(0, new Label("Sinh viên:"), studentGraduationChoiceBox);
        graduationGrid.addRow(1, checkGraduationButton);
        graduationGrid.add(graduationStatusLabel, 0, 2, 2, 1);

        // --- Phần xem bảng điểm của sinh viên ---
        GridPane transcriptGrid = new GridPane();
        transcriptGrid.setHgap(10);
        transcriptGrid.setVgap(10);
        transcriptGrid.setPadding(new Insets(10));

        studentForTranscriptChoiceBox = new ChoiceBox<>();
        studentForTranscriptChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên";
            }

            @Override
            public Student fromString(String string) {
                return null;
            }
        });
        refreshStudentForTranscriptChoices();
        studentForTranscriptChoiceBox.getSelectionModel().selectFirst();

        Button viewTranscriptButton = new Button("Xem Bảng điểm");
        viewTranscriptButton.setOnAction(e -> viewTranscript());
        reportTextArea = new TextArea(); // Sử dụng tên reportTextArea
        reportTextArea.setEditable(false);
        reportTextArea.setWrapText(true);
        reportTextArea.setPrefRowCount(10); // Tăng kích thước để hiển thị nhiều điểm hơn

        transcriptMessageLabel = new Label(""); // Khởi tạo label mới
        transcriptMessageLabel.setTextFill(Color.BLUE);

        transcriptGrid.addRow(0, new Label("Sinh viên:"), studentForTranscriptChoiceBox);
        transcriptGrid.addRow(1, viewTranscriptButton);
        transcriptGrid.add(reportTextArea, 0, 2, 2, 1);
        transcriptGrid.add(transcriptMessageLabel, 0, 3, 2, 1); // Thêm label vào grid

        // --- Layout tổng thể ---
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                new Label("KIỂM TRA TỐT NGHIỆP"),
                new Separator(),
                graduationGrid,
                new Label("BẢNG ĐIỂM CỦA SINH VIÊN"),
                new Separator(),
                transcriptGrid
        );
    }

    private void refreshStudentGraduationChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentGraduationChoiceBox.setItems(students);
        studentGraduationChoiceBox.getItems().add(0, null);
    }

    private void refreshStudentForTranscriptChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentForTranscriptChoiceBox.setItems(students);
        studentForTranscriptChoiceBox.getItems().add(0, null);
    }

    private void checkGraduation() {
        Student selectedStudent = studentGraduationChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            smsGUI.checkGraduationForStudent(selectedStudent.getStudentID());
            String status = selectedStudent.isGraduationStatus() ? "ĐỦ ĐIỀU KIỆN TỐT NGHIỆP" : "CHƯA ĐỦ ĐIỀU KIỆN TỐT NGHIỆP";
            graduationStatusLabel.setText("Trạng thái tốt nghiệp của " + selectedStudent.getName() + ": " + status);
            graduationStatusLabel.setTextFill(selectedStudent.isGraduationStatus() ? Color.GREEN : Color.RED);
        } else {
            graduationStatusLabel.setText("Vui lòng chọn sinh viên để kiểm tra tốt nghiệp.");
            graduationStatusLabel.setTextFill(Color.RED);
        }
    }

    private void viewTranscript() {
        Student selectedStudent = studentForTranscriptChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            // Thay vì in ra console, chúng ta sẽ bắt đầu in vào String và hiển thị
            // Sử dụng một ByteArrayOutputStream để chuyển hướng System.out
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream old = System.out;
            System.setOut(ps);

            // Giả định rằng selectedStudent.viewTranscript() tồn tại và in ra System.out
            // Nếu phương thức này nằm trong StudentManagementSystemGUI hoặc StudentManagementSystem
            // bạn sẽ cần gọi nó thông qua smsGUI hoặc đối tượng Student tương ứng.
            // Ví dụ: smsGUI.displayStudentTranscript(selectedStudent.getStudentID());
            // Hoặc, nếu method viewTranscript() là của class Student
            selectedStudent.viewTranscript();

            System.out.flush();
            System.setOut(old); // Khôi phục System.out
            reportTextArea.setText(baos.toString());
            transcriptMessageLabel.setTextFill(Color.GREEN);
            transcriptMessageLabel.setText("Đã hiển thị bảng điểm.");
        } else {
            transcriptMessageLabel.setText("Vui lòng chọn một sinh viên để xem bảng điểm.");
            reportTextArea.setText("");
            transcriptMessageLabel.setTextFill(Color.RED);
        }
    }


    public Parent getReportView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));

        GridPane graduationGrid = new GridPane();
        graduationGrid.setHgap(10);
        graduationGrid.setVgap(10);
        graduationGrid.setPadding(new Insets(10));
        studentGraduationChoiceBox = new ChoiceBox<>();
        studentGraduationChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) { return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên"; }
            @Override
            public Student fromString(String string) { return null; }
        });
        Button checkGraduationButton = new Button("Kiểm tra Tốt nghiệp");
        checkGraduationButton.setOnAction(e -> checkGraduation());
        graduationStatusLabel = new Label("");
        graduationStatusLabel.setTextFill(Color.BLUE);
        graduationGrid.addRow(0, new Label("Sinh viên:"), studentGraduationChoiceBox);
        graduationGrid.addRow(1, checkGraduationButton);
        graduationGrid.add(graduationStatusLabel, 0, 2, 2, 1);

        GridPane transcriptGrid = new GridPane();
        transcriptGrid.setHgap(10);
        transcriptGrid.setVgap(10);
        transcriptGrid.setPadding(new Insets(10));
        studentForTranscriptChoiceBox = new ChoiceBox<>();
        studentForTranscriptChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) { return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên"; }
            @Override
            public Student fromString(String string) { return null; }
        });
        Button viewTranscriptButton = new Button("Xem Bảng điểm");
        viewTranscriptButton.setOnAction(e -> viewTranscript());
        reportTextArea = new TextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setWrapText(true);
        reportTextArea.setPrefRowCount(10);

        transcriptMessageLabel = new Label("");
        transcriptMessageLabel.setTextFill(Color.BLUE);

        transcriptGrid.addRow(0, new Label("Sinh viên:"), studentForTranscriptChoiceBox);
        transcriptGrid.addRow(1, viewTranscriptButton);
        transcriptGrid.add(reportTextArea, 0, 2, 2, 1);
        transcriptGrid.add(transcriptMessageLabel, 0, 3, 2, 1);

        refreshStudentGraduationChoices();
        refreshStudentForTranscriptChoices();
        studentGraduationChoiceBox.getSelectionModel().selectFirst();
        studentForTranscriptChoiceBox.getSelectionModel().selectFirst();


        layout.getChildren().addAll(
                new Label("KIỂM TRA TỐT NGHIỆP"),
                new Separator(),
                graduationGrid,
                new Label("BẢNG ĐIỂM CỦA SINH VIÊN"),
                new Separator(),
                transcriptGrid
        );
        return layout;
    }
}
package view;

import model.Course;
import model.PartTimeStudent;
import model.StudentManagementSystem;
import model.Student;
import model.CreditBasedStudent;
import model.Grade;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StudentManagementSystemGUI {
private StudentManagementSystem sms;

    public StudentManagementSystemGUI() {
        this.sms = new StudentManagementSystem();
        // Khởi tạo dữ liệu mẫu giống như trong MainConsole để thử nghiệm
        initializeSampleData();
    }

    private void initializeSampleData() {
        Course gt1 = new Course("GT1", "Giải tích 1", 3, 30, 70, 50);
        Course gt2 = new Course("GT2", "Giải tích 2", 3, 30, 70, 50);
        Course dev = new Course("DEV", "Lập trình cơ bản", 4, 40, 60, 40, gt1);
        Course web = new Course("WEB", "Lập trình Web", 3, 30, 70, 30, dev);
        Course cs = new Course("CS1", "Nhập môn Khoa học Máy tính", 3, 30, 70, 50);
        Course algo = new Course("ALGO", "Thuật toán", 3, 30, 70, 50, dev);
        Course db = new Course("DB", "Cơ sở dữ liệu", 3, 30, 70, 40, dev);
        Course oop = new Course("OOP", "Lập trình Hướng đối tượng", 3, 40, 60, 50, dev);
        Course ai = new Course("AI", "Trí tuệ nhân tạo", 3, 40, 60, 60, algo);
        Course ml = new Course("ML", "Machine Learning", 3, 40, 60, 60, ai);

        sms.addCourse(gt1);
        sms.addCourse(gt2);
        sms.addCourse(dev);
        sms.addCourse(web);
        sms.addCourse(cs);
        sms.addCourse(algo);
        sms.addCourse(db);
        sms.addCourse(oop);
        sms.addCourse(ai);
        sms.addCourse(ml);

        sms.addStudent(new PartTimeStudent("P1", "Tran Thi B", LocalDate.of(1998, 10, 20),
                "b.tran@example.com", new ArrayList<>(List.of(gt1, gt2, dev))));
        sms.addStudent(new PartTimeStudent("P2", "Hoang Thi E", LocalDate.of(1999, 8, 30),
                "e.hoang@example.com", new ArrayList<>(List.of(cs, gt1, oop))));
        sms.addStudent(new PartTimeStudent("P3", "Vo Thi H", LocalDate.of(1997, 9, 3),
                "h.vo@example.com", new ArrayList<>(List.of(gt2, cs))));
        sms.addStudent(new CreditBasedStudent("C1", "Nguyen Van A", LocalDate.of(2003, 5, 15),
                "a.nguyen@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C2", "Le Thi C", LocalDate.of(2002, 3, 18),
                "c.le@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C3", "Pham Van D", LocalDate.of(2004, 12, 5),
                "d.pham@example.com", 4));
        sms.addStudent(new CreditBasedStudent("C4", "Nguyen Van F", LocalDate.of(2001, 7, 22),
                "f.nguyen@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C5", "Tran Van G", LocalDate.of(2003, 1, 14),
                "g.tran@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C6", "Bui Van I", LocalDate.of(2000, 11, 27),
                "i.bui@example.com", 4));
        sms.addStudent(new CreditBasedStudent("C7", "Do Thi J", LocalDate.of(2003, 6, 11),
                "j.do@example.com", 3));
    }

    public List<Student> getAllStudents() {
        return sms.getAllStudents();
    }

    public Student findStudentById(String studentID) {
        return sms.findStudentById(studentID);
    }

    public List<Student> findStudentByName(String name) {
        return sms.findStudentByName(name);
    }

    public void addStudent(Student student) {
        sms.addStudent(student);
    }

    public void updateStudentInfo(Student student) {
        sms.updateStudentInfo(student);
    }

    public void removeStudent(String studentID) {
        sms.removeStudent(studentID);
    }

    public List<Course> getAllCourses() {
        return sms.getAllCourses();
    }

    public Course findCourseById(String courseID) {
        return sms.findCourseById(courseID);
    }

    public void addCourse(Course course) {
        sms.addCourse(course);
    }

    public void removeCourse(String courseID) {
        sms.removeCourse(courseID);
    }

    public void inputGrade(String studentID, String courseID, double midtermScore, double finalScore) {
        sms.inputGrade(studentID, courseID, midtermScore, finalScore);
    }

    public void checkGraduationForStudent(String studentID) {
        sms.checkGraduationForStudent(studentID);
    }

    // PHƯƠNG THỨC MỚI ĐỂ ĐĂNG KÝ MÔN HỌC TỪ GUI
    public boolean registerStudentForCourse(String studentID, String courseID) {
        Student student = sms.findStudentById(studentID);
        Course course = sms.findCourseById(courseID);

        if (student != null && course != null) {
            return student.registerCourse(course);
        }
        return false;
    }

    // Phương thức để xem các môn học đã đăng ký của một sinh viên
    public List<Course> getRegisteredCoursesForStudent(String studentId) {
        Student student = sms.findStudentById(studentId);
        if (student != null) {
            return student.viewRegisteredCourses();
        }
        return new ArrayList<>();
    }

    // Phương thức để lấy danh sách sinh viên đăng ký một môn học
    public List<Student> getEnrolledStudentsInCourse(String courseID) {
        Course course = sms.findCourseById(courseID);
        if (course != null) {
            return course.getEnrolledStudents();
        }
        return new ArrayList<>();
    }
}
package view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane; // Import BorderPane
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Course;
import model.CreditBasedStudent;
import model.PartTimeStudent;
import model.Student;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class StudentView {

    private StudentManagementSystemGUI smsGUI;
    private TableView<Student> studentTable;
    private ObservableList<Student> studentObservableList;

    private TextField idField;
    private TextField nameField;
    private DatePicker dobPicker;
    private TextField emailField;
    private CheckBox isCreditBasedCheckBox;
    private TextField requiredCreditsField; // Chỉ cho CreditBasedStudent
    private Label messageLabel;

    // Các thành phần mới cho chức năng xem môn học đã đăng ký
    private TextArea registeredCoursesArea;
    private Button viewRegisteredCoursesButton;

    private BorderPane mainLayout; // Thay đổi từ VBox sang BorderPane

    public StudentView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        initializeUI();
        loadStudentData(); // Tải dữ liệu ban đầu
    }

    private void initializeUI() {
        // Form nhập liệu
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));

        idField = new TextField();
        idField.setPromptText("ID Sinh viên");
        nameField = new TextField();
        nameField.setPromptText("Tên sinh viên");
        dobPicker = new DatePicker();
        dobPicker.setPromptText("Ngày sinh (YYYY-MM-DD)");
        emailField = new TextField();
        emailField.setPromptText("Email");
        isCreditBasedCheckBox = new CheckBox("Là Sinh viên Tín chỉ?");
        requiredCreditsField = new TextField();
        requiredCreditsField.setPromptText("Tín chỉ yêu cầu");
        requiredCreditsField.setManaged(false); // Ẩn mặc định
        requiredCreditsField.setVisible(false); // Ẩn mặc định

        isCreditBasedCheckBox.setOnAction(e -> {
            boolean isCredit = isCreditBasedCheckBox.isSelected();
            requiredCreditsField.setManaged(isCredit);
            requiredCreditsField.setVisible(isCredit);
        });

        messageLabel = new Label("");
        messageLabel.setTextFill(Color.BLUE); // Mặc định màu xanh

        formGrid.addRow(0, new Label("ID:"), idField);
        formGrid.addRow(1, new Label("Tên:"), nameField);
        formGrid.addRow(2, new Label("Ngày sinh:"), dobPicker);
        formGrid.addRow(3, new Label("Email:"), emailField);
        formGrid.addRow(4, isCreditBasedCheckBox);
        formGrid.addRow(5, new Label("Tín chỉ yêu cầu:"), requiredCreditsField);
        formGrid.add(messageLabel, 0, 6, 2, 1);

        // Nút chức năng
        Button addButton = new Button("Thêm");
        addButton.setOnAction(e -> addStudent());
        Button updateButton = new Button("Cập nhật");
        updateButton.setOnAction(e -> updateStudent());
        Button deleteButton = new Button("Xóa");
        deleteButton.setOnAction(e -> deleteStudent());
        Button clearButton = new Button("Xóa form");
        clearButton.setOnAction(e -> clearFormFieldsOnly()); // Gọi phương thức mới để chỉ xóa các trường

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // Bảng hiển thị sinh viên
        studentTable = new TableView<>();
        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        TableColumn<Student, String> nameCol = new TableColumn<>("Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Student, LocalDate> dobCol = new TableColumn<>("Ngày sinh");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Student, Float> gpaCol = new TableColumn<>("GPA");
        gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        TableColumn<Student, Boolean> graduationStatusCol = new TableColumn<>("Tốt nghiệp");
        graduationStatusCol.setCellValueFactory(new PropertyValueFactory<>("graduationStatus"));
        TableColumn<Student, String> typeCol = new TableColumn<>("Loại SV");
        typeCol.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof CreditBasedStudent) {
                return new ReadOnlyStringWrapper("Tín chỉ");
            } else if (cellData.getValue() instanceof PartTimeStudent) {
                return new ReadOnlyStringWrapper("Bán thời gian");
            }
            return new ReadOnlyStringWrapper("");
        });

        studentTable.getColumns().addAll(idCol, nameCol, dobCol, emailCol, gpaCol, graduationStatusCol, typeCol);

        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                idField.setText(newSelection.getStudentID());
                nameField.setText(newSelection.getName());
                dobPicker.setValue(newSelection.getDob());
                emailField.setText(newSelection.getEmail());
                if (newSelection instanceof CreditBasedStudent) {
                    isCreditBasedCheckBox.setSelected(true);
                    requiredCreditsField.setManaged(true);
                    requiredCreditsField.setVisible(true);
                    requiredCreditsField.setText(String.valueOf(((CreditBasedStudent) newSelection).getRequiredCredits()));
                } else {
                    isCreditBasedCheckBox.setSelected(false);
                    requiredCreditsField.setManaged(false);
                    requiredCreditsField.setVisible(false);
                    requiredCreditsField.clear();
                }
                // Tự động hiển thị môn học đã đăng ký khi chọn sinh viên
                viewSelectedStudentRegisteredCourses();
                messageLabel.setText(""); // Xóa thông báo khi chọn một sinh viên mới
                messageLabel.setTextFill(Color.BLUE);
            } else {
                clearFormFieldsOnly(); // Khi không có lựa chọn nào, reset form
            }
        });

        // --- Phần xem môn học đã đăng ký của sinh viên ---
        registeredCoursesArea = new TextArea();
        registeredCoursesArea.setEditable(false);
        registeredCoursesArea.setWrapText(true);
        registeredCoursesArea.setPromptText("Môn học đã đăng ký của sinh viên được chọn sẽ hiển thị ở đây.");
        registeredCoursesArea.setPrefRowCount(7);

        viewRegisteredCoursesButton = new Button("Xem Môn học đã ĐK");
        viewRegisteredCoursesButton.setOnAction(e -> viewSelectedStudentRegisteredCourses());


        // --- Cấu trúc layout bằng BorderPane ---
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // VBox cho phần bên trái (Form, Buttons, Table)
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(0, 10, 0, 0)); // Padding bên phải để tạo khoảng cách
        leftPanel.getChildren().addAll(
                new Label("QUẢN LÝ SINH VIÊN"),
                new Separator(),
                formGrid,
                buttonBox,
                studentTable
        );

        // VBox cho phần bên phải (Registered Courses)
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(0, 0, 0, 10)); // Padding bên trái để tạo khoảng cách
        rightPanel.setPrefWidth(300); // Đặt chiều rộng ưu tiên cho panel bên phải
        rightPanel.getChildren().addAll(
                new Label("CÁC MÔN ĐANG HỌC (SINH VIÊN ĐANG CHỌN)"),
                new Separator(),
                viewRegisteredCoursesButton,
                registeredCoursesArea
        );

        mainLayout.setCenter(leftPanel);
        mainLayout.setRight(rightPanel);
    }

    private void loadStudentData() {
        studentObservableList = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentTable.setItems(studentObservableList);
    }

    private void clearFormFieldsOnly() { // Đổi tên để rõ ràng hơn
        idField.clear();
        nameField.clear();
        dobPicker.setValue(null);
        emailField.clear();
        isCreditBasedCheckBox.setSelected(false);
        requiredCreditsField.clear();
        requiredCreditsField.setManaged(false);
        requiredCreditsField.setVisible(false);
        messageLabel.setText(""); // Xóa thông báo khi người dùng nhấn nút "Xóa form"
        messageLabel.setTextFill(Color.BLUE);
        studentTable.getSelectionModel().clearSelection();
        registeredCoursesArea.clear(); // Xóa nội dung khi clear form
    }

    private void addStudent() {
        messageLabel.setText(""); // Xóa thông báo cũ
        messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định

        try {
            String studentId = idField.getText();
            String name = nameField.getText();
            LocalDate dob = dobPicker.getValue();
            String email = emailField.getText();

            if (studentId.isEmpty() || name.isEmpty() || dob == null || email.isEmpty()) {
                messageLabel.setText("Vui lòng điền đầy đủ thông tin.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (smsGUI.findStudentById(studentId) != null) {
                messageLabel.setText("ID sinh viên đã tồn tại.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            Student newStudent;
            if (isCreditBasedCheckBox.isSelected()) {
                int requiredCredits = Integer.parseInt(requiredCreditsField.getText());
                if (requiredCredits <= 0) {
                    messageLabel.setText("Tín chỉ yêu cầu phải là số dương.");
                    messageLabel.setTextFill(Color.RED);
                    return;
                }
                newStudent = new CreditBasedStudent(studentId, name, dob, email, requiredCredits);
            } else {
                newStudent = new PartTimeStudent(studentId, name, dob, email, new ArrayList<>());
            }
            smsGUI.addStudent(newStudent);
            loadStudentData();
            // Không clearFormFieldsOnly() ở đây để thông báo được hiển thị
            messageLabel.setText("Thêm sinh viên thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            messageLabel.setText("Tín chỉ yêu cầu phải là số.");
            messageLabel.setTextFill(Color.RED);
        } catch (DateTimeParseException e) {
            messageLabel.setText("Ngày sinh không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD.");
            messageLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi thêm sinh viên: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void updateStudent() {
        messageLabel.setText(""); // Xóa thông báo cũ
        messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định

        try {
            String studentId = idField.getText();
            Student existingStudent = smsGUI.findStudentById(studentId);

            if (existingStudent == null) {
                messageLabel.setText("Không tìm thấy sinh viên để cập nhật.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Kiểm tra các trường bắt buộc không được rỗng khi cập nhật
            if (nameField.getText().isEmpty() || dobPicker.getValue() == null || emailField.getText().isEmpty()) {
                messageLabel.setText("Vui lòng điền đầy đủ thông tin.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            existingStudent.setName(nameField.getText());
            existingStudent.setDob(dobPicker.getValue());
            existingStudent.setEmail(emailField.getText());

            if (isCreditBasedCheckBox.isSelected()) {
                int requiredCredits = Integer.parseInt(requiredCreditsField.getText());
                if (requiredCredits <= 0) {
                    messageLabel.setText("Tín chỉ yêu cầu phải là số dương.");
                    messageLabel.setTextFill(Color.RED);
                    return;
                }
                if (existingStudent instanceof CreditBasedStudent) {
                    ((CreditBasedStudent) existingStudent).setRequiredCredits(requiredCredits);
                } else {
                    messageLabel.setText("Không thể thay đổi loại sinh viên từ Bán thời gian sang Tín chỉ.");
                    messageLabel.setTextFill(Color.RED);
                    return;
                }
            } else { // Nếu checkbox không được chọn, và sinh viên là CreditBasedStudent
                if (existingStudent instanceof CreditBasedStudent) {
                    messageLabel.setText("Không thể thay đổi loại sinh viên từ Tín chỉ sang Bán thời gian.");
                    messageLabel.setTextFill(Color.RED);
                    return;
                }
                // Nếu là PartTimeStudent và checkbox không được chọn, không làm gì đặc biệt
            }

            smsGUI.updateStudentInfo(existingStudent);
            loadStudentData();
            // Không clearFormFieldsOnly() ở đây để thông báo được hiển thị
            messageLabel.setText("Cập nhật sinh viên thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } catch (NumberFormatException e) {
            messageLabel.setText("Tín chỉ yêu cầu phải là số.");
            messageLabel.setTextFill(Color.RED);
        } catch (DateTimeParseException e) {
            messageLabel.setText("Ngày sinh không hợp lệ. Vui lòng sử dụng định dạng YYYY-MM-DD.");
            messageLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi cập nhật sinh viên: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    private void deleteStudent() {
        messageLabel.setText(""); // Xóa thông báo cũ
        messageLabel.setTextFill(Color.BLUE); // Đặt lại màu mặc định

        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            smsGUI.removeStudent(selectedStudent.getStudentID());
            loadStudentData();
            clearFormFieldsOnly(); // Xóa form và thông báo khi xóa thành công
            messageLabel.setText("Xóa sinh viên thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("Vui lòng chọn sinh viên cần xóa.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    // Phương thức mới để hiển thị môn học đã đăng ký
    private void viewSelectedStudentRegisteredCourses() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            List<Course> registeredCourses = smsGUI.getRegisteredCoursesForStudent(selectedStudent.getStudentID());
            StringBuilder sb = new StringBuilder();
            if (registeredCourses.isEmpty()) {
                sb.append("Sinh viên này chưa đăng ký môn học nào hoặc danh sách môn học cố định rỗng.");
            } else {
                sb.append("Các môn học đã đăng ký của ").append(selectedStudent.getName()).append(":\n");
                for (Course course : registeredCourses) {
                    sb.append("- ").append(course.getCourseID()).append(": ").append(course.getCourseName()).append(" (").append(course.getCredits()).append(" tín chỉ)\n");
                }
            }
            registeredCoursesArea.setText(sb.toString());
        } else {
            registeredCoursesArea.setText("Vui lòng chọn sinh viên từ bảng để xem môn học đã đăng ký.");
        }
    }

    public Parent getStudentView() {
        return mainLayout; // Trả về layout đã được khởi tạo
    }
}
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.*;

public class MainApp extends Application {

    private StudentManagementSystemGUI smsGUI;
    private StackPane centerPane; // Để hoán đổi các chế độ xem khác nhau
    private StudentView studentView;
    private CourseView courseView;
    private GradeView gradeView;
    private ReportView reportView;

    @Override
    public void start(Stage primaryStage) {
        // Khởi tạo hệ thống quản lý sinh viên GUI
        StudentManagementSystemGUI smsGUI = new StudentManagementSystemGUI();

        // Khởi tạo các View
        StudentView studentView = new StudentView(smsGUI);
        CourseView courseView = new CourseView(smsGUI);
        GradeView gradeView = new GradeView(smsGUI);
        ReportView reportView = new ReportView(smsGUI);
        RegistrationView registrationView = new RegistrationView(smsGUI); // Khởi tạo RegistrationView

        // Tạo TabPane để chứa các View
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Không cho phép đóng tab

        // Tạo các Tab và thêm View vào
        Tab studentTab = new Tab("Quản lý Sinh viên", studentView.getStudentView());
        Tab courseTab = new Tab("Quản lý Môn học", courseView.getCourseView());
        Tab registrationTab = new Tab("Đăng ký Môn học", registrationView.getRegistrationView()); // Thêm tab đăng ký
        Tab gradeTab = new Tab("Nhập điểm", gradeView.getGradeInputView());
        Tab reportTab = new Tab("Báo cáo", reportView.getReportView());

        tabPane.getTabs().addAll(studentTab, courseTab, registrationTab, gradeTab, reportTab); // Thêm registrationTab vào danh sách

        Scene scene = new Scene(tabPane, 1000, 700); // Kích thước cửa sổ
        primaryStage.setTitle("Hệ Thống Quản Lý Sinh Viên");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}