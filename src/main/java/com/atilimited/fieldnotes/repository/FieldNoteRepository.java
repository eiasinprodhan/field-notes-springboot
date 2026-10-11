package com.atilimited.fieldnotes.repository;

import com.atilimited.fieldnotes.model.FieldNote;
import com.atilimited.fieldnotes.model.NoteStatus;
import com.atilimited.fieldnotes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface FieldNoteRepository extends JpaRepository<FieldNote, Long> {

@Query("select n from FieldNote n join fetch n.site s join fetch s.customer c " +
           "where c.user = :user and n.deleted = false order by n.dateTime desc")
    List<FieldNote> findAllByUser(@Param("user") User user);

@Query("select n from FieldNote n join fetch n.site s join fetch s.customer c " +
           "where c.user = :user and n.deleted = false " +
           "  and (:search is null or lower(n.title)    like lower(concat('%',:search,'%')) " +
           "   or lower(n.description) like lower(concat('%',:search,'%')) " +
           "   or lower(s.siteName)    like lower(concat('%',:search,'%')) " +
           "   or lower(c.name)        like lower(concat('%',:search,'%'))) " +
           "  and (:status is null or n.status = :status) " +
           "  and (:siteUuid is null or s.uuid = :siteUuid)")
    List<FieldNote> search(@Param("user") User user,
                           @Param("search") String search,
                           @Param("status") NoteStatus status,
                           @Param("siteUuid") String siteUuid);

    @Query("select n from FieldNote n join fetch n.site s join fetch s.customer c " +
           "where c.user = :user and n.uuid = :uuid and n.deleted = false")
    Optional<FieldNote> findByUserAndUuid(@Param("user") User user, @Param("uuid") String uuid);

@Query("select n from FieldNote n join fetch n.site s join fetch s.customer c " +
           "where c.user = :user and n.updatedAt > :since")
    List<FieldNote> findAllByUserSince(@Param("user") User user, @Param("since") Instant since);
}
