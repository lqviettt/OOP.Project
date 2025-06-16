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