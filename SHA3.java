import java.util.*;
import java.io.*;

public class SHA3 {
    public static String toHex(String binary) {
        String hex = "";
        while (binary.length() % 4 != 0) {
            binary = "0" + binary;
        }
        for (int i = 0; i < binary.length(); i += 4) {
            String fourBits = binary.substring(i, i + 4);
            int decimalValue = Integer.parseInt(fourBits, 2);
            hex = hex + Integer.toHexString(decimalValue);
        }
        return hex;
    }
    public static String toHexReversed(String binary) {
        String hex = "";
        while (binary.length() % 4 != 0) {
            binary += "0";
        }
        for (int i = 0; i < binary.length(); i += 4) {
            String fourBits = binary.substring(i, i + 4);
            fourBits = new StringBuilder(fourBits).reverse().toString();
            int decimalValue = Integer.parseInt(fourBits, 2);
            hex = hex + Integer.toHexString(decimalValue);
        }
        return hex;
    }
    public static String toString(Integer [][][] arr) {
        String str = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < arr[0][0].length; k++) {
                    str = str + arr[i][j][k];
                }
            }
        }
        return str;
    }
    // public static Integer [][][] theta (Integer [][][] stateArray) {
    //     int w = stateArray[0][0].length; // word size
    //     Integer[][][] newState = new Integer[5][5][w];
    //     for (int i = 0; i < 5; i++) {
    //         for (int j = 0; j < 5; j++) {
    //             for (int k = 0; k < w; k++) {
    //                 int parity1 = 0;
    //                 int parity2 = 0;
    //                 for (int m = 0; m < 5; m++) {
    //                     parity1 = parity1 ^ stateArray[m][Math.floorMod(j - 1, 5)][k];
    //                     parity2 = parity2 ^ stateArray[m][Math.floorMod(j + 1, 5)][Math.floorMod(k - 1, w)];
    //                 }
    //                 newState[i][j][k] = stateArray[i][j][k] ^ parity1 ^ parity2;
    //             }
    //         }
    //     }
    //     return newState;
    // }
    public static Integer [][][] theta (Integer [][][] stateArray) {
        int w = stateArray[0][0].length; // word size
        Integer[][][] newState = new Integer[5][5][w];
        Integer[][] C = new Integer[5][w];
        for (int i = 0; i < 5; i++) {
            for (int k = 0; k < w; k++) {
                C[i][k] = stateArray[i][0][k] ^ stateArray[i][1][k] ^ stateArray[i][2][k] ^ stateArray[i][3][k] ^ stateArray[i][4][k];
            }
        }
        Integer[][] D = new Integer[5][w];
        for (int i = 0; i < 5; i++) {
            for (int k = 0; k < w; k++) {
                D[i][k] = C[Math.floorMod(i - 1, 5)][k] ^ C[Math.floorMod(i + 1, 5)][Math.floorMod(k - 1, w)];
            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < w; k++) {
                    newState[i][j][k] = stateArray[i][j][k] ^ D[i][k];
                }
            }
        }
        return newState;
    }
    public static Integer [][][] rho (Integer [][][] stateArray) {
        int w = stateArray[0][0].length; // word size
        Integer[][][] newState = new Integer[5][5][w]; 
        // t = 0 is handled separately
        int i = 0;
        int j = 1;
        for (int k = 0; k < w; k++) {
            newState[0][0][k] = stateArray[0][0][k];
            newState[i][j][k] = stateArray[i][j][Math.floorMod(k - (1 * 2) / 2, w)];
        }
        
        for (int t = 1; t < 24; t++) {
            int newI = Math.floorMod((3 * i) + (2 * j), 5);
            int newJ = i;
            i = newI;
            j = newJ;
    
            for (int k = 0; k < w; k++) {
                newState[i][j][k] = stateArray[i][j][Math.floorMod(k - ((t + 1) * (t + 2) / 2), w)];
            }
        }
    
        return newState;
    }
    public static Integer [][][] pi (Integer [][][] stateArray) {
        Integer [][][] newState = new Integer[5][5][stateArray[0][0].length];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < stateArray[0][0].length; k++) {
                    newState[(3 * i + 2 * j) % 5][i][k] = stateArray[i][j][k];
                }  
            }
        }
        return newState;
    }
    public static Integer [][][] chi (Integer [][][] stateArray) {
        int w = stateArray[0][0].length; // word size
        Integer[][][] newState = new Integer[5][5][w];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < w; k++) {
                    newState[i][j][k] = stateArray[i][j][k] ^ (~stateArray[i][Math.floorMod(j + 1, 5)][k] & stateArray[i][Math.floorMod(j + 2, 5)][k]);
                }
            }
        }
        return newState;
    }
    
    public static Integer [][][] iota (Integer [][][] stateArray, int i) {
        // round constants for Keccak-f[1600]
        long[] RC = {
            0x0000000000000001L, 0x0000000000008082L,
            0x800000000000808AL, 0x8000000080008000L,
            0x000000000000808BL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L,
            0x000000000000008AL, 0x0000000000000088L,
            0x0000000080008009L, 0x000000008000000AL,
            0x000000008000808BL, 0x800000000000008BL,
            0x8000000000008089L, 0x8000000000008003L,
            0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800AL, 0x800000008000000AL,
            0x8000000080008081L, 0x8000000000008080L,
            0x0000000080000001L, 0x8000000080008008L
        }; 

        int w = stateArray[0][0].length; 
        long rc = RC[i]; 

        for (int k = 0; k < w; k++) {
            int bit = (int)((rc >> k) & 1L);
            stateArray[0][0][k] ^= bit;
        }

        return stateArray;

    }
    public static String keccakf (String state, int w) {
        // convert the state string into a 5 x 5 x w array of bits
        // stateArray[i][j][k] is bit number (5i + j)w + k of the state
        int l = (int) (Math.log(w) / Math.log(2)); // number of bits needed to represent w
        Integer [][][] stateArray = new Integer[5][5][w];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < w; k++) {
                    int index = (5 * i + j) * w + k;
                    stateArray[i][j][k] = Integer.parseInt(state.charAt(state.length() - 1 - index) + "");
                }
            }
        }
        for (int i = 0; i < 12 + 2 * l; i++) {
            stateArray = theta(stateArray);
            stateArray = rho(stateArray);
            stateArray = pi(stateArray);
            stateArray = chi(stateArray);
            stateArray = iota(stateArray, i);
        }
        return toString(stateArray);
    }
    public static String pad (String message, int r) {
        // pad the message with the SHA3 suffix and 10*1 padding until the length is a multiple of r
        int paddingLength = r - (message.length() % r);
        if (paddingLength < 4) {
            paddingLength = paddingLength + r;
        }
        String padding = "011";
        for (int i = 3; i < paddingLength - 1; i++) {
            padding = padding + "0";
        }
        padding = padding + "1";
        System.out.println(message + padding);
        return message + padding;
    }
    public static String toBinary (String text) {
        // convert each character to 8-bit binary representation
        String binary = "";
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            String byte_ = "";
            for (int b = 0; b < 8; b++) {
                byte_ = byte_ + ((c >> b) & 1);
            }
            binary = binary + byte_;
        }
        return binary;
    }
    public static String absorb (String message, int b, int c, int r, int w) {
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
            for (int j = 0; j < c; j++) {
                block = block + "0";
            }

            for (int j = 0; j < b; j++) {
                if (block.charAt(j) == state.charAt(j)) {
                    state = state.substring(0, j) + "0" + state.substring(j + 1);
                }
                else {
                    state = state.substring(0, j) + "1" + state.substring(j + 1);
                }
            }
            state = keccakf(state, w);
        }

        return state;
    }
    public static String squeeze (String state, int d, int r, int w) {
        // initialize an empty string Z (will be the output hash)
        // while Z is fewer than d bits long:
        // (1) append the first r bits of the state to Z
        // (2) apply the permutation function keccakf to the state to yield a new state
        String Z = "";
        while (true) {
            Z = Z + state.substring(0, r);
            if (Z.length() < d) {
                state = keccakf(state, w);
            }
            else {
                break;
            }
        }
        // truncate Z to d bits and return it as the output hash
        Z = Z.substring(0, d);
        return Z;
    }
    
    public static void main (String[] args) throws IOException {
        String filename = args[0]; // file should contain a bit string on a single line
        Scanner scan = new Scanner(new FileReader(filename));
        String message = "";
        if (scan.hasNextLine()) {
            message = scan.nextLine(); // a bit string to be hashed
        }

        int b = 1600; // state size (in standard SHA-3, the state is a 1600-bit array composed of a 5x5 grid of 64-bit words)
        int w = b / 25; // word size (the number of bits in each of the 25 words in the state; in standard SHA-3, w = 64)
        int d = 256; // digest length (the fixed length of the output hash in bits)
        int c = d * 2; // capacity (the number of bits of the state that are not directly affected by the input, this determines the security level)
        int r = b - c; // rate (the number of bits of the state that are absorbed from the input per round)

        message = toBinary(message);
        message = pad(message, r);
        String state = absorb(message, b, c, r, w);
        String hashBinary = squeeze(state, d, r, w);

        FileWriter writer = new FileWriter("output.txt");
        writer.write(toHex(hashBinary) + "\n");
        writer.write(toHexReversed(hashBinary));
        writer.close();

        // for testing:
        // the SHA3-256 hash of an empty string is: a7ffc6f8bf1ed766 51c14756a061d662 f580ff4de43b49fa 82d80a4b80f8434a
        // the SHA3-256 hash of "abc" is: 3a985da74fe225b2 045c172d6bd390bd 855f086e3e9d525b 46bfe24511431532
        // the SHA3-256 hash of "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq" is: 41c0dba2a9d62408 49100376a8235e2c 82e1b9998a999e21 db32dd97496d3376
        // the SHA3-256 hash of "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu" is: 916f6061fe879741 ca6469b43971dfdb 28b1a32dc36cb325 4e812be27aad1d18
    }
}