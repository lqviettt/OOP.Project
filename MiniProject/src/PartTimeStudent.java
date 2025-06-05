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