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