class  extends Student {
    private List<Course> fixedCourseList;

    public PartTimeStudent(String studentID, String name, String dob, String email, List<Course> fixedCourseList) {
        super(studentID, name, dob, email);
        this.fixedCourseList = fixedCourseList;
    }

    @Override
    public void registerCourse(Course ignored) {
        for (Course course : fixedCourseList) {
            if (!course.isCourseFull()) {
                courses.add(course);
                course.addStudent(this);
            }
        }
    }

    @Override
    public void checkGraduation() {
        boolean allCompleted = true;
        for (Course course : fixedCourseList) {
            if (!courses.contains(course) || course.calculateFinalGrade() == null || course.calculateFinalGrade() < 5.0) {
                allCompleted = false;
                break;
            }
        }
        calculateGPA();
        this.graduationStatus = allCompleted && this.gpa >= 2.0;
    }
}
