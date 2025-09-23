package za.ac.cput.repository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
