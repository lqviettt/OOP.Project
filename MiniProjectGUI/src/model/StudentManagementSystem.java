package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManagementSystem {
    private List<Student> students;
    private List<Course> courses;
    private List<Grade> grades; // Quản lý điểm số của sinh viên

    public StudentManagementSystem() {
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    // Quản lý sinh viên
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
            System.out.println("Đã thêm sinh viên: " + student.getName() + " (" + student.getStudentID() + ")");
        } else {
            System.out.println("Sinh viên " + student.getStudentID() + " đã tồn tại trong hệ thống.");
        }
    }

    public void updateStudentInfo(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentID().equals(student.getStudentID())) {
                students.set(i, student);
                System.out.println("Đã cập nhật thông tin sinh viên: " + student.getName() + " (" + student.getStudentID() + ")");
                return;
            }
        }
        System.out.println("Không tìm thấy sinh viên với ID: " + student.getStudentID());
    }

    public void removeStudent(String studentID) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentID().equals(studentID)) {
                Student removedStudent = students.remove(i);
                // Cần loại bỏ sinh viên này khỏi tất cả các môn học đã đăng ký
                for (Course course : courses) {
                    course.removeStudent(removedStudent);
                }
                // Cần loại bỏ tất cả điểm số của sinh viên này
                grades.removeIf(grade -> grade.getStudent().getStudentID().equals(studentID));
                System.out.println("Đã xóa sinh viên với ID: " + studentID);
                return;
            }
        }
        System.out.println("Không tìm thấy sinh viên với ID: " + studentID);
    }

    public Student findStudentById(String studentID) {
        return students.stream()
                .filter(student -> student.getStudentID().equals(studentID))
                .findFirst()
                .orElse(null);
    }

    public List<Student> findStudentByName(String name) {
        return students.stream()
                .filter(student -> student.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Student> getAllStudents() {
        return this.students;
    }

    // Quản lý môn học
    public void addCourse(Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
            System.out.println("Đã thêm môn học: " + course.getCourseName() + " (" + course.getCourseID() + ")");
        } else {
            System.out.println("Môn học " + course.getCourseID() + " đã tồn tại trong hệ thống.");
        }
    }

    public void removeCourse(String courseID) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID().equals(courseID)) {
                Course removedCourse = courses.remove(i);
                // Cần loại bỏ môn học này khỏi danh sách đăng ký của tất cả sinh viên
                for (Student student : students) {
                    student.getCourses().remove(removedCourse);
                }
                // Cần loại bỏ tất cả điểm số liên quan đến môn học này
                grades.removeIf(grade -> grade.getCourse().getCourseID().equals(courseID));
                System.out.println("Đã xóa môn học với ID: " + courseID);
                return;
            }
        }
        System.out.println("Không tìm thấy môn học với ID: " + courseID);
    }

    public Course findCourseById(String courseID) {
        return courses.stream()
                .filter(course -> course.getCourseID().equals(courseID))
                .findFirst()
                .orElse(null);
    }

    public List<Course> getAllCourses() {
        return this.courses;
    }

    // Nhập điểm cho sinh viên
    public void inputGrade(String studentID, String courseID, double midtermScore, double finalScore) {
        Student student = findStudentById(studentID);
        Course course = findCourseById(courseID);

        if (student == null) {
            System.out.println("Không tìm thấy sinh viên với ID: " + studentID);
            return;
        }
        if (course == null) {
            System.out.println("Không tìm thấy môn học với ID: " + courseID);
            return;
        }
        if (!student.getCourses().contains(course)) {
            System.out.println("Sinh viên " + student.getName() + " chưa đăng ký môn học " + course.getCourseName() + ".");
            return;
        }

        // Kiểm tra xem đã có điểm cho sinh viên và môn học này chưa
        for (Grade grade : grades) {
            if (grade.getStudent().equals(student) && grade.getCourse().equals(course)) {
                grade.setMidtermScore(midtermScore);
                grade.setFinalScore(finalScore);
                System.out.println("Đã cập nhật điểm cho sinh viên " + student.getName() + " môn " + course.getCourseName());
                course.setFinalGradeForStudent(student, grade.getFinalGrade()); // Cập nhật điểm trong model.Course
                return;
            }
        }

        // Nếu chưa có điểm, tạo mới
        Grade newGrade = new Grade(student, course, midtermScore, finalScore);
        grades.add(newGrade);
        course.setFinalGradeForStudent(student, newGrade.getFinalGrade()); // Lưu điểm cuối kỳ vào model.Course
        System.out.println("Đã nhập điểm cho sinh viên " + student.getName() + " môn " + course.getCourseName());

        if (student instanceof CreditBasedStudent) {
            CreditBasedStudent cbStudent = (CreditBasedStudent) student;
            if (course.isCompletedByStudent(student)) { // Kiểm tra đã đạt điểm đỗ
                cbStudent.addCompletedCourse(course);
            } else {
                cbStudent.removeCompletedCourse(course); // Nếu điểm thay đổi và không còn đỗ
            }
        }
        student.calculateGPA();
    }

    // Kiểm tra điều kiện tốt nghiệp cho sinh viên
    public void checkGraduationForStudent(String studentID) {
        Student student = findStudentById(studentID);
        if (student == null) {
            System.out.println("Không tìm thấy sinh viên với ID: " + studentID);
            return;
        }
        student.calculateGPA(); // Cập nhật GPA trước khi kiểm tra
        if (student.checkGraduation()) {
            student.setGraduationStatus(true);
            System.out.println("Sinh viên " + student.getName() + " (" + student.getStudentID() + ") đủ điều kiện tốt nghiệp.");
        } else {
            student.setGraduationStatus(false);
            System.out.println(student.getGpa() + " GPA");
            System.out.println("Sinh viên " + student.getName() + " (" + student.getStudentID() + ") chưa đủ điều kiện tốt nghiệp.");
        }
    }

    // Hiển thị danh sách sinh viên đã đăng ký một môn học cụ thể
    public void displayCourseEnrollment(String courseID) {
        Course course = findCourseById(courseID);
        if (course == null) {
            System.out.println("Không tìm thấy môn học với ID: " + courseID);
            return;
        }
        System.out.println("\n--- Danh sách sinh viên đã đăng ký môn học " + course.getCourseName() + " (" + course.getCourseID() + ") ---");
        if (course.getEnrolledStudents().isEmpty()) {
            System.out.println("Không có sinh viên nào đăng ký môn học này.");
        } else {
            for (Student student : course.getEnrolledStudents()) {
                System.out.println("- " + student.getName() + " (" + student.getStudentID() + ")");
            }
        }
        System.out.println("------------------------------------------------------------------");
    }

    public void displayStudenst(String studentId) {
        Student studentToViewCourses = findStudentById(studentId);
        if (studentToViewCourses != null) {
            System.out.println("\nDanh sách môn học đã đăng ký của sinh viên " + studentToViewCourses.getName() + ":");
            if (studentToViewCourses.getCourses().isEmpty()) {
                System.out.println("Chưa có môn học nào được đăng ký.");
            } else {
                for (Course course : studentToViewCourses.getCourses()) {
                    System.out.println("- " + course.getCourseName() + " (" + course.getCourseID() + ")");
                }
            }
        } else {
            System.out.println("Không tìm thấy sinh viên với ID: " + studentId);
        }
    }

    // Hiển thị danh sách tất cả sinh viên
    public void displayAllStudents() {
        System.out.println("\n--- Danh sách tất cả sinh viên ---");
        if (students.isEmpty()) {
            System.out.println("Không có sinh viên nào trong hệ thống.");
        } else {
            for (Student student : students) {
                System.out.println(student);
            }
        }
        System.out.println("----------------------------------");
    }

    // Hiển thị danh sách tất cả môn học
    public void displayAllCourses() {
        System.out.println("\n--- Danh sách tất cả môn học ---");
        if (courses.isEmpty()) {
            System.out.println("Không có môn học nào trong hệ thống.");
        } else {
            for (Course course : courses) {
                System.out.println(course);
            }
        }
        System.out.println("-------------------------------");
    }
}