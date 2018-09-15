package decryptor;

import java.io.BufferedReader;
import java.io.FileReader;

public class MyFileReader {
	
	String message = "";
	
	String getMessage() {
		return message;
	}
	
	public MyFileReader(String path, String fileName) {
			
        try{    
            BufferedReader bf = new BufferedReader( new FileReader(path+fileName) );    
            message = bf.readLine();
            bf.close();
        }catch(Exception e){
        	System.out.println(e);
        }
        
	}
}
