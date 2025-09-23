package za.ac.cput.service;

import za.ac.cput.domain.Quiz;

import java.util.List;
import java.util.Optional;

public interface IQuizService {
    Quiz create(Quiz quiz);
    Optional<Quiz> read(Long id);
    Quiz update(Quiz course);
    void delete(Long id);
    List<Quiz> getAll();
}
