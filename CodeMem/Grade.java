import java.util.Objects;

public class Grade {
    private final Course course;
    private final Student student;
    private float midtermScore;
    private float finalScore;
    private float finalGrade;
    private boolean isFinalGradeCalculated;

    public Grade(Course course, Student student) {
        this.course = Objects.requireNonNull(course, "Course cannot be null for a Grade.");
        this.student = Objects.requireNonNull(student, "Student cannot be null for a Grade.");
        this.midtermScore = 0.0f;
        this.finalScore = 0.0f;
        this.finalGrade = 0.0f;
        this.isFinalGradeCalculated = false;
    }

    public Course getCourse() {
        return course;
    }

    public Student getStudent() {
        return student;
    }

    public float getMidtermScore() {
        return midtermScore;
    }

    public float getFinalScore() {
        return finalScore;
    }

    public float getFinalGrade() {
        return finalGrade;
    }

    public boolean isFinalGradeCalculated() {
        return isFinalGradeCalculated;
    }

    public void setMidtermScore(float score) {
        if (score < 0.0f || score > 10.0f) {
            throw new IllegalArgumentException("Midterm score must be between 0.0 and 10.0. Received: " + score);
        }
        this.midtermScore = score;
        this.isFinalGradeCalculated = false;
    }

    public void setFinalScore(float score) {
        if (score < 0.0f || score > 10.0f) {
            throw new IllegalArgumentException("Final score must be between 0.0 and 10.0. Received: " + score);
        }
        this.finalScore = score;
        this.isFinalGradeCalculated = false;
    }

    public void calculateAndUpdateFinalGrade() {
        if (this.course == null) {
            throw new IllegalStateException("Cannot calculate final grade. Course information is missing for student " +
                                           (student != null ? student.getName() : "Unknown") + ".");
        }

        try {
            this.finalGrade = this.course.calculateFinalGrade(this.midtermScore, this.finalScore);
            this.isFinalGradeCalculated = true;
        } catch (Exception e) {
            System.err.println("Error calculating final grade for student " +
                               (student != null ? student.getName() : "Unknown") + " in course " +
                               this.course.getCourseName() + ": " + e.getMessage());
            this.finalGrade = 0.0f;
            this.isFinalGradeCalculated = false;
        }
    }

    @Override
    public String toString() {
        return "Grade[Student_ID: " + (student != null ? student.getStudentID() : "N/A") +
               ", Course_ID: " + (course != null ? course.getCourseID() : "N/A") +
               ", Midterm: " + String.format("%.2f", midtermScore) +
               ", Final: " + String.format("%.2f", finalScore) +
               ", Final_Grade: " + (isFinalGradeCalculated ? String.format("%.2f", finalGrade) : "Not Calculated") +
               "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(student.getStudentID(), grade.student.getStudentID()) &&
               Objects.equals(course.getCourseID(), grade.course.getCourseID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(student.getStudentID(), course.getCourseID());
    }
}