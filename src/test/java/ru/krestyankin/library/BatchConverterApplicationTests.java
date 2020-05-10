package ru.krestyankin.library;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.krestyankin.library.repositories.AuthorRepository;
import ru.krestyankin.library.repositories.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.krestyankin.library.config.JobConfig.*;

@SpringBootTest
@SpringBatchTest
class BatchConverterApplicationTests {
	private static final long EXPECTED_BOOKS_COUNT = 3;
	private static final long EXPECTED_AUTHORS_COUNT = 4;
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	@Test
	void testJob() throws Exception {
		Job job = jobLauncherTestUtils.getJob();
		assertThat(job).isNotNull()
				.extracting(Job::getName)
				.isEqualTo(CONVERT_LIBRARY_JOB_NAME);

		JobParameters parameters = new JobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

		assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
		assertThat(bookRepository.count()).isEqualTo(EXPECTED_BOOKS_COUNT);
		assertThat(authorRepository.count()).isEqualTo(EXPECTED_AUTHORS_COUNT);

	}
}
