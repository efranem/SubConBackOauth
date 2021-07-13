package com.telcel.authserver.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telcel.authserver.utils.KeyValue;
import com.telcel.authserver.utils.Propiedades;


@RestController
@RequestMapping(path = "props")
public class PropsController {
	
	@GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getInfo() {

        return ResponseEntity.status(200).body(getProps());
    }
	
	@PostMapping(path = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postInfo(@RequestBody List<KeyValue> lista) {

		for (KeyValue k : lista) {
			Propiedades.getInstance().setProperty(k.getKey(), k.getValue());
		}
		
        return ResponseEntity.status(200).body(getProps());
    }
	
	private String getProps() {
		StringBuilder str = new StringBuilder();
		str.append("{");
		str.append(getJsonProperty("version")).append(",");
		str.append(getJsonProperty("user.api"));
		str.append("}");
		
		return str.toString();
	}
	
	private String getJsonProperty(String prop)
	{
		String val = Propiedades.getInstance().getProperty(prop);
		return " \"" + prop + "\"" + ":" + "\"" + (val == null ? "NO ENCONTRADO" : val) + "\"";
	}

}
