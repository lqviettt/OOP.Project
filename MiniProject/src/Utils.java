import java.text.SimpleDateFormat;
import java.util.*;

class Utils {
    public static String formatName(String name) {
        return name.trim().toUpperCase();
    }

    public static Date formatDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static double convertTo4PointScale(double score10) {
        if (score10 >= 9.5) return 4.0;      // A+
        else if (score10 >= 8.5) return 4.0; // A
        else if (score10 >= 8.0) return 3.5; // B+
        else if (score10 >= 7.0) return 3.0; // B
        else if (score10 >= 6.5) return 2.5; // C+
        else if (score10 >= 5.5) return 2.0; // C
        else if (score10 >= 5.0) return 1.5; // D+
        else if (score10 >= 4.0) return 1.0; // D
        else return 0.0;                     // F
    }

    public static String formatGrade(Double grade) {
        if (grade == null || grade == 0.0) return "N/A";
        return String.format("%.2f", grade);
    }

}
