package za.ac.cput.factory;

import za.ac.cput.domain.Quiz;
import za.ac.cput.util.Helper;
public class QuizFactory {
    public static Quiz buildQuiz(String quizTitle, String quizDescription, String quizAuthor, String quizCategory, String quizContent) {
        if (Helper.isNullOrEmpty(quizTitle) ||
                Helper.isNullOrEmpty(quizDescription) ||
                Helper.isNullOrEmpty(quizAuthor) ||
                Helper.isNullOrEmpty(quizCategory) ||
                Helper.isNullOrEmpty(quizContent)) {
            return null;
        }

        return new Quiz.Builder()
                .setQuizTitle(quizTitle)
                .setQuizDescription(quizDescription)
                .setQuizAuthor(quizAuthor)
                .setQuizCategory(quizCategory)
                .setQuizContent(quizContent)
                .build();
    }
}