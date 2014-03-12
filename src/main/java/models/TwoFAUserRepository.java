package models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFAUserRepository extends JpaRepository<TwoFAUser, String> {

    TwoFAUser findByUsername(String username);
}