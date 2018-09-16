package encryptor;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class MyFileWriter {
	
	public MyFileWriter(String path, String fileName, String message) {
		
		
		try{    
            BufferedWriter br = new BufferedWriter( new FileWriter(path+fileName) );    
            br.write(message);
            br.close();
        }catch(Exception e){
        	System.out.println(e);
        }
        
	}
}
