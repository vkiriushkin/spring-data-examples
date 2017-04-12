package example.springdata.jdbc.basics;

import static java.util.Arrays.*;

import java.time.LocalDate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import example.springdata.jdbc.basics.config.TestContextConfiguration;
import example.springdata.jdbc.basics.domain.AgeGroup;
import example.springdata.jdbc.basics.domain.Category;
import example.springdata.jdbc.basics.domain.CategoryRepository;

/**
 * @author Jens Schauder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public class SimpleCrudTest {

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


		// load categories
		repository.saveAll(asList(cars, buildings));

		// accidental side effect
		System.out.println("== cars and buildings");
		System.out.println("'buildings' got an id " + buildings.getId());

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