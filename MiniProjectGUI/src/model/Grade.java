package model;

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
        return "StudentID: " + student.getStudentID() + ", model.Course: " + course.getCourseName() +
                ", Midterm: " + String.format("%.2f", midtermScore) + ", Final: " + String.format("%.2f", finalScore) +
                ", Final model.Grade: " + String.format("%.2f", finalGrade);
    }
}