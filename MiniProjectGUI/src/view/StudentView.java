package view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
    private TextField requiredCreditsField;
    private Label messageLabel;

    private TextArea registeredCoursesArea;
    private Button viewRegisteredCoursesButton;

    private BorderPane mainLayout;

    public StudentView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
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
        requiredCreditsField.setManaged(false);
        requiredCreditsField.setVisible(false);

        isCreditBasedCheckBox.setOnAction(e -> {
            boolean isCredit = isCreditBasedCheckBox.isSelected();
            requiredCreditsField.setManaged(isCredit);
            requiredCreditsField.setVisible(isCredit);
        });

        messageLabel = new Label("");
        messageLabel.setTextFill(Color.BLUE);

        formGrid.addRow(0, new Label("ID:"), idField);
        formGrid.addRow(1, new Label("Tên:"), nameField);
        formGrid.addRow(2, new Label("Ngày sinh:"), dobPicker);
        formGrid.addRow(3, new Label("Email:"), emailField);
        formGrid.addRow(4, isCreditBasedCheckBox);
        formGrid.addRow(5, new Label("Tín chỉ yêu cầu:"), requiredCreditsField);
        formGrid.add(messageLabel, 0, 6, 2, 1);

        Button addButton = new Button("Thêm");
        addButton.setOnAction(e -> addStudent());
        Button updateButton = new Button("Cập nhật");
        updateButton.setOnAction(e -> updateStudent());
        Button deleteButton = new Button("Xóa");
        deleteButton.setOnAction(e -> deleteStudent());
        Button clearButton = new Button("Xóa form");
        clearButton.setOnAction(e -> clearFormFieldsOnly());

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

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
                viewSelectedStudentRegisteredCourses();
                messageLabel.setText("");
                messageLabel.setTextFill(Color.BLUE);
            } else {
                clearFormFieldsOnly();
            }
        });

        registeredCoursesArea = new TextArea();
        registeredCoursesArea.setEditable(false);
        registeredCoursesArea.setWrapText(true);
        registeredCoursesArea.setPromptText("Môn học đã đăng ký của sinh viên được chọn sẽ hiển thị ở đây.");
        registeredCoursesArea.setPrefRowCount(7);

        viewRegisteredCoursesButton = new Button("Xem Môn học đã ĐK");
        viewRegisteredCoursesButton.setOnAction(e -> viewSelectedStudentRegisteredCourses());

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(0, 10, 0, 0));
        leftPanel.getChildren().addAll(
                new Label("QUẢN LÝ SINH VIÊN"),
                new Separator(),
                formGrid,
                buttonBox,
                studentTable
        );

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(0, 0, 0, 10));
        rightPanel.setPrefWidth(300);
        rightPanel.getChildren().addAll(
                new Label("CÁC MÔN ĐANG HỌC (SINH VIÊN ĐANG CHỌN)"),
                new Separator(),
                viewRegisteredCoursesButton,
                registeredCoursesArea
        );

        mainLayout.setCenter(leftPanel);
        mainLayout.setRight(rightPanel);
    }

    // Make public so smsGUI can call it
    public void loadStudentData() {
        studentObservableList = FXCollections.observableArrayList(smsGUI.getAllStudents());
        studentTable.setItems(studentObservableList);
        studentTable.refresh(); // Explicitly refresh the table
    }

    private void clearFormFieldsOnly() {
        idField.clear();
        nameField.clear();
        dobPicker.setValue(null);
        emailField.clear();
        isCreditBasedCheckBox.setSelected(false);
        requiredCreditsField.clear();
        requiredCreditsField.setManaged(false);
        requiredCreditsField.setVisible(false);
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);
        studentTable.getSelectionModel().clearSelection();
        registeredCoursesArea.clear();
    }

    private void addStudent() {
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);

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
            loadStudentData(); // Calls refresh
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
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);

        try {
            String studentId = idField.getText();
            Student existingStudent = smsGUI.findStudentById(studentId);

            if (existingStudent == null) {
                messageLabel.setText("Không tìm thấy sinh viên để cập nhật.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

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
            } else {
                if (existingStudent instanceof CreditBasedStudent) {
                    messageLabel.setText("Không thể thay đổi loại sinh viên từ Tín chỉ sang Bán thời gian.");
                    messageLabel.setTextFill(Color.RED);
                    return;
                }
            }

            smsGUI.updateStudentInfo(existingStudent);
            loadStudentData(); // Calls refresh
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
        messageLabel.setText("");
        messageLabel.setTextFill(Color.BLUE);

        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            smsGUI.removeStudent(selectedStudent.getStudentID());
            loadStudentData(); // Calls refresh
            clearFormFieldsOnly();
            messageLabel.setText("Xóa sinh viên thành công!");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("Vui lòng chọn sinh viên cần xóa.");
            messageLabel.setTextFill(Color.RED);
        }
    }

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
        return mainLayout;
    }
}