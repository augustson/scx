package com.example.helloworld;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.Assertions.assertThat;
import static org.joda.time.DateTimeZone.forOffsetHours;
import io.dropwizard.jackson.Jackson;

import org.joda.time.DateTime;
import org.junit.Test;

import com.example.helloworld.core.Meetup;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MeetupTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void should_serialize_to_JSON() throws Exception {
		Meetup meetup = new Meetup().setDateTime(new DateTime(2015, 4, 20, 14, 30, forOffsetHours(1)))
				.setSubject("Morgenmøte").setLocation("Kantina");
		String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/meetup.json"), Meetup.class));

		assertThat(MAPPER.writeValueAsString(meetup)).isEqualTo(expected);
	}

}
