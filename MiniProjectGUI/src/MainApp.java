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
    private StackPane centerPane;
    private StudentView studentView;
    private CourseView courseView;
    private GradeView gradeView;
    private ReportView reportView; // Keep reference to ReportView
    private RegistrationView registrationView;

    @Override
    public void start(Stage primaryStage) {
        smsGUI = new StudentManagementSystemGUI();

        studentView = new StudentView(smsGUI);
        courseView = new CourseView(smsGUI);
        gradeView = new GradeView(smsGUI);
        reportView = new ReportView(smsGUI); // Initialize ReportView
        registrationView = new RegistrationView(smsGUI);

        // Pass references to other views to smsGUI so it can notify them
        smsGUI.setStudentView(studentView);
        smsGUI.setCourseView(courseView);
        smsGUI.setRegistrationView(registrationView);
        smsGUI.setGradeView(gradeView);
        smsGUI.setReportView(reportView); // Pass reportView to smsGUI

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab studentTab = new Tab("Quản lý Sinh viên", studentView.getStudentView());
        Tab courseTab = new Tab("Quản lý Môn học", courseView.getCourseView());
        Tab registrationTab = new Tab("Đăng ký Môn học", registrationView.getRegistrationView());
        Tab gradeTab = new Tab("Nhập điểm", gradeView.getGradeInputView());

        // New tabs for Graduation Check and Transcript
        Tab graduationCheckTab = new Tab("Kiểm tra Tốt nghiệp", reportView.getGraduationCheckView()); // New Tab
        Tab transcriptTab = new Tab("Xem Bảng điểm", reportView.getTranscriptView()); // New Tab

        // Add all tabs to the TabPane
        tabPane.getTabs().addAll(
                studentTab,
                courseTab,
                registrationTab,
                gradeTab,
                graduationCheckTab, // Add new tab
                transcriptTab       // Add new tab
        );

        Scene scene = new Scene(tabPane, 1000, 700);
        primaryStage.setTitle("Hệ Thống Quản Lý Sinh Viên");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}