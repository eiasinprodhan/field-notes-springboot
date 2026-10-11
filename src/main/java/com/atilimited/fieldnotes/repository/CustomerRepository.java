package com.atilimited.fieldnotes.repository;

import com.atilimited.fieldnotes.model.Customer;
import com.atilimited.fieldnotes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

@Query("select c from Customer c where c.user = :user and c.deleted = false order by c.name")
    List<Customer> findAllByUser(@Param("user") User user);

@Query("select c from Customer c where c.user = :user and c.uuid = :uuid")
    Optional<Customer> findByUserAndUuid(@Param("user") User user, @Param("uuid") String uuid);

@Query("select c from Customer c where c.user = :user and c.updatedAt > :since")
    List<Customer> findAllByUserSince(@Param("user") User user, @Param("since") Instant since);
}
