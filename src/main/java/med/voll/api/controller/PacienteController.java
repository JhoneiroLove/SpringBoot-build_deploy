package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.paciente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    @Autowired
    private PacienteRepository pacienteRepository;
    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaPaciente> registrarPaciente(@RequestBody @Valid DatosRegistroPaciente datosRegistroPaciente,
                                                                    UriComponentsBuilder uriComponentsBuilder) {
        Paciente paciente = pacienteRepository.save(new Paciente(datosRegistroPaciente));
        DatosRespuestaPaciente datosRespuestaPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(),
                paciente.getTelefono(), paciente.getDocumentoIdentidad()
        );

        URI url = uriComponentsBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaPaciente);
    }
    @GetMapping
    public ResponseEntity<Page<DatosListadoPaciente>> listadoPacientes(@PageableDefault(page = 0, size = 4, sort = {"nombre"}) Pageable paginacion) {
        return ResponseEntity.ok(pacienteRepository.findAll(paginacion).map(DatosListadoPaciente::new));
    }
    @PutMapping
    @Transactional
    public ResponseEntity actualizarPaciente(@RequestBody @Valid DatosActualizarPaciente datosActualizarPaciente) {
        Paciente paciente = pacienteRepository.getReferenceById(datosActualizarPaciente.id());
        paciente.actualizarDatos(datosActualizarPaciente);
        return ResponseEntity.ok(new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(),
                paciente.getTelefono(), paciente.getDocumentoIdentidad()
        ));
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        paciente.desactivarPaciente();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaPaciente> retornaDatosPacientes(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        var datosPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(),
                paciente.getTelefono(), paciente.getDocumentoIdentidad()
        );
        return ResponseEntity.ok(datosPaciente);
    }
}
