package com.uvk.test.conf;



import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.uvk.mapper.VendorRowMapper;
import com.uvk.model.Vendor;
import com.uvk.processor.VendorItemProcessor;

@Configuration
@EnableBatchProcessing
@ImportResource("classpath:/jobs/job-extract-items.xml")
public class BatchConfiguration {

 @Autowired
 public JobBuilderFactory jobBuilderFactory;
 
 @Autowired
 public StepBuilderFactory stepBuilderFactory;
 
 @Autowired
 public DataSource dataSource;
 
 @Autowired
 private FlatFileItemWriter flateFileItemWriter2;
 
 @Bean
 public DataSource dataSource() {
  final DriverManagerDataSource dataSource = new DriverManagerDataSource();
  dataSource.setDriverClassName("com.mysql.jdbc.Driver");
  dataSource.setUrl("jdbc:mysql://localhost:3306/shubhaDB");
  dataSource.setUsername("root");
  dataSource.setPassword("");
  
  return dataSource;
 }

 
 @Bean
 public JdbcCursorItemReader<Vendor> itemReader(){
  JdbcCursorItemReader<Vendor> vendor = new JdbcCursorItemReader<Vendor>();
  vendor.setDataSource(dataSource);
  vendor.setSql("SELECT accountNumber,routingNumber FROM Test1");
  vendor.setRowMapper(new VendorRowMapper());
  
  return vendor;
 }
 
// public class VendorRowMapper implements RowMapper<Vendor>{
//
////  @Override
//  public Vendor mapRow(ResultSet rs, int rowNum) throws SQLException {
//	  Vendor vendor = new Vendor();
//	  vendor.setBankAccountNumber(rs.getString("BankAccountNumber"));
//		vendor.setRoutingNumber(rs.getString("RoutingNumber"));
//   
//   return vendor;
//  }
//  
// }
 
 @Bean
 public VendorItemProcessor processor(){
  return new VendorItemProcessor();
 }
 
 
 
 
 
 
 
 /*@Bean
 public FlatFileItemWriter<Vendor> flatFileItemWriter(){
  FlatFileItemWriter<Vendor> writer = new FlatFileItemWriter<Vendor>();
  writer.setResource(new ClassPathResource("file:users.csv"));
  writer.setLineAggregator(new DelimitedLineAggregator<Vendor>() {{
   setDelimiter(",");
   setFieldExtractor(new BeanWrapperFieldExtractor<Vendor>() {{
    setNames(new String[] { "accountNumber","routingNumber" });
   }});
  }});
  
  return writer;
 }*/
 
 
 
 @Bean
 public Step step1() {
  return stepBuilderFactory.get("step1").<Vendor, Vendor> chunk(10)
    .reader(itemReader())
    .processor(processor())
    .writer(flateFileItemWriter2)
    .build();
 }
 
 @Bean
 public Job exportUserJob() {
  return jobBuilderFactory.get("exportUserJob")
    .incrementer(new RunIdIncrementer())
    .flow(step1())
    .end()
    .build();
  
  
 }
 
 @Bean
 public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
     SimpleJobLauncher launcher = new SimpleJobLauncher();
     launcher.setJobRepository(jobRepository);
     return launcher;
 }

 @Bean
 public ResourcelessTransactionManager transactionManager() {
     return new ResourcelessTransactionManager();
 }

 @Bean
 public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager transactionManager)
         throws Exception {
     MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(transactionManager);
     factory.afterPropertiesSet();
     return factory;
 }

 @Bean
 public JobRepository jobRepository(MapJobRepositoryFactoryBean repositoryFactory) throws Exception {
     return repositoryFactory.getObject();
 }
}