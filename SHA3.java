import java.util.*;
import java.io.*;

public class SHA3 {
    public static void main (String[] args) {
        String filename = args[0]; // file should contain a bit string on a single line
        Scanner scan = new Scanner(new FileReader(filename));
        String message = scan.nextLine(); // a bit string to be hashed
    }
}