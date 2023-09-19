package io.bootify.visitor_management_app.rest;

import io.bootify.visitor_management_app.model.AddressDTO;
import io.bootify.visitor_management_app.model.UserDTO;
import io.bootify.visitor_management_app.model.UserStatus;
import io.bootify.visitor_management_app.model.VisitDTO;
import io.bootify.visitor_management_app.service.UserService;
import io.bootify.visitor_management_app.service.VisitService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminPanelController {
    @Autowired
    private UserService userService;

    @Autowired
    private VisitService visitService;


    @PostMapping("/createUser")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createUser(@RequestBody final UserDTO userDTO) {
        final Long createdId = userService.create(userDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }
    @PutMapping("/markInactive/{id}")
    public ResponseEntity<Long> markInactive(@PathVariable(name = "id") final Long id) {
        userService.markInactive(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/getAllVisits")
    public ResponseEntity<List<VisitDTO>> getAllVisits(@RequestParam Integer pageSize , @RequestParam Integer pageNo){
        Pageable pageable= Pageable.ofSize(pageSize).withPage(pageNo);
        List<VisitDTO> visits=visitService.findAll(pageable);
        return ResponseEntity.ok(visits);
    }

    @PostMapping("/uploadCSV")
    public ResponseEntity<List<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        List<String> response = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            List<UserDTO> userDTOList = new ArrayList<>();
            for(CSVRecord csvRecord : csvRecords){
                UserDTO userDTO = new UserDTO();
                userDTO.setName(csvRecord.get("name"));
                userDTO.setEmail(csvRecord.get("email"));
                userDTO.setPhone(csvRecord.get("phone"));
                userDTO.setFlat(csvRecord.get("flat"));
                userDTO.setRole(csvRecord.get("role"));
                userDTO.setStatus(UserStatus.valueOf(csvRecord.get("status")));
                userDTO.setPassword(csvRecord.get("password"));
                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setLine1(csvRecord.get("line1"));
                addressDTO.setLine2(csvRecord.get("line2"));
                addressDTO.setCity(csvRecord.get("city"));
                addressDTO.setPincode(csvRecord.get("pincode"));
                addressDTO.setCountry(csvRecord.get("country"));
                userDTO.setAddressDTO(addressDTO);
                try{
                    Long id = userService.create(userDTO);
                    response.add("Created user "+userDTO.getName()+" with id:"+id);
                }
                catch (Exception ex){
                    response.add("Exception while creating user "+userDTO.getName());
                }
            }

        }
        catch (Exception e){
            response.add("Exception :"+e.getMessage());
        }

        return ResponseEntity.ok(response);
    }





}
