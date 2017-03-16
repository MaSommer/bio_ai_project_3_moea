package moea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileAdministrator {
	
	private PrintWriter writer;
	private String filename;
	
	public FileAdministrator(String filename){
		try{
			this.filename = filename;
		    writer = new PrintWriter(filename, "UTF-8");
		} catch (IOException e) {
		   System.out.println(e);
		}
	}
	
	public void writeMST(ArrayList<Pixel> pixelsMST){
		for (Pixel p : pixelsMST) {
			String add = "" + p.getId() + "\n";
			writer.write(add);
		}
		writer.close();
	}
	
	public ArrayList<Pixel> readMST(ArrayList<Pixel> pixels, String filename) throws IOException{
		FileReader fr = new FileReader(filename);
		System.out.println(fr.ready());
		BufferedReader br = new BufferedReader(new FileReader(filename));	

		
		ArrayList<Pixel> pixelsMST = new ArrayList<Pixel>();
		String line = "";
		while((line = br.readLine()) != null){
			pixelsMST.add(pixels.get(Integer.parseInt(line)));
			System.out.println("cock");
		}
		br.close();
		return pixelsMST;
	}

}
