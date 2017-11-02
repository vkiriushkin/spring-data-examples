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
package example.springdata.jdbc.basics;

import javax.sql.DataSource;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jdbc.mapping.event.BeforeSave;
import org.springframework.data.jdbc.mapping.model.ConversionCustomizer;
import org.springframework.data.jdbc.mapping.model.DefaultNamingStrategy;
import org.springframework.data.jdbc.mapping.model.JdbcPersistentProperty;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.lang.Nullable;

import example.springdata.jdbc.basics.domain.LegoSet;
import example.springdata.jdbc.basics.domain.LegoSetRepository;
import example.springdata.jdbc.basics.domain.Manual;

/**
 * Demonstrates non trivial usage of Spring Data JDBC especially handling of collections and references crossing aggregate boundaries.
 * It tries to showcase the following
 * <ul>
 * <li>Custom Names for columns and tables via NamingStrategy</li>
 * <li>Manual id generation</li>
 * <li>Custom conversions</li>
 * </ul>
 *
 * @author Jens Schauder
 */
@EnableJdbcRepositories
public class AggregatesApplication implements CommandLineRunner {

	private static final AtomicInteger id = new AtomicInteger(0);

	@Autowired
	private LegoSetRepository repository;

	@Override
	public void run(String... args) throws Exception {

		LegoSet smallCar = createLegoSet();
		smallCar.setManual(createManual("Just put all the pieces together in the right order"));

		repository.save(smallCar);
		Output.list(repository.findAll(), "Original LegoSet");

		smallCar.getManual().setText("Just make it so it looks like a car.");

		repository.save(smallCar);
		Output.list(repository.findAll(), "Updated");

		smallCar.setManual(createManual("One last attempt: Just build a car! Ok?"));

		repository.save(smallCar);
		Output.list(repository.findAll(), "Manual replaced");
	}

	private Manual createManual(String text) {
		Manual manual = new Manual();
		manual.setAuthor("Jens Schauder");
		manual.setText(text);
		return manual;
	}

	private LegoSet createLegoSet() {
		LegoSet smallCar = new LegoSet();
		smallCar.setName("Small Car 01");
		smallCar.setMinimumAge(Period.ofYears(5));
		smallCar.setMaximumAge(Period.ofYears(12));
		return smallCar;
	}

	public static void main(String[] args) {
		SpringApplication.run(AggregatesApplication.class, args);
	}

	@Bean
	DataSource dataSource() {

		return new EmbeddedDatabaseBuilder() //
				.generateUniqueName(true) //
				.setType(EmbeddedDatabaseType.HSQL) //
				.setScriptEncoding("UTF-8") //
				.ignoreFailedDrops(true) //
				.addScript("createLegoSet.sql") //
				.build();
	}

	@Bean
	public ApplicationListener<?> idSetting() {

		return (ApplicationListener<BeforeSave>) event -> {

			Object entity = event.getEntity();
			if (entity instanceof LegoSet) {
				LegoSet legoSet = (LegoSet) entity;
				if (legoSet.getId() == 0) {
					legoSet.setId(id.incrementAndGet());
				}

				Manual manual = legoSet.getManual();
				if (manual != null) {
					manual.setId((long) legoSet.getId());
				}
			}
		};
	}

	@Bean
	public NamingStrategy namingStrategy() {

		Map<String, String> tableAliases = new HashMap<String, String>();
		tableAliases.put("Manual", "Handbuch");

		Map<String, String> columnAliases = new HashMap<String, String>();
		columnAliases.put("LegoSet.intMaximumAge", "maxAge");
		columnAliases.put("LegoSet.intMinimumAge", "minAge");
		columnAliases.put("Handbuch.id", "Handbuch_id");

		Map<String, String> reverseColumnAliases = new HashMap<String, String>();
		reverseColumnAliases.put("LegoSet", "Handbuch_id");

		return new DefaultNamingStrategy() {

			@Override
			public String getColumnName(JdbcPersistentProperty property) {

				String defaultName = super.getColumnName(property);
				String key = getTableName(property.getOwner().getType()) + "." + defaultName;
				return columnAliases.getOrDefault(key, defaultName);
			}

			@Override
			public String getTableName(Class<?> type) {

				String defaultName = super.getTableName(type);
				return tableAliases.getOrDefault(defaultName, defaultName);
			}

			@Override
			public String getReverseColumnName(JdbcPersistentProperty property) {

				String defaultName = super.getReverseColumnName(property);
				return reverseColumnAliases.getOrDefault(defaultName, defaultName);
			}
		};
	}

	@Bean
	public ConversionCustomizer conversionCustomizer() {
		return conversions -> conversions.addConverter(new Converter<Clob, String>() {
			@Nullable
			@Override
			public String convert(Clob clob) {

				try {
					int length = Math.toIntExact(clob.length());
					if (length == 0) return "";

					return clob.getSubString(1, length);
				} catch (SQLException e) {
					throw new IllegalStateException("Failed to convert CLOB to String.", e);
				}
			}
		});
	}
}
