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

        sms.addStudent(new PartTimeStudent("P1", "Tran Thi B", LocalDate.of(1998, 10, 20), "b.tran@example.com", new ArrayList<>(List.of(gt1, gt2, dev))));
        sms.addStudent(new PartTimeStudent("P2", "Hoang Thi E", LocalDate.of(1999, 8, 30), "e.hoang@example.com", new ArrayList<>(List.of(cs, gt1, oop))));
        sms.addStudent(new PartTimeStudent("P3", "Vo Thi H", LocalDate.of(1997, 9, 3), "h.vo@example.com", new ArrayList<>(List.of(gt2, cs))));
        sms.addStudent(new CreditBasedStudent("C1", "Nguyen Van A", LocalDate.of(2003, 5, 15), "a.nguyen@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C2", "Le Thi C", LocalDate.of(2002, 3, 18), "c.le@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C3", "Pham Van D", LocalDate.of(2004, 12, 5), "d.pham@example.com", 4));
        sms.addStudent(new CreditBasedStudent("C4", "Nguyen Van F", LocalDate.of(2001, 7, 22), "f.nguyen@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C5", "Tran Van G", LocalDate.of(2003, 1, 14), "g.tran@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C6", "Bui Van I", LocalDate.of(2000, 11, 27), "i.bui@example.com", 4));
        sms.addStudent(new CreditBasedStudent("C7", "Do Thi J", LocalDate.of(2003, 6, 11), "j.do@example.com", 3));
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
            // Logic đăng ký thực tế sẽ gọi phương thức registerCourse của đối tượng Student
            // Phương thức này đã được ghi đè trong PartTimeStudent để ngăn đăng ký.
            // Trong CreditBasedStudent, nó sẽ xử lý đăng ký.
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