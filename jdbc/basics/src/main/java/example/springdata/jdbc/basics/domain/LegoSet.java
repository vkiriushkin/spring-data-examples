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

import java.time.Period;
import java.time.temporal.ChronoUnit;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.Data;

/**
 * A Lego Set consisting of multiple Blocks and a manual
 *
 * @author Jens Schauder
 */
@Data
@AccessType(Type.PROPERTY)
public class LegoSet {

	@Id
	private int id;

	private String name;

	@Transient
	private Period minimumAge;
	@Transient
	private Period maximumAge;

	private Manual manual; // one-to-one relationship

	// private Theme theme;
	// private Category;

	//private final Map<Brick, Integer> bricks = new HashMap<>();

	// conversion for custom types currently has to be done through getters/setter + marking the underlying property with @Transient.
	public int getIntMinimumAge() {
		return toInt(this.minimumAge);
	}

	public void setIntMinimumAge(int years) {
		minimumAge = toPeriod(years);
	}

	public int getIntMaximumAge() {
		return toInt(this.maximumAge);
	}

	public void setIntMaximumAge(int years) {
		maximumAge = toPeriod(years);
	}


	private static int toInt(Period period) {
		return (int) (period == null ? 0 : period.get(ChronoUnit.YEARS));
	}

	private static Period toPeriod(int years) {
		return Period.ofYears(years);
	}
}
