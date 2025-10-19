package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Quiz;
import za.ac.cput.factory.QuizFactory;
import za.ac.cput.service.QuizService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
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
    public ResponseEntity<Quiz> read(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.read(id);
        return quiz.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Quiz quiz) {
        try {
            Quiz updated = quizService.update(quiz);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found with id: " + quiz.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating quiz: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<Quiz> existingQuiz = quizService.read(id);
            if (existingQuiz.isPresent()) {
                quizService.delete(id);
                return ResponseEntity.ok("Quiz deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting quiz: " + e.getMessage());
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Quiz backend is running!";
    }
}