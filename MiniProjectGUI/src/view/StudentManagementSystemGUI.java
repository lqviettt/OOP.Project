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

    // Add references to views that need to be refreshed
    private RegistrationView registrationView;
    private GradeView gradeView;
    private ReportView reportView;
    private StudentView studentView; // <<--- THÊM DÒNG NÀY
    private CourseView courseView;

    public StudentManagementSystemGUI() {
        this.sms = new StudentManagementSystem();
        initializeSampleData();
    }

    // Setters for the views, called from MainApp after views are initialized
    public void setRegistrationView(RegistrationView registrationView) {
        this.registrationView = registrationView;
    }

    public void setGradeView(GradeView gradeView) {
        this.gradeView = gradeView;
    }

    public void setReportView(ReportView reportView) {
        this.reportView = reportView;
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
        // Notify other views about data change
        if (registrationView != null) registrationView.refreshStudentChoices();
        if (gradeView != null) gradeView.refreshStudentAndCourseChoices();
        if (reportView != null) reportView.refreshStudentGraduationChoices();
        if (reportView != null) reportView.refreshStudentForTranscriptChoices();
    }

    public void updateStudentInfo(Student student) {
        sms.updateStudentInfo(student);
        // Notify other views about data change
        if (registrationView != null) registrationView.refreshStudentChoices();
        if (gradeView != null) gradeView.refreshStudentAndCourseChoices();
        if (reportView != null) reportView.refreshStudentGraduationChoices();
        if (reportView != null) reportView.refreshStudentForTranscriptChoices();
    }

    public void removeStudent(String studentID) {
        sms.removeStudent(studentID);
        // Notify other views about data change
        if (registrationView != null) registrationView.refreshStudentChoices();
        if (gradeView != null) gradeView.refreshStudentAndCourseChoices();
        if (reportView != null) reportView.refreshStudentGraduationChoices();
        if (reportView != null) reportView.refreshStudentForTranscriptChoices();
    }

    public List<Course> getAllCourses() {
        return sms.getAllCourses();
    }

    public Course findCourseById(String courseID) {
        return sms.findCourseById(courseID);
    }

    public void addCourse(Course course) {
        sms.addCourse(course);
        // Notify other views about data change
        if (registrationView != null) registrationView.refreshCourseChoices();
        if (gradeView != null) gradeView.refreshStudentAndCourseChoices();
    }

    public void removeCourse(String courseID) {
        sms.removeCourse(courseID);
        // Notify other views about data change
        if (registrationView != null) registrationView.refreshCourseChoices();
        if (gradeView != null) gradeView.refreshStudentAndCourseChoices();
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
            boolean success = student.registerCourse(course);
            if (success) {
                // Important: Refresh course view to update current enrollment count
                if (courseView != null) courseView.loadCourseData(); // Assuming courseView has a public loadCourseData or similar
                // Refresh student view to update registered courses displayed (if implemented)
                if (studentView != null) studentView.loadStudentData();
            }
            return success;
        }
        return false;
    }

    // Add a setter for studentView
    public void setStudentView(StudentView studentView) {
        this.studentView = studentView;
    }

    // Add a setter for courseView
    public void setCourseView(CourseView courseView) {
        this.courseView = courseView;
    }


    public List<Course> getRegisteredCoursesForStudent(String studentId) {
        Student student = sms.findStudentById(studentId);
        if (student != null) {
            return student.viewRegisteredCourses();
        }
        return new ArrayList<>();
    }

    public List<Student> getEnrolledStudentsInCourse(String courseID) {
        Course course = sms.findCourseById(courseID);
        if (course != null) {
            return course.getEnrolledStudents();
        }
        return new ArrayList<>();
    }
}