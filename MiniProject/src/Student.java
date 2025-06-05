
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