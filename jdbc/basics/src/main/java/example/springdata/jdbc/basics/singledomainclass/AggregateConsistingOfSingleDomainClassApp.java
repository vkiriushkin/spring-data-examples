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

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Arrays;

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
 *
 * @author Jens Schauder
 */
@EnableJdbcRepositories
public class AggregateConsistingOfSingleDomainClassApp implements CommandLineRunner {

	@Autowired
	private CategoryRepository repository;

	@Override
	public void run(String... args) throws Exception {

		System.out.println(Arrays.toString(args));

		if (args.length == 0) {
			automaticScript();
		}
	}

	public void automaticScript() {

		// create some categories
		Category cars = createCategory("Cars", "Anything that has approximately 4 wheels");
		Category buildings = createCategory("Buildings", null);

		// save categories
		repository.saveAll(asList(cars, buildings));

		listAll("`Cars` and `Buildings` got saved");

		// update one
		buildings.setDescription("Famous and impressive buildings incl. the bike shed.");
		repository.save(buildings);

		listAll("`Buildings` has a description");

		// delete stuff again
		repository.delete(cars);

		listAll("`Cars` is gone.");
	}

	private void listAll(String x) {

		System.out.println();
		System.out.println("==== " + x);
		repository.findAll().forEach(System.out::println);
		System.out.println();
	}

	private Category createCategory(String name, String description) {

		Category category = new Category(null);
		category.setName(name);
		category.setDescription(description);

		return category;
	}

	public static void main(String[] args) {
		SpringApplication.run(AggregateConsistingOfSingleDomainClassApp.class, args);
	}

	@Bean
	DataSource dataSource() {

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
