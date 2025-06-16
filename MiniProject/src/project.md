import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Course {
private String courseID;
private String courseName;
private int credits;
private int midtermWeight;
private int finalWeight;
private Course prerequisite;
private int maxCapacity;
private int currentEnrollment;
private final List<Student> enrolledStudents;
private final Map<Student, Double> studentFinalGrades;

    public Course(String courseID, String courseName, int credits, int midtermWeight, int finalWeight, int maxCapacity, Course prerequisite) {
        this.courseID = Objects.requireNonNull(courseID, "Course ID cannot be null");
        this.courseName = Objects.requireNonNull(courseName, "Course name cannot be null");
        if (midtermWeight + finalWeight != 100) {
            throw new IllegalArgumentException("Midterm and final weights must sum to 100");
        }
        if (credits <= 0 || maxCapacity <= 0) {
            throw new IllegalArgumentException("Credits and max capacity must be positive");
        }
        this.credits = credits;
        this.midtermWeight = midtermWeight;
        this.finalWeight = finalWeight;
        this.prerequisite = prerequisite;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = 0;
        this.enrolledStudents = new ArrayList<>();
        this.studentFinalGrades = new HashMap<>();
    }


    public Course(String courseID, String courseName, int credits, int midtermWeight, int finalWeight, int maxCapacity) {
        this(courseID, courseName, credits, midtermWeight, finalWeight, maxCapacity, null);
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = Objects.requireNonNull(courseID, "Course ID cannot be null");
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = Objects.requireNonNull(courseName, "Course name cannot be null");
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.credits = credits;
    }

    public int getMidtermWeight() {
        return midtermWeight;
    }

    public void setMidtermWeight(int midtermWeight) {
        if (midtermWeight + this.finalWeight != 100) {
            throw new IllegalArgumentException("Midterm and final weights must sum to 100");
        }
        this.midtermWeight = midtermWeight;
    }

    public int getFinalWeight() {
        return finalWeight;
    }

    public void setFinalWeight(int finalWeight) {
        if (this.midtermWeight + finalWeight != 100) {
            throw new IllegalArgumentException("Midterm and final weights must sum to 100");
        }
        this.finalWeight = finalWeight;
    }

    public Course getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Course prerequisite) {
        this.prerequisite = prerequisite;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive");
        }
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    private void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public List<Student> getEnrolledStudents() {
        return new ArrayList<>(enrolledStudents);
    }

    public boolean addStudent(Student student) {
        if (student == null || isCourseFull() || enrolledStudents.contains(student)) {
            return false;
        }
        enrolledStudents.add(student);
        setCurrentEnrollment(getCurrentEnrollment() + 1);
        return true;
    }

    public boolean removeStudent(Student student) {
        if (student == null || !enrolledStudents.contains(student)) {
            return false;
        }
        enrolledStudents.remove(student);
        setCurrentEnrollment(getCurrentEnrollment() - 1);
        studentFinalGrades.remove(student);
        return true;
    }

    public boolean isCourseFull() {
        return currentEnrollment >= maxCapacity;
    }

    public boolean hasPrerequisite() {
        return prerequisite != null;
    }

    public double calculateFinalGrade(double midtermScore, double finalScore) {
        if (midtermScore < 0 || finalScore < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }
        return (midtermScore * midtermWeight + finalScore * finalWeight) / 100.0;
    }

    public void setFinalGradeForStudent(Student student, double finalGrade) {
        if (!enrolledStudents.contains(student) || finalGrade < 0) {
            throw new IllegalArgumentException("Invalid student or grade");
        }
        studentFinalGrades.put(student, finalGrade);
    }

    public Double getFinalGradeForStudent(Student student) {
        return studentFinalGrades.get(student);
    }

    public boolean isCompletedByStudent(Student student) {
        Double finalGrade = studentFinalGrades.get(student);
        if (finalGrade == null) {
            return false;
        }
        return finalGrade > 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseID, course.courseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseID);
    }

    @Override
    public String toString() {
        return String.format("CourseID: %s, Name: %s, Credits: %d", courseID, courseName, credits);
    }
}
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreditBasedStudent extends Student {
private List<Course> completedCourses; // Danh sách các môn học đã hoàn thành
private int requiredCredits; // Số tín chỉ cần thiết để tốt nghiệp

    public CreditBasedStudent(String studentID, String name, LocalDate dob, String email, int requiredCredits) {
        super(studentID, name, dob, email);
        this.completedCourses = new ArrayList<>();
        this.requiredCredits = requiredCredits;
    }

    public List<Course> getCompletedCourses() {
        return new ArrayList<>(completedCourses); // Return a copy for immutability
    }

    public void setCompletedCourses(List<Course> completedCourses) {
        if (completedCourses != null) {
            this.completedCourses = new ArrayList<>(completedCourses); // Ensure the input list is copied
        }
    }

    public int getRequiredCredits() {
        return requiredCredits;
    }

    public void setRequiredCredits(int requiredCredits) {
        if (requiredCredits > 0) {
            this.requiredCredits = requiredCredits;
        }
    }

    @Override
    public boolean registerCourse(Course course) {
        if (course == null) {
            System.out.println("Môn học không hợp lệ.");
            return false;
        }

        if (getCourses().contains(course)) {
            System.out.println("Sinh viên " + getName() + " đã đăng ký môn học " + course.getCourseName() + ".");
            return false;
        }

        if (course.hasPrerequisite() && !getCompletedCourses().contains(course.getPrerequisite())) {
            System.out.println("Sinh viên " + getName() + " chưa hoàn thành môn tiên quyết " + course.getPrerequisite().getCourseName() + " cho môn " + course.getCourseName() + ".");
            return false;
        }

        if (course.isCourseFull()) {
            System.out.println("Môn học " + course.getCourseName() + " đã đầy chỗ.");
            return false;
        }

        getCourses().add(course);
        course.addStudent(this);
        System.out.println("Sinh viên " + getName() + " đã đăng ký thành công môn học " + course.getCourseName() + ".");
        return true;
    }

    @Override
    public void calculateGPA() {
        if (completedCourses.isEmpty()) {
            setGpa(0.0f);
            return;
        }

        double totalGradePoints = 0;
        int totalCredits = 0;

        for (Course course : completedCourses) {
            Double finalGrade = course.getFinalGradeForStudent(this);
            if (finalGrade != null) {
                totalGradePoints += Utils.convertTo4PointScale(finalGrade) * course.getCredits();
                totalCredits += course.getCredits();
            }
        }

        setGpa(totalCredits > 0 ? (float) (totalGradePoints / totalCredits) : 0.0f);
    }

    @Override
    public boolean checkGraduation() {
        calculateGPA();
        return completedCourses.stream().mapToInt(Course::getCredits).sum() >= requiredCredits && getGpa() >= 2.0f;
    }

    @Override
    public void viewTranscript() {
        System.out.println("\n--- Bảng điểm của sinh viên " + getName() + " (" + getStudentID() + ") ---");
        System.out.println("Các môn học đã hoàn thành:");
        if (completedCourses.isEmpty()) {
            System.out.println("Chưa có môn học nào hoàn thành (đạt điểm đỗ).");
        } else {
            for (Course course : completedCourses) {
                Double finalGrade = course.getFinalGradeForStudent(this);
                // Chỉ hiển thị môn học đã hoàn thành với điểm số và trạng thái đỗ/trượt
                if (finalGrade != null) {
                    String status = finalGrade >= 4 ? "ĐỖ" : "TRƯỢT";
                    System.out.println("- " + course.getCourseName() + " (" + course.getCourseID() + "): " + String.format("%.2f", finalGrade) + " - " + status);
                } else {
                    System.out.println("- " + course.getCourseName() + " (" + course.getCourseID() + "): N/A (chưa có điểm)");
                }
            }
        }
        System.out.println("Tổng tín chỉ đã hoàn thành: " + completedCourses.stream().filter(course -> course.isCompletedByStudent(this)).mapToInt(Course::getCredits).sum());
        System.out.println("GPA tích lũy: " + String.format("%.2f", getGpa()));
        System.out.println("Trạng thái tốt nghiệp: " + (checkGraduation() ? "Đủ điều kiện" : "Chưa đủ điều kiện"));
        System.out.println("--------------------------------------------------");
    }

    public void addCompletedCourse(Course course) {
        if (!completedCourses.contains(course)) {
            completedCourses.add(course);
        }
    }

    public void removeCompletedCourse(Course course) {
        completedCourses.remove(course);
    }
}

