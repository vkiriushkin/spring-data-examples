package example.springdata.jdbc.basics.singledomainclass;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import example.springdata.jdbc.basics.domain.Category;

/**
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public class AggregateConsistingOfSingleDomainClassTest {

	@Autowired
	CategoryRepository repository;

	@Test
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

}