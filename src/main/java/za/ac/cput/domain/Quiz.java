package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // use jakarta.persistence.Id here, not Spring's

import java.util.Objects;

@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quizTitle;
    private String quizDescription;
    private String quizAuthor;
    private String quizCategory;
    private String quizContent; // renamed from quizQuiz

    public Quiz() {}

    // Used by the Builder
    private Quiz(Builder builder) {
        // Remove this line — builder.build() causes recursion and is unnecessary
        // this.id = builder.build().id;
        this.id = builder.id; // assign from builder's id (add id field in builder below)
        this.quizTitle = builder.quizTitle;
        this.quizDescription = builder.quizDescription;
        this.quizAuthor = builder.quizAuthor;
        this.quizCategory = builder.quizCategory;
        this.quizContent = builder.quizContent;
    }

    // Add getter for id
    public Long getId() {
        return id;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public String getQuizAuthor() {
        return quizAuthor;
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public String getQuizContent() {
        return quizContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return Objects.equals(id, quiz.id) && // include id in equals/hashCode
                Objects.equals(quizTitle, quiz.quizTitle) &&
                Objects.equals(quizDescription, quiz.quizDescription) &&
                Objects.equals(quizAuthor, quiz.quizAuthor) &&
                Objects.equals(quizCategory, quiz.quizCategory) &&
                Objects.equals(quizContent, quiz.quizContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quizTitle, quizDescription, quizAuthor, quizCategory, quizContent);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", quizTitle='" + quizTitle + '\'' +
                ", quizDescription='" + quizDescription + '\'' +
                ", quizAuthor='" + quizAuthor + '\'' +
                ", quizCategory='" + quizCategory + '\'' +
                ", quizContent='" + quizContent + '\'' +
                '}';
    }

    public static class Builder {
        private Long id;  // add id here
        private String quizTitle;
        private String quizDescription;
        private String quizAuthor;
        private String quizCategory;
        private String quizContent;

        // Add setter for id in builder
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setQuizTitle(String quizTitle) {
            this.quizTitle = quizTitle;
            return this;
        }

        public Builder setQuizDescription(String quizDescription) {
            this.quizDescription = quizDescription;
            return this;
        }

        public Builder setQuizAuthor(String quizAuthor) {
            this.quizAuthor = quizAuthor;
            return this;
        }

        public Builder setQuizCategory(String quizCategory) {
            this.quizCategory = quizCategory;
            return this;
        }

        public Builder setQuizContent(String quizContent) {
            this.quizContent = quizContent;
            return this;
        }

        public Builder copy(Quiz quiz) {
            this.id = quiz.id; // copy id too
            this.quizTitle = quiz.quizTitle;
            this.quizDescription = quiz.quizDescription;
            this.quizAuthor = quiz.quizAuthor;
            this.quizCategory = quiz.quizCategory;
            this.quizContent = quiz.quizContent;
            return this;
        }

        public Quiz build() {
            return new Quiz(this);
        }
    }
}
