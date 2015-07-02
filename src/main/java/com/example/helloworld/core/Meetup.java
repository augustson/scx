package com.example.helloworld.core;

import static org.joda.time.DateTimeZone.forID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meetup {

	@Id
	@GeneratedValue
	private Long id;

	private DateTime dateTime;

	private String subject;

	private String location;

	public Meetup setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
		return this;
	}

	@JsonProperty
	public DateTime getDateTime() {
		return dateTime;
	}

	public Meetup setId(Long id) {
		this.id = id;
		return this;
	}

	@JsonProperty
	public Long getId() {
		return id;
	}

	public Meetup setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	@JsonProperty
	public String getSubject() {
		return subject;
	}

	public Meetup setLocation(String location) {
		this.location = location;
		return this;
	}

	@JsonProperty
	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Meetup [id=" + id + ", dateTime=" + dateTime == null ? null : dateTime.withZone(forID("Europe/Oslo"))
				.toString() + ", subject=" + subject + ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meetup other = (Meetup) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

}
