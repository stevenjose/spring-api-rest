package com.bolsadeideas.sprintboot.backend.apirest.models.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	public Resource uploadImg( String namePhoto) throws MalformedURLException;
	
	public String saveImg(MultipartFile archivo) throws IOException;
	
	public boolean deleteImg(String foto);
	
	public Path getPath(String foto);
	
}
