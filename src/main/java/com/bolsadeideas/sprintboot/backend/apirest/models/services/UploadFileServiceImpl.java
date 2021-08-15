package com.bolsadeideas.sprintboot.backend.apirest.models.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Service
public class UploadFileServiceImpl implements IUploadFileService{

	private static final String DIRECTORIO_UPLOAD = "upload";
	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);
	@Override
	public Resource uploadImg(String namePhoto) throws MalformedURLException {
		Path rutaArchivo = getPath(namePhoto);
		log.info(rutaArchivo.toString());
		
		Resource recurso = new UrlResource(rutaArchivo.toUri());
		
		if(!recurso.exists() && !recurso.isReadable()) {
			rutaArchivo = Paths.get("src/main/resources/static/images").resolve("no-usuario.png").toAbsolutePath();
			
			recurso = new UrlResource(rutaArchivo.toUri());
			
			log.error("Error no se pudo cargar la imagen: " + namePhoto);
			
		}
		return recurso;
	}

	@Override
	public String saveImg(MultipartFile archivo) throws IOException {
		String nombreArchivo = UUID.randomUUID().toString() + "_" +archivo.getOriginalFilename().replace(" ","");
		Path rutaArchivo = getPath(nombreArchivo);
		log.info(rutaArchivo.toString());
		Files.copy(archivo.getInputStream(), rutaArchivo);
		return nombreArchivo;
	}

	@Override
	public boolean deleteImg(String foto) {
		if(foto != null && foto.length() > 0) {
			Path rutaFotoAnt = Paths.get("upload").resolve(foto).toAbsolutePath();
			log.info(rutaFotoAnt.toString());
			File archivoFotoAnt = rutaFotoAnt.toFile();
			
			if(archivoFotoAnt.exists() && archivoFotoAnt.canRead()) {
				archivoFotoAnt.delete();
				return true;
			}
			
		}
		return false;
	}

	@Override
	public Path getPath(String foto) {
		return Paths.get(DIRECTORIO_UPLOAD).resolve(foto).toAbsolutePath();
	}

}
