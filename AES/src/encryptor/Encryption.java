package encryptor;

import javax.swing.JOptionPane;

public class Encryption {
	
	private int excessChars = 0;
	private int totalPackets = 0;
	private String packets[];
	private String encryptedText[];
	private int numberOfRounds = 10;
	private int numberOfBytes = 4;
	
	
	Key key;
	
	public static void main(String[] args) {

		new Encryption();

	}
	
	
	public Encryption() {
		
		key = new Key();
		
		String input = JOptionPane.showInputDialog("Please enter text: ");
		input = fillBlock(input);
		messageFragmenter(input);
		
		encryptPacketMessage();
	}
	
	
	
	private int[][] parseInputBlock(String temp){
		
		int grid[][] = new int[4][4];
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				grid[i][j] = temp.charAt( (i*4)+j );
				//System.out.println(grid[i][j]);
			}
		}
		return grid;
	}
	
	private void encryptPacketMessage() {
		
		String encryptedString = "";
		
        for(int i=0; i<totalPackets; i++) {
        	int[][] save = parseInputBlock( packets[i] );
        	save = encryptPacket(save);
        	
        	String temp="";
        	for(int j=0; j<4; j++) {
        		for(int k=0; k<4; k++) {
        			temp += (char)save[j][k];
        		}
        	}
        	
        	encryptedString += temp;
        	encryptedText[i] = temp;
        }
        
        //write encrypted message to a file
        new MyFileWriter("", "encrypted.txt", encryptedString);
        
        //write key to a file
        new MyFileWriter("", "key.txt", new String( key.firstKey ));
        
        //save the number of filler bytes
        new MyFileWriter("", "otherInfo.txt",  Integer.toString( excessChars) );
	}
	
	
	private int[][] encryptPacket(int data[][]) {
		
        int cureentRound = 0;
        addRoundKey(data, cureentRound);

        for (cureentRound = 1; cureentRound < numberOfRounds; cureentRound++) {
            substituteBytes(data);
            shiftRows(data);
            mixTheColumns(data);
            addRoundKey(data, cureentRound);
        }
        substituteBytes(data);
        shiftRows(data);
        addRoundKey(data, cureentRound);
        return data;
	}
	
	private void messageFragmenter(String text) {

		int len = text.length();
		totalPackets = len/16;
		packets = new String[totalPackets];
		
		encryptedText = new String[totalPackets];
		
		for(int i=0; i<totalPackets; i++) {
			packets[i] = text.substring(i*16, (i*16)+16);
			//System.out.println(packets[i]);
		}
		
		//for(String s: packets) System.out.println(s);
		
	}
	
	private int[][] addRoundKey(int[][] s, int round) {
        for (int c = 0; c < numberOfBytes; c++) {
            for (int r = 0; r < 4; r++) {
                s[r][c] = s[r][c] ^ (( key.allKey[round * numberOfBytes + c] << (r * 8)) >>> 24);
            }
        }
        return s;
    }
	
	private int[][] substituteBytes(int[][] data) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < numberOfBytes; j++) {
                data[i][j] = Infos.substituteWord(data[i][j]) & 0xFF;
            }
        }
        return data;
	}
	
	private int[][] shiftRows(int[][] state) {
        int temp1, temp2, temp3, i;

        // row 1
        temp1 = state[1][0];
        for (i = 0; i < numberOfBytes - 1; i++) {
            state[1][i] = state[1][(i + 1) % numberOfBytes];
        }
        state[1][numberOfBytes - 1] = temp1;

        // row 2
        temp1 = state[2][0];
        temp2 = state[2][1];
        for (i = 0; i < numberOfBytes - 2; i++) {
            state[2][i] = state[2][(i + 2) % numberOfBytes];
        }
        state[2][numberOfBytes - 2] = temp1;
        state[2][numberOfBytes - 1] = temp2;

        // row 3
        temp1 = state[3][0];
        temp2 = state[3][1];
        temp3 = state[3][2];
        for (i = 0; i < numberOfBytes - 3; i++) {
            state[3][i] = state[3][(i + 3) % numberOfBytes];
        }
        state[3][numberOfBytes - 3] = temp1;
        state[3][numberOfBytes - 2] = temp2;
        state[3][numberOfBytes - 1] = temp3;

        return state;
    }
	
	private int[][] mixTheColumns(int[][] state) {
        int temp0, temp1, temp2, temp3;
        for (int c = 0; c < numberOfBytes; c++) {

            temp0 = mult(0x02, state[0][c]) ^ mult(0x03, state[1][c]) ^ state[2][c] ^ state[3][c];
            temp1 = state[0][c] ^ mult(0x02, state[1][c]) ^ mult(0x03, state[2][c]) ^ state[3][c];
            temp2 = state[0][c] ^ state[1][c] ^ mult(0x02, state[2][c]) ^ mult(0x03, state[3][c]);
            temp3 = mult(0x03, state[0][c]) ^ state[1][c] ^ state[2][c] ^ mult(0x02, state[3][c]);

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
	
	private String fillBlock(String text) {

		int neededChars = 16 - (text.length()%16);
		excessChars = neededChars;
		for(int i=0; i<neededChars; i++) text += " ";
		return text;
    }
}
