package com.oreilly.reactive.systems.in.java.chapter10.database;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Processing {

  @Incoming(value = "upload")
  @Outgoing(value = "database")
  public Person validate(Person person) {
    if (person.age <= 0) {
      throw new IllegalArgumentException("Invalid age");
    }

    person.name = capitalize(person.name);

    return person;
  }


  static String capitalize(String name) {
    char[] chars = name.toLowerCase().toCharArray();
    boolean found = false;
    for (int i = 0; i < chars.length; i++) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i]);
        found = true;
      } else if (Character.isWhitespace(chars[i])) {
        found = false;
      }
    }
    return String.valueOf(chars);
  }
}
