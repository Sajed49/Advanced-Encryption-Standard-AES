package decryptor;
import encryptor.Infos;

public class Decryptor {
	
	private int excessChars = 0;
	private int totalPackets = 0;
	private String packets[];
	private String encryptedRawMeassage = "";
	
	private int numberOfRounds = 10;
	private int numberOfBytes = 4;
	
	
	Key key;
	
	
	public static void main(String[] args) {

		new Decryptor();
	}
	
	public Decryptor() {
		
		key = new Key();
		getExcessChars();
		
		getEncryptedMessage();
		breakIntoPackets();
		
		decryptTotalMessage();
	}
	
	private void decryptTotalMessage() {
		//System.out.println(encryptedRawMeassage);
		String output = "";
		//System.out.println(totalPackets);
		for(int i=0; i<totalPackets; i++) {
        	
			//System.out.println(packets[i]);
        	int save[][] = new int[4][4];
            for(int j=0; j<4; j++) {
            	for(int k=0; k<4; k++) {
            		save[j][k] = packets[i].charAt( (j*4)+k );
            		//System.out.print( (char)save[j][k] );
            	}
            }
            //System.out.println();
            
            save = decryptPacket(save);
            //System.out.println(save);
            for(int j=0; j<4; j++) {
            	for(int k=0; k<4; k++) {
            		output += (char)save[j][k];
            		//System.out.print((char)save[j][k]);
            	}
            }
        }
		System.out.println(output);
	}
	
	
	private int[][] decryptPacket(int[][] data) {
		
		int currentRound = numberOfRounds;
        addRoundKey(data, currentRound);

        for (currentRound = numberOfRounds - 1; currentRound > 0; currentRound--) {
            inverseShiftRows(data);
            inverseSubstituteBytes(data);
            addRoundKey(data, currentRound);
            inverseMixColumns(data);
        }
        inverseShiftRows(data);
        inverseSubstituteBytes(data);
        addRoundKey(data, currentRound);
        
        return data;
	}
	

	private int[][] addRoundKey(int[][] s, int round) {
        for (int c = 0; c < numberOfBytes; c++) {
            for (int r = 0; r < 4; r++) {
                s[r][c] = s[r][c] ^ ((key.allKey[round * numberOfBytes + c] << (r * 8)) >>> 24);
            }
        }
        return s;
    }
	
	private int[][] inverseShiftRows(int[][] state) {
        int temp1, temp2, temp3, i;
        int Nb = 4;
        // row 1;
        temp1 = state[1][Nb - 1];
        for (i = Nb - 1; i > 0; i--) {
            state[1][i] = state[1][(i - 1) % Nb];
        }
        state[1][0] = temp1;
        // row 2
        temp1 = state[2][Nb - 1];
        temp2 = state[2][Nb - 2];
        for (i = Nb - 1; i > 1; i--) {
            state[2][i] = state[2][(i - 2) % Nb];
        }
        state[2][1] = temp1;
        state[2][0] = temp2;
        // row 3
        temp1 = state[3][Nb - 3];
        temp2 = state[3][Nb - 2];
        temp3 = state[3][Nb - 1];
        for (i = Nb - 1; i > 2; i--) {
            state[3][i] = state[3][(i - 3) % Nb];
        }
        state[3][0] = temp1;
        state[3][1] = temp2;
        state[3][2] = temp3;

        return state;
    }
	
	private int[][] inverseSubstituteBytes(int[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = inverseSubstituteWord(state[i][j]) & 0xFF;
            }
        }
        return state;
    }
	
	private static int inverseSubstituteWord(int word) {
        int subWord = 0;
        for (int i = 24; i >= 0; i -= 8) {
            int in = word << i >>> 24;
            subWord |= Infos.reverseSBox[in] << (24 - i);
        }
        return subWord;
    }
	
	private int[][] inverseMixColumns(int[][] state) {
        int temp0, temp1, temp2, temp3;
        for (int c = 0; c < numberOfBytes; c++) {
            temp0 = mult(0x0e, state[0][c]) ^ mult(0x0b, state[1][c]) ^ mult(0x0d, state[2][c]) ^ mult(0x09, state[3][c]);
            temp1 = mult(0x09, state[0][c]) ^ mult(0x0e, state[1][c]) ^ mult(0x0b, state[2][c]) ^ mult(0x0d, state[3][c]);
            temp2 = mult(0x0d, state[0][c]) ^ mult(0x09, state[1][c]) ^ mult(0x0e, state[2][c]) ^ mult(0x0b, state[3][c]);
            temp3 = mult(0x0b, state[0][c]) ^ mult(0x0d, state[1][c]) ^ mult(0x09, state[2][c]) ^ mult(0x0e, state[3][c]);

            state[0][c] = temp0;
            state[1][c] = temp1;
            state[2][c] = temp2;
            state[3][c] = temp3;
        }
        return state;
    }
	
    private int mult(int a, int b) {
        int sum = 0;
        while (a != 0) { // while it is not 0
            if ((a & 1) != 0) { // check if the first bit is 1
                sum = sum ^ b; // add b from the smallest bit
            }
            b = xtime(b); // bit shift left mod 0x11b if necessary;
            a = a >>> 1; // lowest bit of "a" was used so shift right
        }
        return sum;

    }
    
    private int xtime(int b) {
        if ((b & 0x80) == 0) {
            return b << 1;
        }
        return (b << 1) ^ 0x11b;
    }	
	
	private void getExcessChars() {
		excessChars = Integer.parseInt( new MyFileReader("", "otherInfo.txt").getMessage() );
	}
	
	private void getEncryptedMessage() {
		encryptedRawMeassage = new MyFileReader("", "encrypted.txt").getMessage();
		
	}
	
	private void breakIntoPackets() {
		
		int len = encryptedRawMeassage.length();
		totalPackets = len/16;
		packets = new String[totalPackets];
		
		for(int i=0; i<totalPackets; i++) {
			packets[i] = encryptedRawMeassage.substring(i*16, (i*16)+16);
			//System.out.println(packets[i]);
		}
		
	}
}
