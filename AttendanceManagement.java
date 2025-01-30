package syjava1;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Student {
    private int studentId;
    private String name;
    private int attendanceCount;
    private List<String> attendanceRecords;

    public Student(int studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.attendanceCount = 0;
        this.attendanceRecords = new ArrayList<>();
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public void markAttendance() {
        attendanceCount++;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        attendanceRecords.add(timestamp);
    }

    public String getAttendanceRecords() {
        return String.join(" | ", attendanceRecords);
    }

    public String toString() {
        return studentId + "," + name + "," + attendanceCount + "," + getAttendanceRecords();
    }
}

class AttendanceSystem {
    private Map<Integer, Student> studentMap;
    private final String FILE_NAME = "attendance_data.txt";

    public AttendanceSystem() {
        studentMap = new HashMap<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    int id = Integer.parseInt(data[0]);
                    Student student = new Student(id, data[1]);
                    studentMap.put(id, student);
                    
                    student.markAttendance();  // Restore previous attendance count
                }
            }
        } catch (IOException e) {
            System.out.println("No previous records found. Starting fresh.");
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student student : studentMap.values()) {
                bw.write(student.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance records.");
        }
    }

    public void addStudent(int studentId, String name) {
        if (studentMap.containsKey(studentId)) {
            System.out.println("❌ Student ID already exists!");
        } else {
            Student student = new Student(studentId, name);
            studentMap.put(studentId, student);
            saveToFile();
            System.out.println("✅ Student added successfully!");
        }
    }

    public void markAttendance(int studentId) {
        Student student = studentMap.get(studentId);
        if (student != null) {
            student.markAttendance();
            saveToFile();
            System.out.println("📅 Attendance marked for " + student.getName() + " at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } else {
            System.out.println("❌ Student not found!");
        }
    }

    public void displayAttendance() {
        if (studentMap.isEmpty()) {
            System.out.println("⚠ No students found.");
            return;
        }
        System.out.println("\n📌 Attendance Report:");
        System.out.println("ID\tName\t\tAttendance\tLast Marked");
        System.out.println("------------------------------------------------------");
        for (Student student : studentMap.values()) {
            System.out.printf("%d\t%s\t\t%d\t\t%s%n", student.getStudentId(), student.getName(), student.getAttendanceCount(), student.getAttendanceRecords());
        }
    }
}

public class AttendanceManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AttendanceSystem system = new AttendanceSystem();

        System.out.print("🔐 Enter Admin Password: ");
        String password = scanner.nextLine();
        if (!password.equals("admin123")) {
            System.out.println("❌ Access Denied!");
            return;
        }
        
        while (true) {
            System.out.println("\n🚀 Attendance Management System 🚀");
            System.out.println("1️⃣ Add Student");
            System.out.println("2️⃣ Mark Attendance");
            System.out.println("3️⃣ View Attendance Report");
            System.out.println("4️⃣ Exit");
            System.out.print("🔹 Choose an option: ");

            int choice;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                scanner.next(); // Clear buffer
                continue;
            }

            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("🆔 Enter Student ID (integer): ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("👤 Enter Student Name: ");
                    String name = scanner.nextLine();
                    system.addStudent(id, name);
                    break;
                case 2:
                    System.out.print("📌 Enter Student ID to mark attendance: ");
                    int studentId = scanner.nextInt();
                    system.markAttendance(studentId);
                    break;
                case 3:
                    system.displayAttendance();
                    break;
                case 4:
                    System.out.println("🔴 Exiting... Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }
}
