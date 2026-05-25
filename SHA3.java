import java.util.*;
import java.io.*;

public class SHA3 {
    public static String keccakf (String state, int w) {
        // convert the state string into a 5 x 5 x w array of bits
        // stateArray[i][j][k] is bit number (5i + j)w + k of the state
        int [][][] stateArray = new int[5][5][w];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < w; k++) {
                    int index = (5 * i + j) * w + k;
                    stateArray[i][j][k] = Integer.parseInt(state.charAt(index) + "");
                }
            }
        }
        // theta
        // rho
        // pi
        // chi
        // iota
        return state;
    }
    public static String pad (String message, int r) {
        // pad the message with 100...001 until the length is a multiple of r
        if (message.length() == 0) {
            String padding = "1";
            for (int i = 1; i < r - 2; i++) {
                padding = padding + "0";
            }
            padding = padding + "1";
            return padding;
        }
        else if (message.length() % r == 0) {
            return message;
        }
        else if (message.length() % r == r - 1) {
            return message + "1";
        }
        else {
            int paddingLength = r - (message.length() % r);
            String padding = "1";
            for (int i = 1; i < paddingLength - 1; i++) {
                padding = padding + "0";
            }
            padding = padding + "1";
            return message + padding;
        }
    }
    public static String absorb (String message, int b, int c, int r) {
        // split the message into n r-bit blocks
        int n = message.length() / r;
        String[] blocks = new String[n];
        for (int i = 0; i < n; i++) {
            blocks[i] = message.substring(i * r, (i + 1) * r);
        }
        // initialize the state as a b-bit array of zeros
        String state = "";
        for (int i = 0; i < b; i++) {
            state = state + "0";
        }
        // for each block, absorb it into the state by:
        // (1) extending it at the end by a string of c zero bits
        // (2) XORing it with the state
        // (3) applying the permutation function keccakf to the state to yield a new state
        for (int i = 0; i < n; i++) {
            String block = blocks[i];
            for (int i = 0; i < c; i++) {
                block = block + "0";
            }
            int blockInt = Integer.parseInt(block, 2);
            int stateInt = Integer.parseInt(state, 2);
            int newStateInt = blockInt ^ stateInt;
            state = Integer.toBinaryString(newStateInt);
        }
        state = keccakf(state); // TODO: implement keccakf
        return state;
    }
    public static String squeeze (String state, int d, int r) {
        // initialize an empty string Z (will be the output hash)
        // while Z is fewer than d bits long:
        // (1) append the first r bits of the state to Z
        // (2) apply the permutation function keccakf to the state to yield a new state
        String Z = "";
        while (true) {
            Z = Z + state.substring(0, r);
            if (Z.length() < d) {
                state = keccakf(state);
            }
            else {
                break;
            }
        }
        // truncate Z to d bits and return it as the output hash
        Z = Z.substring(0, d);
        return Z;
    }
    public static void main (String[] args) {
        String filename = args[0]; // file should contain a bit string on a single line
        Scanner scan = new Scanner(new FileReader(filename));
        String message = scan.nextLine(); // a bit string to be hashed

        int b = 1600; // state size (in standard SHA-3, the state is a 1600-bit array composed of a 5x5 grid of 64-bit words)
        int w = b / 25; // word size (the number of bits in each of the 25 words in the state; in standard SHA-3, w = 64)
        int d = 256; // digest length (the fixed length of the output hash in bits)
        int c = d * 2; // capacity (the number of bits of the state that are not directly affected by the input, this determines the security level)
        int r = b - c; // rate (the number of bits of the state that are absorbed from the input per round)

        message = pad(message, r);
        String state = absorb(message, b, c, r);
        String hash = squeeze(state, d, r);
        scan.close();
    }
}