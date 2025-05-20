package com.example.demo;

import com.example.demo.entity.Answer;
import com.example.demo.entity.Quiz;
import com.example.demo.respository.AnswerRepository;
import com.example.demo.respository.QuizRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
@Rollback(false)
class DemoApplicationTests {

	@Autowired
	EntityManagerFactory emf;

	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private AnswerRepository answerRepository;

	@Test
	void contextLoads() {

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Quiz quiz = new Quiz();
			quiz.setId(1L);
			quiz.setQuestion("강호동이 씨름 천하장사를 처음 차지한 해는?");
			em.persist(quiz);

			Answer answer1 = new Answer();
			answer1.setContent("1989년");
			answer1.setQuiz(quiz);
			answer1.setIsCorrect(0);
			em.persist(answer1);

			Answer answer2 = new Answer();
			answer2.setContent("1992년");
			answer2.setQuiz(quiz);
			answer1.setIsCorrect(1);
			em.persist(answer2);

			Answer answer3 = new Answer();
			answer3.setContent("1995년");
			answer3.setQuiz(quiz);
			answer1.setIsCorrect(0);
			em.persist(answer3);

			Answer answer4 = new Answer();
			answer4.setContent("1998년");
			answer4.setQuiz(quiz);
			answer1.setIsCorrect(0);
			em.persist(answer4);

			quizRepository.save(quiz);
			answerRepository.save(answer1);
			answerRepository.save(answer2);
			answerRepository.save(answer3);
			answerRepository.save(answer4);
			em.flush();
			em.clear();
			tx.commit();
		}
		catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		emf.close();
	}

}
