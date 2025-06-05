import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CreditBasedStudent extends Student {
    private List<Course> completedCourses;
    private int requiredCredits;

    public CreditBasedStudent(String studentID, String name, Date dob, String email, int requiredCredits) {
        super(studentID, name, dob, email);
        this.completedCourses = new ArrayList<>();
        this.requiredCredits = requiredCredits;
    }

    public List<Course> getCompletedCourses() {
        return completedCourses;
    }

    public int getRequiredCredits() {
        return requiredCredits;
    }

    public void markCourseAsCompleted(Course course) {
        if (course != null && super.courses.contains(course) && !completedCourses.contains(course)) {
            super.courses.remove(course);
            completedCourses.add(course);
            System.out.println("Sinh viên " + getName() + " đã hoàn thành môn: " + course.getCourseName());
        } else if (completedCourses.contains(course)) {
            System.out.println(getName() + " đã hoàn thành môn " + course.getCourseName() + " trước đó.");
        } else {
            System.out.println("Không thể đánh dấu hoàn thành cho môn " + (course != null ? course.getCourseName() : "null") + ". Môn học chưa được đăng ký hoặc không hợp lệ.");
        }
    }

    @Override
    public boolean registerCourse(Course courseToRegister) {
        if (courseToRegister == null) {
            System.out.println("Lỗi: Môn học không hợp lệ để đăng ký.");
            return false;
        }

        if (completedCourses.contains(courseToRegister)) {
            System.out.println(getName() + " đã hoàn thành môn học này rồi: " + courseToRegister.getCourseName());
            return false;
        }
        if (super.courses.contains(courseToRegister)) {
            System.out.println(getName() + " đã đăng ký môn học này rồi: " + courseToRegister.getCourseName());
            return false;
        }

        Optional<Course> prerequisiteOpt = courseToRegister.getPrerequisite();
        if (prerequisiteOpt.isPresent()) {
            Course prerequisiteCourse = prerequisiteOpt.get();
            if (!completedCourses.contains(prerequisiteCourse)) {
                System.out.println(getName() + " không thể đăng ký môn " + courseToRegister.getCourseName() +
                                   ". Cần hoàn thành môn tiên quyết: " + prerequisiteCourse.getCourseName());
                return false;
            }
        }

        if (courseToRegister.isCourseFull()) {
            System.out.println("Môn học " + courseToRegister.getCourseName() + " đã đủ sĩ số. " + getName() + " không thể đăng ký.");
            return false;
        }

        if (courseToRegister.addStudent(this)) {
            super.courses.add(courseToRegister);
            System.out.println(getName() + " đã đăng ký thành công môn học: " + courseToRegister.getCourseName());
            return true;
        } else {
            System.out.println("Đăng ký môn học " + courseToRegister.getCourseName() + " thất bại do lỗi từ phía môn học (ví dụ: đầy ngay lúc kiểm tra lại).");
            return false;
        }
    }

    @Override
    public boolean checkGraduation() {
        System.out.println("Đang kiểm tra điều kiện tốt nghiệp cho sinh viên tín chỉ: " + getName());

        int totalCompletedCredits = 0;
        for (Course course : completedCourses) {
            totalCompletedCredits += course.getCredits();
        }
        System.out.println(" - Số tín chỉ đã hoàn thành: " + totalCompletedCredits + "/" + requiredCredits);
        System.out.println(" - GPA hiện tại: " + getGpa());

        boolean hasEnoughCredits = totalCompletedCredits >= requiredCredits;
        boolean hasSufficientGpa = getGpa() >= 2.0;

        if (hasEnoughCredits && hasSufficientGpa) {
            super.setGraduationStatus(true);
            System.out.println("==> " + getName() + " đủ điều kiện tốt nghiệp!");
            return true;
        } else {
            super.setGraduationStatus(false);
            System.out.println("==> " + getName() + " chưa đủ điều kiện tốt nghiệp.");
            if (!hasEnoughCredits) {
                System.out.println("- Lý do: Cần hoàn thành thêm " + (requiredCredits - totalCompletedCredits) + " tín chỉ.");
            }
            if (!hasSufficientGpa) {
                System.out.println("- Lý do: GPA cần đạt tối thiểu 2.0 (hiện tại: " + getGpa() + ").");
            }
            return false;
        }
    }
}