package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Support;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
}
