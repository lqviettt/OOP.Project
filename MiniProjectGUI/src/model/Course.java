package model;

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
        this.courseID = Objects.requireNonNull(courseID, "model.Course ID cannot be null");
        this.courseName = Objects.requireNonNull(courseName, "model.Course name cannot be null");
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
        this.courseID = Objects.requireNonNull(courseID, "model.Course ID cannot be null");
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = Objects.requireNonNull(courseName, "model.Course name cannot be null");
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