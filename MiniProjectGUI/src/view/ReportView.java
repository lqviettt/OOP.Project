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