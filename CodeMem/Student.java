import java.util.*;

public abstract class Student {
	protected String studentID;
	protected String name;
	protected String dob;
	protected String email;
	protected double gpa;
	protected boolean graduationStatus;
	protected List<Course> courses;
	
	public Student(String studentID, String name, String dob, String email) {
		this.studentID = studentID;
		this.name = name;
		this.dob = dob;
		this.email = email;
		this.gpa = 0.0;
		this.graduationStatus = false;
		this.courses = new ArrayList<>();
	}
	
	public abstract void registerCourse(Course course);
	
	public abstract void checkGraduation();
	
	public void viewRegisteredCourses() {
		System.out.println("Registered courses: ");
		for(Course course: courses) {
			System.out.println(course.getCourseID()+ " - " + course.getCourseName());
		} // thêm getter cho courseID và courseName, credits
	}
	
	public double calculateGPA() {
		double totalPoints = 0.0;
		int totalCredits = 0;
		for (Course course : courses) {
			Double score = course.calculateFinalGrade(); //Sửa phương thức course.calculateFinalGrade()
			if(score != null) {
				totalPoints += score * course.getCredits(); 
				totalCredits += course.getCredits();
			}
		}
		if (totalCredits > 0) {
			this.gpa = totalPoints / totalCredits;
		} else {
			this.gpa = 0.0;
		}
		return this.gpa;
	}
	
	public void viewTranscript() {
		System.out.println("Transcript:");
		for (Course course : courses) {
			Double score = course.calculateFinalGrade();
			if(score != null) {
				System.out.println(course.getCourseID() + " - " + course.getCourseName() + " - Score: " + score);
			} else {
				System.out.println(course.getCourseID() + " - " + course.getCourseName() + " - Score: N/A");
			}
		}
	}
	
	public String getStudentID() { return studentID; }
	public String getName() { return name; }
	public double getGpa() { return gpa; }
	public boolean isGraduated() {return graduationStatus; }
}
