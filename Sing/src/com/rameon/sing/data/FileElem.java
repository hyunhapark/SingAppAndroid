package com.rameon.sing.data;

public class FileElem {

	private String fileName;
	private String date;
	private String length;
	private String path;
	
	public FileElem() {}
	public FileElem(String fileName, String date, String length, String path) {
		this.fileName = fileName;
		this.date = date;
		this.length = length;
		this.path = path;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	@Override
	public String toString() {
		return fileName + "$" + date + "$" + length + "$" + path;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null) return false;
		else if(fileName != ((FileElem)o).getFileName()) return false;
		else if(date != ((FileElem)o).getDate()) return false;
		else if(length != ((FileElem)o).getLength()) return false;
		else if(path != ((FileElem)o).getPath()) return false;
		
		return true;
	}
	
	
}
