package at.qe.skeleton.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.qe.skeleton.model.Userx;
import at.qe.skeleton.model.UserxRole;
import java.util.Optional;

/**
 * Repository for managing {@link Userx} entities.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
public interface UserxRepository extends AbstractRepository<Userx, Long> {

    Optional<Userx> findFirstByUsername(String username);

    List<Userx> findByUsernameContaining(String username);

    @Query("SELECT u FROM Userx u WHERE CONCAT(u.firstName, ' ', u.lastName) = :wholeName")
    List<Userx> findByWholeNameConcat(@Param("wholeName") String wholeName);

    @Query("SELECT u FROM Userx u WHERE :role MEMBER OF u.roles")
    Page<Userx> findByRolesContaining(@Param("role") UserxRole role, Pageable pageable);

    boolean existsByUsername(String username);

    Page<Userx> findAll(Pageable pageable);

    Page<Userx> findByDeleted(boolean deleted, Pageable pageable);

    Page<Userx> findByRolesContainingAndDeleted(UserxRole role, boolean deleted, Pageable pageable);

    Optional<Userx> findById(Long id);

    @Query("""
    SELECT DISTINCT u
    FROM Userx u
    LEFT JOIN u.roles r
    WHERE u.deleted = :deleted
      AND (:roles IS NULL OR r IN :roles)
""")
    Page<Userx> findWithPaginationFilters(
            @Param("roles") List<UserxRole> roles,
            @Param("deleted") boolean deleted,
            Pageable pageable
    );
}

