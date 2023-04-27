package main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DownloadFileAndMD5 {
	private static URL url;
	private static URL urlMD5;
	private static String filename = "C:/users/Aragostino/Downloads/ml-25m.zip";
	private static String md5name = "C:/users/Aragostino/Downloads/ml-25m.zip.md5";
	
	public DownloadFileAndMD5() { //constructor
		try {
			url = getURL(); 
			urlMD5 = getURLMD5();
			
			//download md5
			BufferedInputStream bisc = new BufferedInputStream(urlMD5.openStream());
			Files.copy(bisc, Paths.get(md5name), StandardCopyOption.REPLACE_EXISTING);
			
			//download file
			BufferedInputStream bisf = new BufferedInputStream(url.openStream());
			Files.copy(bisf, Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
			
			//check file with its MD5, if true: return list
			if (checksum(filename, md5name))
				printList(filename);
			else
				System.out.println("Checksum failed!");
			
		}catch (IOException ioe) {
			System.out.println("I/O error detected");
		}catch (NoSuchAlgorithmException nsae) {
			System.out.println("MD5 algorithm not found!");
		}
		
	}
	
	public boolean checksum(String filename, String md5name) throws IOException, NoSuchAlgorithmException { //checks file and its MD5
		//get the file and compute MD5
		byte[] file = Files.readAllBytes(Paths.get(filename));
		byte[] md5f = MessageDigest.getInstance("MD5").digest(file);
		//transform the buffer into a String
		StringBuilder sb = new StringBuilder();
		for (byte b: md5f)
			sb.append(String.format("%02x", b));
		String checksum = sb.toString();
		
		//get the checksum file and extract MD5 value
		file = Files.readAllBytes(Paths.get(md5name));
		String md5String = new String(file, StandardCharsets.UTF_8);
		//extract MD5 from String
		Pattern p = Pattern.compile("[a-fA-F0-9]{32}");
		Matcher m = p.matcher(md5String);
		String md5 = null;
		if (m.find()) { md5 = m.group();}
		
		//check the equivalence
		return (!md5.equals(null) && checksum.equals(md5));
	}

	public void printList(String filename){ 
		//gets the list of files from a zip
		try {
			ZipFile file = new ZipFile(filename); //zip file as an object
			Enumeration<? extends ZipEntry> entries = file.entries(); //get entries
			while (entries.hasMoreElements()) 
				System.out.println(entries.nextElement().getName()); //print each entry name
			file.close();	
		}catch(IOException ioe) {ioe.printStackTrace();}
		
	} //printList
	
	
	public URL getURL() { //gets the file URL
		try {
			return new URL ("http://files.grouplens.org/datasets/movielens/ml-25m.zip");
		}catch (MalformedURLException me) {
			me.printStackTrace();
		}//catch
		return null;
	}//getURL
	
	public URL getURLMD5() { //gets the MD5 URL
		try {
			return new URL("http://files.grouplens.org/datasets/movielens/ml-25m.zip.md5"); 
		}catch (MalformedURLException me) {
			me.printStackTrace();
		}//catch
		return null;
	}//getURLMD5
	
	
	public static void main(String[] args) {
		new DownloadFileAndMD5();
		
	}
}