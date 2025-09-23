package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Quiz;
import za.ac.cput.factory.QuizFactory;
import za.ac.cput.repository.QuizRepository;
import za.ac.cput.service.QuizService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizRepository quizRepository;

    @Autowired
    public QuizController(QuizService quizService, QuizRepository quizRepository) {
        this.quizService = quizService;
        this.quizRepository = quizRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Quiz quizInput) {
        try {
            Quiz newQuiz = QuizFactory.buildQuiz(
                    quizInput.getQuizTitle(),
                    quizInput.getQuizDescription(),
                    quizInput.getQuizAuthor(),
                    quizInput.getQuizCategory(),
                    quizInput.getQuizContent()
            );

            if (newQuiz == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            Quiz saved = quizService.create(newQuiz);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Quiz> getAll() {
        return quizService.getAll();
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<String> read(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.read(id);
        return quiz.map(q -> ResponseEntity.ok(q.toString()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("There's no content"));
    }


    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody Quiz quiz) {
        Quiz updated = quizService.update(quiz);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found or update failed");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.ok("Quiz deleted successfully");
    }

    @GetMapping("/ping")
    public String ping() {
        return "Quiz backend is running!";
    }
}
