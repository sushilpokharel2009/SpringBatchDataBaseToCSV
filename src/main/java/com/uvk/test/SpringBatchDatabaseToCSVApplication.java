package com.uvk.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uvk.test.conf.BatchConfiguration;

//@SpringBootApplication
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,BatchAutoConfiguration.class})
public class SpringBatchDatabaseToCSVApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SpringBatchDatabaseToCSVApplication app = new SpringBatchDatabaseToCSVApplication();
		app.run();
		
		
	}
	
	private void run() {
		//LogManager.getLogManager().getLogger("org.springframework.jdbc.datasource.DriverManagerDataSource").setLevel(java.util.logging.Level.ALL);;

//		String[] springConfig = { "jobs/job-extract-items.xml" };
//		ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
		
		ApplicationContext context = new AnnotationConfigApplicationContext(BatchConfiguration.class);

		
		JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
		Job job = (Job) context.getBean("exportUserJob");
		
		JobParameters params = new JobParametersBuilder().toJobParameters();
		
		try {
			jobLauncher.run(job, params);
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
