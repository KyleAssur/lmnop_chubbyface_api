package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Quiz;
import za.ac.cput.repository.QuizRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService implements IQuizService {

    private final QuizRepository repository;

    @Autowired
    public QuizService(QuizRepository repository) {
        this.repository = repository;
    }

    @Override
    public Quiz create(Quiz quiz) {
        return repository.save(quiz);
    }

    @Override
    public Optional<Quiz> read(Long id) {
        return null;
    }

    @Override
    public Quiz update(Quiz course) {
        return null;
    }

    @Override
    public void delete(Long id) {
        //TODO
    }

    @Override
    public List<Quiz> getAll() {
        return repository.findAll();
    }
}
