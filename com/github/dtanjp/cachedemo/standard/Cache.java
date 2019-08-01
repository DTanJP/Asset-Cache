package com.github.dtanjp.cachedemo.standard;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Cache.java
 * Description: 
 * This is the [ STANDARD ] cache class which supports only:
 * Loading from cache files
 * 
 * This is for the standard use of application/game development
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

	/** Load the cache and its content into memory **/
	public void load() {
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

	/** 
	 * Access the files hashmap the byte[] content
	 **/
	public byte[] get(String s) {
		if(files == null) return null;
		if(files.isEmpty()) return null;
		if(files.get(s) != null)
			return files.get(s);
		return null;
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