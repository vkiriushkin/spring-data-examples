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
package example.springdata.jdbc.basics.config;

import javax.sql.DataSource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.mapping.event.BeforeInsert;
import org.springframework.data.jdbc.mapping.event.JdbcEvent;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import example.springdata.jdbc.basics.domain.Category;

/**
 * @author Jens Schauder
 */
@Configuration
@EnableJdbcRepositories(basePackages="example.springdata.jdbc.basics")
public class TestContextConfiguration {

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

	public ApplicationListener<?> loggingListener(){
		return (ApplicationListener<JdbcEvent>) jdbcEvent -> System.out.println("received an event: " + jdbcEvent);
	}

	public ApplicationListener<?> idGenerator(){
		return (ApplicationListener<BeforeInsert>) jdbcEvent -> ((Category)jdbcEvent.getEntity()).timeStamp();
	}
}
