package com.atilimited.fieldnotes.repository;

import com.atilimited.fieldnotes.model.Site;
import com.atilimited.fieldnotes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

@Query("select s from Site s join fetch s.customer c " +
           "where c.user = :user and s.deleted = false order by s.siteName")
    List<Site> findAllByUser(@Param("user") User user);

    @Query("select s from Site s join fetch s.customer c " +
           "where c.user = :user and s.uuid = :uuid and s.deleted = false")
    Optional<Site> findByUserAndUuid(@Param("user") User user, @Param("uuid") String uuid);

@Query("select s from Site s join fetch s.customer c " +
           "where c.user = :user and s.updatedAt > :since")
    List<Site> findAllByUserSince(@Param("user") User user, @Param("since") Instant since);
}
