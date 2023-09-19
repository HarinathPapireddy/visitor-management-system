package io.bootify.visitor_management_app.repos;

import io.bootify.visitor_management_app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    UserDetails findByEmail(String email);



}
