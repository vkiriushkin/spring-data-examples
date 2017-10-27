/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.jdbc.basics.singledomainclass;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.mapping.event.JdbcEvent;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import example.springdata.jdbc.basics.domain.Category;

/**
 * Demonstrates the most simple usage of Spring Data JDBC
 * @author Jens Schauder
 */
@EnableJdbcRepositories
public class AggregateConsistingOfSingleDomainClassApp implements CommandLineRunner {

	@Autowired
	private CategoryRepository repository;

	@Override
	public void run(String... args) throws Exception {
		simpleCrud();
	}

	public void simpleCrud() {

		// create some categories
		Category cars = new Category(null);
		cars.setName("Cars");
		cars.setDescription(null);
		cars.setCreated(LocalDate.now());

		Category buildings = new Category(null);
		buildings.setName("buildings");

		// save categories
		repository.saveAll(asList(cars, buildings));

		// accidental side effect
		System.out.println("== cars and buildings saved");
		System.out.println("'buildings' got an id " + buildings.getId());
		assertThat(buildings.getId()).isNotNull();

		repository.findAll().forEach(System.out::println);

		// update one
		buildings.setDescription("Famous and impressive buildings incl. the bike shed.");
		repository.save(buildings);

		System.out.println("== buildings has a description");
		repository.findAll().forEach(System.out::println);

		// delete stuff again
		repository.delete(cars);

		System.out.println("== no more cars");
		repository.findAll().forEach(System.out::println);
	}

	public static void main(String[] args) {
		SpringApplication.run(AggregateConsistingOfSingleDomainClassApp.class, args);
	}

	@Bean
	DataSource dataSource() {
		System.out.println("I'm inn *******************************************");
		return new EmbeddedDatabaseBuilder() //
				.generateUniqueName(true) //
				.setType(EmbeddedDatabaseType.HSQL) //
				.setScriptEncoding("UTF-8") //
				.ignoreFailedDrops(true) //
				.addScript("create.sql") //
				.build();
	}

	@Bean
	public ApplicationListener<?> loggingListener() {

		return (ApplicationListener<ApplicationEvent>) event -> {
			if (event instanceof JdbcEvent) {
				System.out.println("received an event: " + event);
			}
		};
	}
}
