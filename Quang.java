import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student {
    private String studentID;
    private String name;
    private int age;
    private List<Course> courses; // Assuming Course objects are stored here
    private List<Grade> grades;   // Assuming Grade objects are stored here

    public Student(String studentID, String name, int age) {
        this.studentID = studentID;
        this.name = name;
        this.age = age;
        this.courses = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    public String getStudentID() {
        return studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    public void removeCourse(Course course) {
        if (course != null) {
            courses.remove(course);
        }
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void addGrade(Grade grade) {
        if (grade != null) {
            grades.add(grade);
        }
    }

    // You might want to implement equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentID, student.studentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID);
    }

    @Override
    public String toString() {
        return "Student ID: " + studentID + ", Name: " + name + ", Age: " + age;
    }
}