package io.bootify.visitor_management_app.rest;

import io.bootify.visitor_management_app.domain.Visit;
import io.bootify.visitor_management_app.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resident")
public class ResidentPanelController {

    @Autowired
    private VisitService visitService;
    @PutMapping("/approveVisit/{id}")
    public ResponseEntity<Long> approveVisit(@PathVariable Long id){
        visitService.approveVisit(id);
        return ResponseEntity.ok(id);
    }


    @PutMapping("/rejectVisit/{id}")
    public ResponseEntity<Long> rejectVisit(@PathVariable Long id){
        visitService.rejectVisit(id);
        return ResponseEntity.ok(id);
    }

}
