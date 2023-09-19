package io.bootify.visitor_management_app.rest;

import io.bootify.visitor_management_app.domain.Visit;
import io.bootify.visitor_management_app.model.VisitDTO;
import io.bootify.visitor_management_app.model.VisitorDTO;
import io.bootify.visitor_management_app.service.VisitService;
import io.bootify.visitor_management_app.service.VisitorService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/gateKeeper")
public class GatekeeperPanelController {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private VisitService visitService;

    @PostMapping("/createVisitor")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createVisitor(@RequestBody @Valid final VisitorDTO visitorDTO) {
        final Long createdId = visitorService.create(visitorDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/createVisit")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createVisit(@RequestBody final VisitDTO visitDTO) {
        final Long createdId = visitService.create(visitDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/markEntry/{id}")
    public ResponseEntity<String> markEntry(@PathVariable final Long id){
        return ResponseEntity.ok(visitService.markEntry(id));
    }
    @PutMapping("/markExit/{id}")
    public ResponseEntity<String> markExit(@PathVariable final Long id){
        return ResponseEntity.ok(visitService.markExit(id));
    }
    @PostMapping("/uploadPhoto")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String response = "";
        try {
            String uploadPath = "E:\\PROGRAMS\\java programes\\visitor-photos\\" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadPath));
            response = uploadPath;
        } catch (Exception ex){
            response="Exception "+ex.getMessage();
        }
        return ResponseEntity.ok().body(response);
    }



}
