package com.bolsadeideas.sprintboot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.sprintboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.sprintboot.backend.apirest.models.services.IClienteService;
import com.sun.tools.javac.code.Attribute.Array;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	static final String SUCCESS = "success";
	static final String MENSAJE_OK = "ok";
	static final String MENSAJE_FAILED = "failed";

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/v1/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}

	@GetMapping("/v1/clientes/page/{page}")
	public Page<Cliente> all(@PathVariable Integer page) {
		return clienteService.findAll(PageRequest.of(page, 10));
	}
	
	@PostMapping("/v1/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		Cliente client = null;
		Map<String, Object> response = new HashMap<>();
		
		/*
		List<String> errors = new ArrayList<>();
		
		for(FieldError err : result.getFieldErrors()) {
			errors.add("El campo '" + err.getField() + "' " + err.getDefaultMessage());
		}*/
		
		List<String> errors = this.errors(result);
		
		if(result.hasErrors()) {
			response.put("error", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			client = clienteService.save(cliente);

		} catch (DataAccessException e) {
			response.put("error", "Ocurrio un error: "
					.concat(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put(SUCCESS,MENSAJE_OK);
		response.put("data", client);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/v1/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();

		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("error", "Ocurrio un error: "
					.concat(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cliente == null) {
			response.put("data", "client ID: ".concat(id.toString().concat(" NOT FOUND")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}

	@PutMapping("/v1/clientes/{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente,BindingResult result, @PathVariable Long id) {
		Cliente clienteActual = null;
		Map<String, Object> response = new HashMap<>();
		Cliente client = null;
		
		List<String> errors = this.errors(result);
				
		if(result.hasErrors()) {
			response.put("error", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteActual = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("error", "Ocurrio un error: "
					.concat(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setEmail(cliente.getEmail());
		try {
			client = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("error", "Ocurrio un error: "
					.concat(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Cliente>(client, HttpStatus.OK);
	}

	@DeleteMapping("/v1/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("error", "Ocurrio un error: "
					.concat(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put(SUCCESS, MENSAJE_OK);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		
	}
	
	public List<String> errors(BindingResult result){
		List<String> errors = result.getFieldErrors()
				.stream()
				.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
				.collect(Collectors.toList());
		return errors;
	}

}
