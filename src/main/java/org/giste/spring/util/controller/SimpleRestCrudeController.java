package org.giste.spring.util.controller;

import org.giste.spring.util.dto.SimpleNonRemovableDto;
import org.giste.spring.util.service.SimpleCrudeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simples")
public class SimpleRestCrudeController extends RestCrudeController<SimpleNonRemovableDto> {

	public SimpleRestCrudeController(SimpleCrudeService service) {
		super(service);
	}

}
