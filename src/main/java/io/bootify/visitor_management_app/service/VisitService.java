package io.bootify.visitor_management_app.service;

import io.bootify.visitor_management_app.config.SecurityConfig;
import io.bootify.visitor_management_app.domain.Flat;
import io.bootify.visitor_management_app.domain.User;
import io.bootify.visitor_management_app.domain.Visit;
import io.bootify.visitor_management_app.domain.Visitor;
import io.bootify.visitor_management_app.model.VisitDTO;
import io.bootify.visitor_management_app.model.VisitStatus;
import io.bootify.visitor_management_app.repos.FlatRepository;
import io.bootify.visitor_management_app.repos.UserRepository;
import io.bootify.visitor_management_app.repos.VisitRepository;
import io.bootify.visitor_management_app.repos.VisitorRepository;
import io.bootify.visitor_management_app.util.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class VisitService {


    @Autowired
    private UserRepository userRepository;


    private final VisitRepository visitRepository;
    private final VisitorRepository visitorRepository;
    private final FlatRepository flatRepository;


    public VisitService(final VisitRepository visitRepository,
            final VisitorRepository visitorRepository, final FlatRepository flatRepository) {
        this.visitRepository = visitRepository;
        this.visitorRepository = visitorRepository;
        this.flatRepository = flatRepository;
    }

    public List<VisitDTO> findAll() {
        final List<Visit> visits = visitRepository.findAll(Sort.by("id"));
        return visits.stream()
                .map(visit -> mapToDTO(visit, new VisitDTO()))
                .toList();
    }

    public VisitDTO get(final Long id) {
        return visitRepository.findById(id)
                .map(visit -> mapToDTO(visit, new VisitDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VisitDTO visitDTO) {
        final Visit visit = new Visit();
        mapToEntity(visitDTO, visit);
        return visitRepository.save(visit).getId();
    }

    public void update(final Long id, final VisitDTO visitDTO) {
        final Visit visit = visitRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(visitDTO, visit);
        visitRepository.save(visit);
    }

    public void approveVisit(final Long id ){
        Visit visit= visitRepository.findById(id).orElseThrow(()->new NotFoundException("Visit Not Found"));
        Flat flat= flatRepository.findById(visit.getFlat().getId()).get();
        User user = userRepository.findById(flat.getUser().getId()).get();
        UserDetails userDetails= (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!userDetails.getUsername().equals(user.getUsername())){
            throw new NotFoundException("Visit Not Found");
        }
        if(visit.getStatus().equals(VisitStatus.WAITING)){
            visit.setStatus(VisitStatus.APPROVED);
            visitRepository.save(visit);
        }
    }
    public void rejectVisit(final Long id ){
        Visit visit= visitRepository.findById(id).orElseThrow(()->new NotFoundException("Visit Not Found"));
        Flat flat= flatRepository.findById(visit.getFlat().getId()).get();
        User user = userRepository.findById(flat.getUser().getId()).get();
        UserDetails userDetails= (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!userDetails.getUsername().equals(user.getUsername())){
            throw new NotFoundException("Visit Not Found");
        }
        if(visit.getStatus().equals(VisitStatus.WAITING)){
            visit.setStatus(VisitStatus.REJECTED);

        }
        visitRepository.save(visit);
    }


    public String markEntry(final Long id){
        Visit visit= visitRepository.findById(id).get();
        if(visit.getStatus().equals(VisitStatus.APPROVED)){
            visit.setInTime(LocalDateTime.now());
            visitRepository.save(visit);
            return "Marked Entry for:"+id;
        }
        else{
            return "Something went wrong with id:"+id;
        }

    }

    public String markExit(final Long id){
        Visit visit= visitRepository.findById(id).get();
        if(visit.getStatus().equals(VisitStatus.APPROVED)){
            visit.setOutTime(LocalDateTime.now());
            visit.setStatus(VisitStatus.COMPLETED);
            visitRepository.save(visit);
            return "Marked Exit for:"+id;
        }
        else{
            return "Something went wrong with id:"+id;

        }

    }

    public List<VisitDTO> findAll(Pageable pageable) {
        //final List<Visit> visits = visitRepository.findAll(Sort.by("id"));
        final List<Visit> visits = visitRepository.findAll(pageable).toList();
        return visits.stream()
                .map(visit -> mapToDTO(visit, new VisitDTO()))
                .toList();
    }

    public void delete(final Long id) {
        visitRepository.deleteById(id);
    }

    private VisitDTO mapToDTO(final Visit visit, final VisitDTO visitDTO) {
        visitDTO.setId(visit.getId());
        visitDTO.setStatus(visit.getStatus());
        visitDTO.setInTime(visit.getInTime());
        visitDTO.setOutTime(visit.getOutTime());
        visitDTO.setPurpose(visit.getPurpose());
        visitDTO.setImageURL(visit.getImageURL());
        visitDTO.setNumOfPeople(visit.getNumOfPeople());
        visitDTO.setVisitor(visit.getVisitor() == null ? null : visit.getVisitor().getId());
        visitDTO.setFlat(visit.getFlat() == null ? null : visit.getFlat().getId());
        return visitDTO;
    }

    private Visit mapToEntity(final VisitDTO visitDTO, final Visit visit) {
        visit.setStatus(VisitStatus.WAITING);
        visit.setInTime(visitDTO.getInTime());
        visit.setOutTime(visitDTO.getOutTime());
        visit.setPurpose(visitDTO.getPurpose());
        visit.setImageURL(visitDTO.getImageURL());
        visit.setNumOfPeople(visitDTO.getNumOfPeople());
        final Visitor visitor = visitDTO.getVisitor() == null ? null : visitorRepository.findById(visitDTO.getVisitor())
                .orElseThrow(() -> new NotFoundException("visitor not found"));
        visit.setVisitor(visitor);
        final Flat flat = visitDTO.getFlat() == null ? null : flatRepository.findById(visitDTO.getFlat())
                .orElseThrow(() -> new NotFoundException("flat not found"));
        visit.setFlat(flat);
        return visit;
    }

}
