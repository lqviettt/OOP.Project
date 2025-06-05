import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentManagementSystem {

    private final List<Student> students;
    private final List<Course> courses;

    public StudentManagementSystem() {
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students); 
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses); 
    }

    
    public boolean addStudent(Student student) {
        if (student == null) {
            return false;
        }

       
        Optional<Student> existingStudent = findStudentByID(student.getStudentID());
        if (existingStudent.isPresent()) {
            System.out.println("Sinh vien da ton tai voi ID: " + student.getStudentID());
            return false;
        }

      
        students.add(student);
        System.out.println("Da them sinh vien " + student.getName() + " vao he thong!");
        return true;
    }

   
    public boolean updateStudentInfo(Student updatedStudent) {
        if (updatedStudent == null) {
            return false;
        }

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentID().equals(updatedStudent.getStudentID())) {
                students.set(i, updatedStudent);
                System.out.println("Da cap nhat thong tin cho sinh vien " + updatedStudent.getName() + "!");
                return true;
            }
        }
        System.out.println("Khong tim thay sinh vien can cap nhat!");
        return false;
    }

   
    public boolean removeStudent(Student student) {
        if (student == null) {
            return false;
        }

        boolean removed = students.removeIf(s -> s.getStudentID().equals(student.getStudentID()));

        if (removed) {
            
            for (Course course : courses) {
                course.removeStudent(student);
            }
            System.out.println("Da xoa sinh vien " + student.getName() + " khoi he thong!");
            return true;
        } else {
            System.out.println("Khong tim thay sinh vien can xoa!");
            return false;
        }
    }

   
    public Optional<Student> findStudentByID(String studentID) {
        return students.stream()
                .filter(student -> student.getStudentID().equals(studentID))
                .findFirst();
    }

   
    public List<Student> findStudentByName(String name) {
        String searchNameLower = name.toLowerCase();
        return students.stream()
                .filter(student -> student.getName().toLowerCase().contains(searchNameLower))
                .collect(Collectors.toList());
    }

   
    public boolean addCourse(Course course) {
        if (course == null) {
            return false;
        }

       
        Optional<Course> existingCourse = findCourseByID(course.getCourseID());
        if (existingCourse.isPresent()) {
            System.out.println("Mon hoc da ton tai voi ID: " + course.getCourseID()); 
            return false;
        }

        
        courses.add(course);
        System.out.println("Da them mon hoc " + course.getCourseName() + " vao he thong!");
        return true;
    }

  
    public boolean removeCourse(String courseID) {
        Optional<Course> courseToRemove = findCourseByID(courseID);

        if (courseToRemove.isEmpty()) {
            System.out.println("Khong tim thay mon hoc can xoa!");
            return false;
        }

      
        for (Student student : students) {
            student.removeCourse(courseToRemove.get());
        }

       
        courses.remove(courseToRemove.get());
        System.out.println("Da xoa mon hoc " + courseID + " khoi he thong!");
        return true;
    }

   
    public Optional<Course> findCourseByID(String courseID) {
        return courses.stream()
                .filter(course -> course.getCourseID().equals(courseID))
                .findFirst();
    }

    public boolean inputGrade(Student student, Course course, Grade grade) {
        if (student == null || course == null || grade == null) {
            return false;
        }

      
        boolean isStudentRegistered = student.getCourses().stream()
                .anyMatch(c -> c.getCourseID().equals(course.getCourseID()));

        if (!isStudentRegistered) {
            System.out.println("Sinh vien chua dang ky mon hoc nay!");
            return false;
        }

        
        student.addGrade(grade);
        System.out.println("Da nhap diem cho sinh vien " + student.getName() + " mon hoc " + course.getCourseName() + "!");
        return true;
    }
}
