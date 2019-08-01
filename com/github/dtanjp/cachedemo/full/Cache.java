package com.github.dtanjp.cachedemo.full;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Cache.java
 * Description: 
 * This is the [ FULL ] cache class and it has all of functionalities
 * Extracting
 * Packing
 * Loading
 * 
 * This version is only if you plan to write your own cache packer tool
 * @author David Tan
 */
public class Cache {

	/** Constructor **/
	public Cache(String path) {
		cachePath = path;
		cacheFile = new File(cachePath);
	}
	
	/** Constructor **/
	public Cache(File path) {
		if(path != null)
			cachePath = path.getAbsolutePath();
		cacheFile = path;
	}
	
	/** Extract only 1 cachefile **/
	public void extract(String directory, String name) {
		File dir = new File(directory);
		if(dir.exists() && !dir.isDirectory()) {
			new Exception("[Cache]: Cannot extract to "+directory+". Invalid directory.").printStackTrace();
			return;
		}
		if(get(name) == null) return;
		directory = (directory.endsWith("/")) ? directory : directory+"/";
		String path = directory+name;
		//Start writing data
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(path)))){
			out.write(get(name));//Byte content data
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** Packs multiple files into 1 cache file **/
	public void pack() {
		if(cacheFile == null && cachePath.length() == 0) {
			new Exception("[Cache]: Cache file not set");
			return;
		}

		//Make sure the cacheFile and cachePath are the same
		cacheFile = new File(cachePath);

		//Decide what to do if cacheFile exist or does not exist
		if(!cacheFile.exists()) {
			try {
				cacheFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Clear the file
		clearFile();

		//Start writing data
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(cacheFile))){
			//Amount of files
			out.writeInt(files.size());

			for(String s : files.keySet()) {
				if(files.get(s) != null) {
					byte[] data = compress(files.get(s));
					out.writeInt(s.length());//Length of the pathname
					out.writeChars(s);//Path name
					out.writeInt(data.length);//Length of the byte content
					out.write(data);//Byte content data
				}
			}

			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Quickly register + pack image into the cache
	 * Note: Practical for use when needed to quickly
	 * create a cache on the go with only a few files
	 **/
	public void pack(String path) {
		boolean reset = this.pack_overwrite;
		this.pack_overwrite = true;
		registerFile(path);
		pack();
		this.pack_overwrite = reset;
	}

	/** Load the cache and its content into memory **/
	public void load() {
		//Clear the current session
		files.clear();
		loaded = false;
		//Start reading
		try(DataInputStream in = new DataInputStream(new FileInputStream(cacheFile))) {
			int count = in.readInt();
			totalFilesCount = count;
			for(int i=0; i<count; i++) {
				//Read the content path name
				String pathName = "";
				int name_length = in.readInt();
				for(int s=0; s<name_length; s++)
					pathName += in.readChar();
				//Read the content byte[] data
				byte[] data = new byte[in.readInt()];
				in.read(data);
				//Place the result into the hashmap
				files.put(pathName, decompress(data));
				loadedFilesCount++;
			}
		} catch (IOException | DataFormatException e) {
		}
		loaded = true;
	}
	
	/** Register a file into memory ready to be packed into a cache **/
	public void registerFile(String file) {
		if(cacheFile == null || cachePath.length() == 0) {
			new Exception("[Cache]: Cache file not set");
			return;
		}

		File newFile = new File(file);
		if(!newFile.exists() || !newFile.isFile()) {
			new Exception("[Cache]: Unable to register "+file+".");
			return;
		}
		String fileName = newFile.getName();
		//Read the file's bytes into (byte[])data
		byte[] data = new byte[(int)newFile.length()];
		try(FileInputStream inputStream = new FileInputStream(newFile)) {
			inputStream.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		files.put(fileName, data);
		totalFilesCount++;
	}

	/** Compresses byte[] **/
	private byte[] compress(byte[] data) {
		byte[] result = null;
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		result = outputStream.toByteArray();
		outputStream.flush();
		} catch(IOException e) {
			return result;
		}
		return result;
	}

	/** Decompresses byte[] **/
	private byte[] decompress(byte[] data) throws DataFormatException {
		byte[] result = null;
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		result = outputStream.toByteArray();
		outputStream.flush();
		} catch(IOException e) {
			return result;
		}
		return result;
	}

	/**  Access the files hashmap the byte[] content **/
	public byte[] get(String s) {
		if(files == null) return null;
		if(files.isEmpty()) return null;
		if(files.get(s) != null)
			return files.get(s);
		return null;
	}

	/** Clears the cache file contents **/
	public void clearFile() {
		if(cacheFile.exists() && cacheFile.isFile()) {
			try (BufferedWriter worker = new BufferedWriter(new FileWriter(cacheFile, false))){
			} catch (IOException ioexception) {
			}
		}
	}
	
	/** Removes an entry from memory **/
	public void unregister(String name) {
		if(name == null) return;
		if(get(name) == null && !files.containsKey(name)) return;
		files.remove(name);
		totalFilesCount--;
	}
	
	public boolean exist() { return cacheFile.exists() && cacheFile.isFile() && cacheFile.getAbsolutePath().endsWith(".cache"); }
	
	/** Getters **/
	public File getCacheFile() { return cacheFile; }
	public String getCachePath() { return cachePath; }
	public boolean isLoaded() { return loaded; }
	public int getCacheSize() { return files.size(); }
	public Set<String> fileNames() { return files.keySet(); }
	public int getTotalFiles() { return totalFilesCount; }
	public int getLoadedFilesCount() { return loadedFilesCount; }
	
	/** Instances **/
	private File cacheFile;
	private Map<String, byte[]> files = new HashMap<>();
	
	/** Variables **/
	private boolean loaded = false;
	public boolean pack_overwrite = false;
	private String cachePath = "";
	private int totalFilesCount = 0;
	private int loadedFilesCount = 0;
}