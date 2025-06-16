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
                new Label("MÔN HỌC ĐÃ ĐĂNG KÝ (SINH VIÊN ĐANG CHỌN)"),
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