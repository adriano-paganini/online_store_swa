package at.qe.skeleton.tests;

import at.qe.skeleton.model.Userx;
import at.qe.skeleton.model.UserxRole;
import at.qe.skeleton.repositories.UserxRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@DataJpaTest
class UserxRepositorySortingTest {

    @Autowired
    private UserxRepository userRepository;

    @Test
    void findWithPaginationFiltersSortsByUsernameAscendingUsingSeedData() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(null, false, pageable);

        List<String> usernames = result.getContent().stream()
                .map(Userx::getUsername)
                .toList();

        Assertions.assertEquals(List.of("admin", "elvis", "user1", "user2"), usernames);
    }

    @Test
    void findWithPaginationFiltersSortsByUsernameDescendingUsingSeedData() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "username"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(null, false, pageable);

        List<String> usernames = result.getContent().stream()
                .map(Userx::getUsername)
                .toList();

        Assertions.assertEquals(List.of("user2", "user1", "elvis", "admin"), usernames);
    }

    @Test
    void findWithPaginationFiltersSortsByLastNameAscendingUsingSeedData() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "lastName"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(null, false, pageable);

        List<String> lastNames = result.getContent().stream()
                .map(Userx::getLastName)
                .toList();

        Assertions.assertEquals(List.of("Istrator", "Kaufgern", "Mustermann", "The King"), lastNames);
    }

    @Test
    void findWithPaginationFiltersSortsByLastNameDescendingUsingSeedData() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastName"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(null, false, pageable);

        List<String> lastNames = result.getContent().stream()
                .map(Userx::getLastName)
                .toList();

        Assertions.assertEquals(List.of("The King", "Mustermann", "Kaufgern", "Istrator"), lastNames);
    }

    @Test
    void findWithPaginationFiltersSortsByIdAscendingUsingSeedData() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(null, false, pageable);

        List<Long> ids = result.getContent().stream()
                .map(Userx::getId)
                .toList();

        Assertions.assertEquals(List.of(1000L, 2000L, 3000L, 4000L), ids);
    }

    @Test
    void findWithPaginationFiltersSortsByIdDescendingUsingSeedData() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(null, false, pageable);

        List<Long> ids = result.getContent().stream()
                .map(Userx::getId)
                .toList();

        Assertions.assertEquals(List.of(4000L, 3000L, 2000L, 1000L), ids);
    }

    @Test
    void findWithPaginationFiltersFiltersByAdminRole() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(List.of(UserxRole.ADMIN), false, pageable);

        List<String> usernames = result.getContent().stream()
                .map(Userx::getUsername)
                .toList();

        Assertions.assertEquals(List.of("admin", "elvis"), usernames);
    }

    @Test
    void findWithPaginationFiltersFiltersByManagerRole() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(List.of(UserxRole.MANAGER), false, pageable);

        List<String> usernames = result.getContent().stream()
                .map(Userx::getUsername)
                .toList();

        Assertions.assertEquals(List.of("user1"), usernames);
    }

    @Test
    void findWithPaginationFiltersFiltersByAdminOrManagerRole() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(List.of(UserxRole.ADMIN, UserxRole.MANAGER), false, pageable);

        List<String> usernames = result.getContent().stream()
                .map(Userx::getUsername)
                .toList();

        Assertions.assertEquals(List.of("admin", "elvis", "user1"), usernames);
    }

    @Test
    void findWithPaginationFiltersFiltersByCustomerRole() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));

        Page<Userx> result =
                userRepository.findWithPaginationFilters(List.of(UserxRole.CUSTOMER), false, pageable);

        List<String> usernames = result.getContent().stream()
                .map(Userx::getUsername)
                .toList();

        Assertions.assertEquals(List.of("admin", "elvis", "user1", "user2"), usernames);
    }
}
