//package com.yourpackage.main; // Đặt package phù hợp
//
//import com.yourpackage.model.CreditBasedStudent;
//import com.yourpackage.model.Course;
//import com.yourpackage.model.PartTimeStudent;
//import com.yourpackage.service.StudentManagementSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainConsole {
    public static void main(String[] args) {
        StudentManagementSystem sms = new StudentManagementSystem();
        Scanner scanner = new Scanner(System.in);

        // Tạo một số sinh viên và môn học mẫu
        Course math101 = new Course("MATH101", "Giải tích 1", 3, 30, 70, 50);
        Course prog101 = new Course("PROG101", "Lập trình cơ bản", 4, 40, 60, 40, math101); // PROG101 có MATH101 là tiên quyết
        Course web201 = new Course("WEB201", "Lập trình Web", 3, 30, 70, 30, prog101);

        sms.addCourse(math101);
        sms.addCourse(prog101);
        sms.addCourse(web201);

        CreditBasedStudent student1 = new CreditBasedStudent("S1001", "Nguyen Van A", LocalDate.of(2003, 5, 15), "a.nguyen@example.com", 3);
        PartTimeStudent student2 = new PartTimeStudent("S2002", "Tran Thi B", LocalDate.of(1998, 10, 20), "b.tran@example.com", Arrays.asList(math101, prog101));

        sms.addStudent(student1);
        sms.addStudent(student2);

        // Menu điều khiển (ví dụ)
        int choice;
        do {
            System.out.println("\n--- Hệ Thống Quản Lý Sinh Viên (Console) ---");
            System.out.println("1. Thêm sinh viên");
            System.out.println("2. Xóa sinh viên");
            System.out.println("3. Thêm môn học");
            System.out.println("4. Xóa môn học");
            System.out.println("5. Đăng ký môn học (SV tín chỉ)");
            System.out.println("6. Nhập điểm");
            System.out.println("7. Xem bảng điểm");
            System.out.println("8. Kiểm tra tốt nghiệp");
            System.out.println("9. Hiển thị danh sách sinh viên");
            System.out.println("10. Hiển thị danh sách môn học");
            System.out.println("11. Hiển thị danh sách môn học đã đăng ký của sinh viên");
            System.out.println("12. Hiển thị danh sách sinh viên đã đăng ký môn học");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng new line

            switch (choice) {
                case 1:
                    System.out.println("Thêm sinh viên mới:");
                    System.out.print("Nhập ID sinh viên: ");
                    String id = scanner.nextLine();
                    System.out.print("Nhập tên sinh viên: ");
                    String name = scanner.nextLine();
                    System.out.print("Nhập ngày sinh (yyyy-mm-dd): ");
                    LocalDate dob = LocalDate.parse(scanner.nextLine());
                    System.out.print("Nhập email sinh viên: ");
                    String email = scanner.nextLine();
                    System.out.print("Sinh viên tín chỉ? (true/false): ");
                    boolean isCreditBased = scanner.nextBoolean();
                    scanner.nextLine(); // Đọc dòng new line

                    if (isCreditBased) {
                        System.out.print("Nhập tín chỉ yêu cầu để tốt nghiệp: ");
                        int requiredCredits = scanner.nextInt();
                        scanner.nextLine(); // Đọc dòng new line
                        CreditBasedStudent student = new CreditBasedStudent(id, name, dob, email, requiredCredits);
                        sms.addStudent(student);
                    } else {
                        PartTimeStudent student = new PartTimeStudent(id, name, dob, email, new ArrayList<>());
                        sms.addStudent(student);
                    }
                    System.out.println("Đã thêm sinh viên thành công.");
                    break;
                case 2:
                    System.out.print("Nhập ID sinh viên cần xóa: ");
                    String studentIdToDelete = scanner.nextLine();
                    sms.removeStudent(studentIdToDelete);
                    break;
                case 3:
                    System.out.println("Thêm môn học mới:");
                    System.out.print("Nhập ID môn học: ");
                    String courseId = scanner.nextLine();
                    System.out.print("Nhập tên môn học: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Nhập số tín chỉ: ");
                    int credits = scanner.nextInt();
                    System.out.print("Nhập số lượng sinh viên tối đa: ");
                    int capacity = scanner.nextInt();
                    System.out.print("Nhập trọng số của điểm giữa kỳ (%): ");
                    int midtermWeight = scanner.nextInt();
                    System.out.print("Nhập trọng số của điểm cuối kỳ (%): ");
                    int finalWeight = scanner.nextInt();
                    scanner.nextLine(); // Đọc dòng new line

                    System.out.print("ID môn học tiên quyết (để trống nếu không có): ");
                    String prerequisiteId = scanner.nextLine();

                    Course prerequisiteCourse = prerequisiteId.isEmpty() ? null : sms.findCourseById(prerequisiteId);

                    Course newCourse = new Course(courseId, courseName, credits, capacity, midtermWeight, finalWeight, prerequisiteCourse);
                    sms.addCourse(newCourse);
                    System.out.println("Đã thêm môn học mới thành công.");

                    break;
                case 4:
                    System.out.print("Nhập ID môn học cần xóa: ");
                    String courseIdToDelete = scanner.nextLine();
                    sms.removeCourse(courseIdToDelete);
                    break;
                case 5:
                    System.out.print("Nhập ID sinh viên: ");
                    String studentIdToRegister = scanner.nextLine();
                    Student studentToRegister = sms.findStudentById(studentIdToRegister);
                    if (studentToRegister instanceof PartTimeStudent) {
                        System.out.println("Sinh viên thuộc hệ vừa học vừa làm không thể đăng ký.");
                        break;
                    } else {
                        System.out.print("Nhập ID môn học muốn đăng ký: ");
                        String courseIdToRegister = scanner.nextLine();
                        Course courseToRegister = sms.findCourseById(courseIdToRegister);
                        if (studentToRegister instanceof CreditBasedStudent && courseToRegister != null) {
                            ((CreditBasedStudent) studentToRegister).registerCourse(courseToRegister);
                        } else {
                            System.out.println("Không tìm thấy sinh viên hoặc môn học.");
                        }
                        break;
                    }
                case 6:
                    System.out.print("Nhập ID sinh viên: ");
                    String studentIdToGrade = scanner.nextLine();
                    System.out.print("Nhập ID môn học: ");
                    String courseIdToGrade = scanner.nextLine();
                    System.out.print("Nhập điểm giữa kỳ: ");
                    double midtermScore = scanner.nextDouble();
                    System.out.print("Nhập điểm cuối kỳ: ");
                    double finalScore = scanner.nextDouble();
                    scanner.nextLine(); // Đọc dòng new line
                    sms.inputGrade(studentIdToGrade, courseIdToGrade, midtermScore, finalScore);
                    break;
                case 7:
                    System.out.print("Nhập ID sinh viên để xem bảng điểm: ");
                    String studentIdToViewTranscript = scanner.nextLine();
                    Student studentToView = sms.findStudentById(studentIdToViewTranscript);
                    if (studentToView != null) {
                        studentToView.viewTranscript();
                    } else {
                        System.out.println("Không tìm thấy sinh viên với ID: " + studentIdToViewTranscript);
                    }
                    break;
                case 8:
                    System.out.print("Nhập ID sinh viên để kiểm tra tốt nghiệp: ");
                    String studentIdToCheckGraduation = scanner.nextLine();
                    sms.checkGraduationForStudent(studentIdToCheckGraduation);
                    break;
                case 9:
                    sms.displayAllStudents();
                    break;
                case 10:
                    sms.displayAllCourses();
                    break;

                case 11:
                    System.out.print("Nhập ID sinh viên để xem danh sách môn học đã đăng ký: ");
                    String studentIdToViewCourses = scanner.nextLine();
                    sms.displayStudenst(studentIdToViewCourses);
                    break;
                case 12:
                    System.out.print("Nhập ID môn học để xem danh sách sinh viên đã đăng ký: ");
                    String courseIdToDisplayEnrollment = scanner.nextLine();
                    sms.displayCourseEnrollment(courseIdToDisplayEnrollment);
                    break;
                case 0:
                    System.out.println("Thoát khỏi chương trình.");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        } while (choice != 0);

        scanner.close();
    }
}