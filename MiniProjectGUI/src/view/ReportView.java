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

    // Components for Graduation Check
    private ChoiceBox<Student> studentGraduationChoiceBox;
    private Label graduationStatusLabel;

    // Components for Transcript View
    private ChoiceBox<Student> studentForTranscriptChoiceBox;
    private TextArea reportTextArea;
    private Label transcriptMessageLabel;

    public ReportView(StudentManagementSystemGUI smsGUI) {
        this.smsGUI = smsGUI;
        // Bỏ các lệnh gọi refresh khỏi constructor
        // refreshStudentGraduationChoices(); // <-- BỎ DÒNG NÀY
        // refreshStudentForTranscriptChoices(); // <-- BỎ DÒNG NÀY
    }

    // --- Public methods to get separate UI sections ---

    public Parent getGraduationCheckView() {
        GridPane graduationGrid = new GridPane();
        graduationGrid.setHgap(10);
        graduationGrid.setVgap(10);
        graduationGrid.setPadding(new Insets(10));

        studentGraduationChoiceBox = new ChoiceBox<>(); // Khởi tạo ChoiceBox ở đây
        studentGraduationChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên";
            }
            @Override
            public Student fromString(String string) { return null; }
        });
        refreshStudentGraduationChoices(); // Sau khi khởi tạo, gọi refresh để điền dữ liệu
        studentGraduationChoiceBox.getSelectionModel().selectFirst();

        Button checkGraduationButton = new Button("Kiểm tra Tốt nghiệp");
        checkGraduationButton.setOnAction(e -> checkGraduation());
        graduationStatusLabel = new Label("");
        graduationStatusLabel.setTextFill(Color.BLUE);

        graduationGrid.addRow(0, new Label("Sinh viên:"), studentGraduationChoiceBox);
        graduationGrid.addRow(1, checkGraduationButton);
        graduationGrid.add(graduationStatusLabel, 0, 2, 2, 1);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                new Label("KIỂM TRA ĐIỀU KIỆN TỐT NGHIỆP"),
                new Separator(),
                graduationGrid
        );
        return layout;
    }

    public Parent getTranscriptView() {
        GridPane transcriptGrid = new GridPane();
        transcriptGrid.setHgap(10);
        transcriptGrid.setVgap(10);
        transcriptGrid.setPadding(new Insets(10));

        studentForTranscriptChoiceBox = new ChoiceBox<>(); // Khởi tạo ChoiceBox ở đây
        studentForTranscriptChoiceBox.setConverter(new javafx.util.StringConverter<Student>() {
            @Override
            public String toString(Student student) {
                return student != null ? student.getStudentID() + " - " + student.getName() : "Chọn sinh viên";
            }
            @Override
            public Student fromString(String string) { return null; }
        });
        refreshStudentForTranscriptChoices(); // Sau khi khởi tạo, gọi refresh để điền dữ liệu
        studentForTranscriptChoiceBox.getSelectionModel().selectFirst();

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

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                new Label("XEM BẢNG ĐIỂM CỦA SINH VIÊN"),
                new Separator(),
                transcriptGrid
        );
        return layout;
    }

    // --- Private helper methods (unchanged) ---

    // Make public for external refresh
    public void refreshStudentGraduationChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        // Đảm bảo studentGraduationChoiceBox không null trước khi sử dụng
        if (studentGraduationChoiceBox != null) {
            studentGraduationChoiceBox.setItems(students);
            studentGraduationChoiceBox.getItems().add(0, null);
            studentGraduationChoiceBox.getSelectionModel().select(0);
        }
    }

    // Make public for external refresh
    public void refreshStudentForTranscriptChoices() {
        ObservableList<Student> students = FXCollections.observableArrayList(smsGUI.getAllStudents());
        // Đảm bảo studentForTranscriptChoiceBox không null trước khi sử dụng
        if (studentForTranscriptChoiceBox != null) {
            studentForTranscriptChoiceBox.setItems(students);
            studentForTranscriptChoiceBox.getItems().add(0, null);
            studentForTranscriptChoiceBox.getSelectionModel().select(0);
        }
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
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream old = System.out;
            System.setOut(ps);

            selectedStudent.viewTranscript();

            System.out.flush();
            System.setOut(old);
            reportTextArea.setText(baos.toString());
            transcriptMessageLabel.setTextFill(Color.GREEN);
            transcriptMessageLabel.setText("Đã hiển thị bảng điểm.");
        } else {
            transcriptMessageLabel.setText("Vui lòng chọn một sinh viên để xem bảng điểm.");
            reportTextArea.setText("");
            transcriptMessageLabel.setTextFill(Color.RED);
        }
    }
}