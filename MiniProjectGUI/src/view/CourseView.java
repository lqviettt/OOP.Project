package view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    public CourseView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        initializeUI();
        loadCourseData();
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
        clearButton.setOnAction(e -> clearForm());

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
            } else {
                clearForm();
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


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                new Label("QUẢN LÝ MÔN HỌC"),
                new Separator(),
                formGrid,
                buttonBox,
                courseTable,
                new Separator(), // Thêm phân cách
                new Label("SINH VIÊN ĐÃ ĐĂNG KÝ (MÔN HỌC ĐANG CHỌN)"), // Tiêu đề mới
                viewEnrolledStudentsButton, // Nút xem
                enrolledStudentsArea // Vùng hiển thị
        );
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

    private void clearForm() {
        idField.clear();
        nameField.clear();
        creditsField.clear();
        midtermWeightField.clear();
        finalWeightField.clear();
        maxCapacityField.clear();
        prerequisiteChoiceBox.getSelectionModel().select(0); // Chọn "Không có môn tiên quyết"
        messageLabel.setText("");
        courseTable.getSelectionModel().clearSelection();
        enrolledStudentsArea.clear(); // Xóa nội dung khi clear form
    }

    private void addCourse() {
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
                return;
            }
            if (midtermWeight + finalWeight != 100) {
                messageLabel.setText("Tổng trọng số giữa kỳ và cuối kỳ phải là 100%.");
                return;
            }

            if (smsGUI.findCourseById(courseId) != null) {
                messageLabel.setText("ID môn học đã tồn tại.");
                return;
            }

            Course newCourse = new Course(courseId, name, credits, midtermWeight, finalWeight, maxCapacity, prerequisite);
            smsGUI.addCourse(newCourse);
            loadCourseData(); // Tải lại dữ liệu sau khi thêm
            clearForm();
            messageLabel.setText("Thêm môn học thành công!");
        } catch (NumberFormatException e) {
            messageLabel.setText("Tín chỉ, trọng số và sức chứa phải là số nguyên.");
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi thêm môn học: " + e.getMessage());
        }
    }

    private void updateCourse() {
        try {
            String courseId = idField.getText();
            Course existingCourse = smsGUI.findCourseById(courseId);

            if (existingCourse == null) {
                messageLabel.setText("Không tìm thấy môn học để cập nhật.");
                return;
            }

            existingCourse.setCourseName(nameField.getText());
            existingCourse.setCredits(Integer.parseInt(creditsField.getText()));
            existingCourse.setMidtermWeight(Integer.parseInt(midtermWeightField.getText()));
            existingCourse.setFinalWeight(Integer.parseInt(finalWeightField.getText()));
            existingCourse.setMaxCapacity(Integer.parseInt(maxCapacityField.getText()));
            existingCourse.setPrerequisite(prerequisiteChoiceBox.getSelectionModel().getSelectedItem());

            if (existingCourse.getMidtermWeight() + existingCourse.getFinalWeight() != 100) {
                messageLabel.setText("Tổng trọng số giữa kỳ và cuối kỳ phải là 100%.");
                return;
            }

            loadCourseData(); // Tải lại dữ liệu sau khi cập nhật
            clearForm();
            messageLabel.setText("Cập nhật môn học thành công!");
        } catch (NumberFormatException e) {
            messageLabel.setText("Tín chỉ, trọng số và sức chứa phải là số nguyên.");
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("Lỗi khi cập nhật môn học: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            smsGUI.removeCourse(selectedCourse.getCourseID());
            loadCourseData(); // Tải lại dữ liệu sau khi xóa
            clearForm();
            messageLabel.setText("Xóa môn học thành công!");
        } else {
            messageLabel.setText("Vui lòng chọn môn học cần xóa.");
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
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));

        idField = new TextField(); idField.setPromptText("ID Môn học");
        nameField = new TextField(); nameField.setPromptText("Tên Môn học");
        creditsField = new TextField(); creditsField.setPromptText("Số tín chỉ");
        midtermWeightField = new TextField(); midtermWeightField.setPromptText("Trọng số giữa kỳ (%)");
        finalWeightField = new TextField(); finalWeightField.setPromptText("Trọng số cuối kỳ (%)");
        maxCapacityField = new TextField(); maxCapacityField.setPromptText("Số lượng tối đa");
        prerequisiteChoiceBox = new ChoiceBox<>();
        prerequisiteChoiceBox.setConverter(new javafx.util.StringConverter<Course>() {
            @Override
            public String toString(Course course) { return course != null ? course.getCourseID() + " - " + course.getCourseName() : "Không có môn tiên quyết"; }
            @Override
            public Course fromString(String string) { return null; }
        });
        messageLabel = new Label("");

        formGrid.addRow(0, new Label("ID:"), idField);
        formGrid.addRow(1, new Label("Tên:"), nameField);
        formGrid.addRow(2, new Label("Tín chỉ:"), creditsField);
        formGrid.addRow(3, new Label("Trọng số GK:"), midtermWeightField);
        formGrid.addRow(4, new Label("Trọng số CK:"), finalWeightField);
        formGrid.addRow(5, new Label("SL Tối đa:"), maxCapacityField);
        formGrid.addRow(6, new Label("Tiên quyết:"), prerequisiteChoiceBox);
        formGrid.add(messageLabel, 0, 7, 2, 1);

        Button addButton = new Button("Thêm"); addButton.setOnAction(e -> addCourse());
        Button updateButton = new Button("Cập nhật"); updateButton.setOnAction(e -> updateCourse());
        Button deleteButton = new Button("Xóa"); deleteButton.setOnAction(e -> deleteCourse());
        Button clearButton = new Button("Xóa form"); clearButton.setOnAction(e -> clearForm());

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

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
                viewSelectedCourseEnrolledStudents(); // Display enrolled students for newly selected course
            } else {
                clearForm();
            }
        });

        // New section for enrolled students
        enrolledStudentsArea = new TextArea();
        enrolledStudentsArea.setEditable(false);
        enrolledStudentsArea.setWrapText(true);
        enrolledStudentsArea.setPromptText("Sinh viên đã đăng ký môn học được chọn sẽ hiển thị ở đây.");
        enrolledStudentsArea.setPrefRowCount(7);
        viewEnrolledStudentsButton = new Button("Xem SV đã ĐK");
        viewEnrolledStudentsButton.setOnAction(e -> viewSelectedCourseEnrolledStudents());


        loadCourseData(); // Load data when view is requested

        layout.getChildren().addAll(
                new Label("QUẢN LÝ MÔN HỌC"),
                new Separator(),
                formGrid,
                buttonBox,
                courseTable,
                new Separator(),
                new Label("SINH VIÊN ĐÃ ĐĂNG KÝ (MÔN HỌC ĐANG CHỌN)"),
                viewEnrolledStudentsButton,
                enrolledStudentsArea
        );
        return layout;
    }
}
