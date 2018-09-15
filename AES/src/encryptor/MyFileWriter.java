package encryptor;

import java.io.FileWriter;

public class MyFileWriter {
	
	public MyFileWriter(String path, String fileName, String message) {
		
        try{    
            FileWriter fw=new FileWriter(path+fileName);    
            fw.write(message);    
            fw.close();    
        }catch(Exception e){
        	System.out.println(e);
        }
        
	}
}
