package decryptor;

import encryptor.Infos;

class Key {
	
	byte [] firstKey;
	int allKey[] = new int [44];
	private int totalBytes = 4;
	private int roundNumber = 10;
	
	public Key() {
		
		getFirstKey();
		expandFirstKey();
	}
	
	
	private void getFirstKey() {
		
		String key = new MyFileReader("", "key.txt").getMessage();
        System.out.println("Key: "+key);
        firstKey = key.getBytes();
	}
	
	
	private void expandFirstKey() {
		
		int temp, i = 0;

        for( ; i<4; i++) {
        	allKey[i]  = 0x00000000;
        	allKey[i] |= firstKey[4*i] << 24;
        	allKey[i] |= firstKey[4*i+1] << 16;
        	allKey[i] |= firstKey[4*i+2] << 8;
        	allKey[i] |= firstKey[4*i+3];
        }
        
        
        while (i < totalBytes * (roundNumber + 1)) {
            //System.out.println(34);
        	temp = allKey[i - 1];
            if (i % 4 == 0) {
                //  XOR with rCon.
                temp = Infos.substituteWord( Infos.rotateWord(temp)) ^ ( Infos.RCon[i / 4] << 24 );
            }
            System.out.println(temp);
            allKey[i] = allKey[i - 4] ^ temp;
            i++;
        }
        
        //for(int j=0; j<44; j++) System.out.println(allKey[j]);
	}
}