public class Grade {
private Student student;
private Course course;
private double midtermScore;
private double finalScore;
private double finalGrade; // Điểm cuối cùng đã được tính

    public Grade(Student student, Course course, double midtermScore, double finalScore) {
        this.student = student;
        this.course = course;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
        this.finalGrade = course.calculateFinalGrade(midtermScore, finalScore); // Tính điểm cuối cùng ngay khi tạo
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public double getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(double midtermScore) {
        this.midtermScore = midtermScore;
        this.finalGrade = course.calculateFinalGrade(this.midtermScore, this.finalScore); // Cập nhật điểm cuối cùng khi điểm giữa kỳ thay đổi
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
        this.finalGrade = course.calculateFinalGrade(this.midtermScore, this.finalScore); // Cập nhật điểm cuối cùng khi điểm cuối kỳ thay đổi
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    @Override
    public String toString() {
        return "StudentID: " + student.getStudentID() + ", Course: " + course.getCourseName() +
                ", Midterm: " + String.format("%.2f", midtermScore) + ", Final: " + String.format("%.2f", finalScore) +
                ", Final Grade: " + String.format("%.2f", finalGrade);
    }
}
import java.time.LocalDate;
import java.util.List;

public class PartTimeStudent extends Student {
private List<Course> fixedCourseList; // Danh sách môn học cố định cần hoàn thành

    public PartTimeStudent(String studentID, String name, LocalDate dob, String email, List<Course> fixedCourseList) {
        super(studentID, name, dob, email);
        setFixedCourseList(fixedCourseList); // Use setter to handle initialization logic
    }

    // Getter cho fixedCourseList
    public List<Course> getFixedCourseList() {
        return fixedCourseList;
    }

    public void setFixedCourseList(List<Course> fixedCourseList) {
        if (fixedCourseList == null) {
            throw new IllegalArgumentException("Danh sách môn học cố định không được null.");
        }
        this.fixedCourseList = fixedCourseList;
        // Cập nhật lại danh sách môn học đã đăng ký nếu danh sách cố định thay đổi
        getCourses().clear();
        getCourses().addAll(fixedCourseList);
        // Cập nhật danh sách sinh viên trong mỗi môn học đã thêm
        for (Course course : fixedCourseList) {
            if (!course.getEnrolledStudents().contains(this)) {
                course.addStudent(this);
            }
        }
    }

    // Ghi đè phương thức registerCourse từ lớp cha
    @Override
    public boolean registerCourse(Course course) {
        System.out.println("Sinh viên hệ vừa học vừa làm không được phép tự đăng ký môn học.");
        return false; // Không cho phép đăng ký tự do
    }

    // Ghi đè phương thức calculateGPA từ lớp cha
    @Override
    public void calculateGPA() {
        if (fixedCourseList.isEmpty()) {
            setGpa(0.0f); // GPA mặc định nếu không có môn học
            return;
        }

        double totalGradePoints = 0;
        int totalCredits = 0;

        // Tính GPA dựa trên các môn học đã hoàn thành
        for (Course course : fixedCourseList) {
            if (course.isCompletedByStudent(this)) {
                Double finalGrade = course.getFinalGradeForStudent(this);
                if (finalGrade != null) {
                    double finalScale = Utils.convertTo4PointScale(finalGrade);
                    totalGradePoints += finalScale * course.getCredits();
                    totalCredits += course.getCredits();
                }
            }
        }

        if (totalCredits != 0) {
            setGpa((float) (totalGradePoints / totalCredits));
        } else {
            setGpa(0.0f); // Nếu không có tín chỉ được hoàn thành, GPA là 0.0
        }
    }

    // Ghi đè phương thức checkGraduation từ lớp cha
    @Override
    public boolean checkGraduation() {
        calculateGPA(); // Cập nhật GPA trước khi kiểm tra
        // Kiểm tra xem tất cả các môn học trong danh sách cố định đã được hoàn thành
        // và GPA >= 2.0
        return fixedCourseList.stream().allMatch(course -> course.isCompletedByStudent(this)) && getGpa() >= 2.0f;
    }

    // Ghi đè phương thức viewTranscript từ lớp cha
    @Override
    public void viewTranscript() {
        System.out.println("\n--- Bảng điểm của sinh viên " + getName() + " (" + getStudentID() + ") ---");
        System.out.println("Chương trình học cố định:");
        calculateGPA();
        if (fixedCourseList.isEmpty()) {
            System.out.println("Chưa có môn học nào trong chương trình.");
        } else {
            for (Course course : fixedCourseList) {
                Double finalGrade = course.getFinalGradeForStudent(this);
                String grade = finalGrade != null ? Utils.formatGrade(finalGrade) : "Chưa có điểm";
                String status = course.isCompletedByStudent(this) ? "Đã hoàn thành" : "Chưa hoàn thành";
                System.out.println("- " + course.getCourseName() + " (" + course.getCourseID() + "): " + grade + " - Trạng thái: " + status);
            }
        }
        System.out.println("GPA tích lũy: " + String.format("%.2f", getGpa()));
        System.out.println("Trạng thái tốt nghiệp: " + (checkGraduation() ? "Đủ điều kiện" : "Chưa đủ điều kiện"));
        System.out.println("--------------------------------------------------");
    }
}

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Student {
private String studentID;
private String name;
private LocalDate dob;
private String email;
private float gpa;
private boolean graduationStatus;
private List<Course> courses; // Danh sách các môn học sinh viên đã đăng ký

    public Student(String studentID, String name, LocalDate dob, String email) {
        this.studentID = studentID;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.gpa = 0.0f; // Mặc định GPA là 0 khi mới tạo
        this.graduationStatus = false; // Mặc định trạng thái tốt nghiệp là false
        this.courses = new ArrayList<>();
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getGpa() {
        return gpa;
    }

    protected void setGpa(float gpa) { // Chỉ lớp con mới được phép set GPA trực tiếp
        this.gpa = gpa;
    }

    public boolean isGraduationStatus() {
        return graduationStatus;
    }

    protected void setGraduationStatus(boolean graduationStatus) { // Chỉ lớp con mới được phép set trạng thái tốt nghiệp
        this.graduationStatus = graduationStatus;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public abstract boolean registerCourse(Course course);

    public abstract void calculateGPA();

    public abstract boolean checkGraduation();

    public List<Course> viewRegisteredCourses() {
        return this.courses;
    }

    public abstract void viewTranscript();

    @Override
    public String toString() {
        return "StudentID: " + studentID + ", Name: " + name + ", DOB: " + dob + ", Email: " + email + ", GPA: " + String.format("%.2f", gpa) + ", Graduation Status: " + (graduationStatus ? "Yes" : "No");
    }
}
//package com.yourpackage.service; // Đặt package phù hợp
//
//import com.yourpackage.model.AbstractStudent;
//import com.yourpackage.model.Course;
//import com.yourpackage.model.CreditBasedStudent;
//import com.yourpackage.model.Grade;
//import com.yourpackage.model.PartTimeStudent;

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
                course.setFinalGradeForStudent(student, grade.getFinalGrade()); // Cập nhật điểm trong Course
                return;
            }
        }

        // Nếu chưa có điểm, tạo mới
        Grade newGrade = new Grade(student, course, midtermScore, finalScore);
        grades.add(newGrade);
        course.setFinalGradeForStudent(student, newGrade.getFinalGrade()); // Lưu điểm cuối kỳ vào Course
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
import java.text.SimpleDateFormat;
import java.util.*;

class Utils {
public static String formatName(String name) {
return name.trim().toUpperCase();
}

    public static Date formatDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static double convertTo4PointScale(double score10) {
        if (score10 >= 9.5) return 4.0;      // A+
        else if (score10 >= 8.5) return 4.0; // A
        else if (score10 >= 8.0) return 3.5; // B+
        else if (score10 >= 7.0) return 3.0; // B
        else if (score10 >= 6.5) return 2.5; // C+
        else if (score10 >= 5.5) return 2.0; // C
        else if (score10 >= 5.0) return 1.5; // D+
        else if (score10 >= 4.0) return 1.0; // D
        else return 0.0;                     // F
    }

    public static String formatGrade(Double grade) {
        if (grade == null || grade == 0.0) return "N/A";
        return String.format("%.2f", grade);
    }

}
//package com.yourpackage.main; // Đặt package phù hợp
//
//import com.yourpackage.model.CreditBasedStudent;
//import com.yourpackage.model.Course;
//import com.yourpackage.model.PartTimeStudent;
//import com.yourpackage.service.StudentManagementSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainConsole {
public static void main(String[] args) {
StudentManagementSystem sms = new StudentManagementSystem();
Scanner scanner = new Scanner(System.in);

        // Tạo một số sinh viên và môn học mẫu
        Course gt1 = new Course("GT1", "Giải tích 1", 3, 30, 70, 50);
        Course gt2 = new Course("GT2", "Giải tích 2", 3, 30, 70, 50);
        Course dev = new Course("DEV", "Lập trình cơ bản", 4, 40, 60, 40, gt1); // PROG101 có MATH101 là tiên quyết
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

        sms.addStudent(new PartTimeStudent("P1", "Tran Thi B", LocalDate.of(1998, 10, 20), "b.tran@example.com", Arrays.asList(gt1, gt2, dev)));
        sms.addStudent(new PartTimeStudent("P2", "Hoang Thi E", LocalDate.of(1999, 8, 30), "e.hoang@example.com", Arrays.asList(cs, gt1, oop)));
        sms.addStudent(new PartTimeStudent("P3", "Vo Thi H", LocalDate.of(1997, 9, 3), "h.vo@example.com", Arrays.asList(gt2, cs)));
        sms.addStudent(new CreditBasedStudent("C1", "Nguyen Van A", LocalDate.of(2003, 5, 15), "a.nguyen@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C2", "Le Thi C", LocalDate.of(2002, 3, 18), "c.le@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C3", "Pham Van D", LocalDate.of(2004, 12, 5), "d.pham@example.com", 4));
        sms.addStudent(new CreditBasedStudent("C4", "Nguyen Van F", LocalDate.of(2001, 7, 22), "f.nguyen@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C5", "Tran Van G", LocalDate.of(2003, 1, 14), "g.tran@example.com", 3));
        sms.addStudent(new CreditBasedStudent("C6", "Bui Van I", LocalDate.of(2000, 11, 27), "i.bui@example.com", 4));
        sms.addStudent(new CreditBasedStudent("C7", "Do Thi J", LocalDate.of(2003, 6, 11), "j.do@example.com", 3));


        // Menu điều khiển
        int choice;
        do {
            System.out.println("\n--- Hệ Thống Quản Lý Sinh Viên (Console) ---");
            System.out.println("1. Thêm sinh viên");
            System.out.println("2. Xóa sinh viên");
            System.out.println("3. Thêm môn học");
            System.out.println("4. Xóa môn học");
            System.out.println("5. Đăng ký môn học (SV tín chỉ)");
            System.out.println("6. Nhập điểm cho sinh viên");
            System.out.println("7. Xem bảng điểm của sinh viên");
            System.out.println("8. Kiểm tra tốt nghiệp");
            System.out.println("9. Hiển thị danh sách sinh viên");
            System.out.println("10. Hiển thị danh sách môn học");
            System.out.println("11. Hiển thị danh sách môn đang học của sinh viên");
            System.out.println("12. Hiển thị danh sách sinh viên đã đăng ký môn học");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng new line

            switch (choice) {
                case 1:
                    System.out.println("Thêm sinh viên mới:");
                    System.out.print("Nhập ID sinh viên: ");
                    String id = scanner.nextLine();
                    System.out.print("Nhập tên sinh viên: ");
                    String name = scanner.nextLine();
                    System.out.print("Nhập ngày sinh (yyyy-mm-dd): ");
                    LocalDate dob = LocalDate.parse(scanner.nextLine());
                    System.out.print("Nhập email sinh viên: ");
                    String email = scanner.nextLine();
                    System.out.print("Sinh viên tín chỉ? (true/false): ");
                    boolean isCreditBased = scanner.nextBoolean();
                    scanner.nextLine(); // Đọc dòng new line

                    if (isCreditBased) {
                        System.out.print("Nhập tín chỉ yêu cầu để tốt nghiệp: ");
                        int requiredCredits = scanner.nextInt();
                        scanner.nextLine(); // Đọc dòng new line
                        CreditBasedStudent student = new CreditBasedStudent(id, name, dob, email, requiredCredits);
                        sms.addStudent(student);
                    } else {
                        PartTimeStudent student = new PartTimeStudent(id, name, dob, email, new ArrayList<>());
                        sms.addStudent(student);
                    }
                    System.out.println("Đã thêm sinh viên thành công.");
                    break;
                case 2:
                    System.out.print("Nhập ID sinh viên cần xóa: ");
                    String studentIdToDelete = scanner.nextLine();
                    sms.removeStudent(studentIdToDelete);
                    break;
                case 3:
                    System.out.println("Thêm môn học mới:");
                    System.out.print("Nhập ID môn học: ");
                    String courseId = scanner.nextLine();
                    System.out.print("Nhập tên môn học: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Nhập số tín chỉ: ");
                    int credits = scanner.nextInt();
                    System.out.print("Nhập số lượng sinh viên tối đa: ");
                    int capacity = scanner.nextInt();
                    System.out.print("Nhập trọng số của điểm giữa kỳ (%): ");
                    int midtermWeight = scanner.nextInt();
                    System.out.print("Nhập trọng số của điểm cuối kỳ (%): ");
                    int finalWeight = scanner.nextInt();
                    scanner.nextLine(); // Đọc dòng new line

                    System.out.print("ID môn học tiên quyết (để trống nếu không có): ");
                    String prerequisiteId = scanner.nextLine();

                    Course prerequisiteCourse = prerequisiteId.isEmpty() ? null : sms.findCourseById(prerequisiteId);

                    Course newCourse = new Course(courseId, courseName, credits, capacity, midtermWeight, finalWeight, prerequisiteCourse);
                    sms.addCourse(newCourse);
                    System.out.println("Đã thêm môn học mới thành công.");

                    break;
                case 4:
                    System.out.print("Nhập ID môn học cần xóa: ");
                    String courseIdToDelete = scanner.nextLine();
                    sms.removeCourse(courseIdToDelete);
                    break;
                case 5:
                    System.out.print("Nhập ID sinh viên: ");
                    String studentIdToRegister = scanner.nextLine();
                    Student studentToRegister = sms.findStudentById(studentIdToRegister);
                    if (studentToRegister instanceof PartTimeStudent) {
                        System.out.println("Sinh viên thuộc hệ vừa học vừa làm không thể đăng ký.");
                        break;
                    } else {
                        System.out.print("Nhập ID môn học muốn đăng ký: ");
                        String courseIdToRegister = scanner.nextLine();
                        Course courseToRegister = sms.findCourseById(courseIdToRegister);
                        if (studentToRegister instanceof CreditBasedStudent && courseToRegister != null) {
                            ((CreditBasedStudent) studentToRegister).registerCourse(courseToRegister);
                        } else {
                            System.out.println("Không tìm thấy sinh viên hoặc môn học.");
                        }
                        break;
                    }
                case 6:
                    System.out.print("Nhập ID sinh viên: ");
                    String studentIdToGrade = scanner.nextLine();
                    System.out.print("Nhập ID môn học: ");
                    String courseIdToGrade = scanner.nextLine();
                    System.out.print("Nhập điểm giữa kỳ: ");
                    double midtermScore = scanner.nextDouble();
                    System.out.print("Nhập điểm cuối kỳ: ");
                    double finalScore = scanner.nextDouble();
                    scanner.nextLine(); // Đọc dòng new line
                    sms.inputGrade(studentIdToGrade, courseIdToGrade, midtermScore, finalScore);
                    break;
                case 7:
                    System.out.print("Nhập ID sinh viên để xem bảng điểm: ");
                    String studentIdToViewTranscript = scanner.nextLine();
                    Student studentToView = sms.findStudentById(studentIdToViewTranscript);
                    if (studentToView != null) {
                        studentToView.viewTranscript();
                    } else {
                        System.out.println("Không tìm thấy sinh viên với ID: " + studentIdToViewTranscript);
                    }
                    break;
                case 8:
                    System.out.print("Nhập ID sinh viên để kiểm tra tốt nghiệp: ");
                    String studentIdToCheckGraduation = scanner.nextLine();
                    sms.checkGraduationForStudent(studentIdToCheckGraduation);
                    break;
                case 9:
                    sms.displayAllStudents();
                    break;
                case 10:
                    sms.displayAllCourses();
                    break;

                case 11:
                    System.out.print("Nhập ID sinh viên để xem danh sách môn học đã đăng ký: ");
                    String studentIdToViewCourses = scanner.nextLine();
                    sms.displayStudenst(studentIdToViewCourses);
                    break;
                case 12:
                    System.out.print("Nhập ID môn học để xem danh sách sinh viên đã đăng ký: ");
                    String courseIdToDisplayEnrollment = scanner.nextLine();
                    sms.displayCourseEnrollment(courseIdToDisplayEnrollment);
                    break;
                case 0:
                    System.out.println("Thoát khỏi chương trình.");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        } while (choice != 0);

        scanner.close();
    }
}