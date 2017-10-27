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
package example.springdata.jdbc.basics.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

/**
 * Coarse classification for {@link LegoSet}s, like "Car", "Plane", "Building" and so on.
 *
 * @author Jens Schauder
 */
@Data
public class Category {

	@Id
	private final Long id;

	private String name;
	private String description;

	private LocalDateTime created = LocalDateTime.now();

	@Setter
	private long inserted;

	public void timeStamp() {

		if (inserted == 0) {
			inserted = System.currentTimeMillis();
		}
	}

	private AgeGroup ageGroup;

	// no Optional
	// expected to be fairly eazy to do with the existing conversions

	// no references
	// How to implement those without going to Vietnam?
	// My current idea:
	// 1. really simple implementation (removing all referencing row and recreating them)
	// 2. a little more advanced, like updating all existing, adding missing, removing non existent, with configurability for both approaches
	// 3. integration with third party libs (mybatis, jooq, querydsl) i.O. to allow the user to provide their own logic.
}
