package io.bootify.visitor_management_app.service;

import io.bootify.visitor_management_app.domain.Address;
import io.bootify.visitor_management_app.domain.Flat;
import io.bootify.visitor_management_app.domain.User;
import io.bootify.visitor_management_app.model.AddressDTO;
import io.bootify.visitor_management_app.model.UserDTO;
import io.bootify.visitor_management_app.model.UserStatus;
import io.bootify.visitor_management_app.repos.AddressRepository;
import io.bootify.visitor_management_app.repos.FlatRepository;
import io.bootify.visitor_management_app.repos.UserRepository;
import io.bootify.visitor_management_app.util.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final AddressRepository addressRepository;

    public UserService(final UserRepository userRepository, final FlatRepository flatRepository,
            final AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
        this.addressRepository = addressRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return user.getId();
    }

    public void markInactive(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());
        userDTO.setStatus(user.getStatus());
        Flat flat = flatRepository.findByNumber(user.getFlat().getNumber());
        userDTO.setFlat(flat.getNumber());

        AddressDTO addressDTO= new AddressDTO();
        addressDTO.setLine1(user.getAddress().getLine1());
        addressDTO.setLine2(user.getAddress().getLine2());
        addressDTO.setCity(user.getAddress().getCity());
        addressDTO.setPincode(user.getAddress().getPincode());
        addressDTO.setCountry(user.getAddress().getCountry());
        userDTO.setAddressDTO(addressDTO);
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());
        user.setPassword(userDTO.getPassword());
        Flat flat = flatRepository.findByNumber(userDTO.getFlat());
        if(flat == null && userDTO.getFlat() != null){
            flat = new Flat();
            //flat.setUser(user);
            flat.setNumber(userDTO.getFlat());
            flat.setDateCreated(OffsetDateTime.now());
            flat.setLastUpdated(OffsetDateTime.now());
            flatRepository.save(flat);
        }
        user.setFlat(flat);
        Address address= new Address();
        address.setLine1(userDTO.getAddressDTO().getLine1());
        address.setLine2(userDTO.getAddressDTO().getLine2());
        address.setCity(userDTO.getAddressDTO().getCity());
        address.setPincode(userDTO.getAddressDTO().getPincode());
        address.setCountry(userDTO.getAddressDTO().getCountry());
        user.setAddress(address);
        addressRepository.save(address);
        userRepository.save(user);
        if(flat.getUser()==null){
            flat.setUser(user);
            flatRepository.save(flat);
        }
        return user;
    }

    public boolean emailExists(final String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user= userRepository.findByEmail(username);
        if(user==null){
            throw new UsernameNotFoundException("User Not Found!!!!");
        }
        return user;
    }
}
