package com.example.helloworld.resources;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.client.Entity;

import no.storebrand.repository.hashmap.HashMapRepository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.example.helloworld.core.Meetup;

public class MeetupResourceTest {

	private static HashMapRepository repository = new HashMapRepository();

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(new MeetupResource(repository)).build();

	@Before
	public void setup() {
		repository.deleteAll(Meetup.class);
	}

	@Test
	public void should_create_meetup() throws Exception {
		Entity<Meetup> entity = Entity.json(new Meetup().setDateTime(new DateTime()));
		resources.client().target("/meetups").request().post(entity);

		assertThat(repository.findAll(Meetup.class)).hasSize(1);
	}

	@Test
	public void should_get_meetup() {
		Meetup meetupInRepository = new Meetup().setDateTime(new DateTime()).setId(1L);
		repository.save(meetupInRepository);

		Meetup meetup = resources.client().target("/meetups/1").request().get(Meetup.class);

		assertThat(meetup).isEqualTo(meetupInRepository);
	}

	@Test
	public void should_return_null_if_no_meetup_is_found() throws Exception {
		Meetup meetup = resources.client().target("/meetups/1").request().get(Meetup.class);
		assertThat(meetup).isNull();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should_get_list_of_meetups() throws Exception {
		Meetup meetupInRepository2 = new Meetup().setDateTime(new DateTime());
		repository.save(new Meetup().setDateTime(new DateTime()));
		repository.save(meetupInRepository2);

		List<Meetup> meetups = resources.client().target("/meetups").request().get(List.class);

		assertThat(meetups).hasSize(2);
	}
}
