package br.com.itstoony.attornatus.dto;

import java.time.LocalDate;

public record UpdatingPersonRecord(String name, LocalDate birthDay) { }



/*

@Test
    public void testUpdatePerson() {
        // Arrange
        Person person = new Person(1L, "John Doe", "12345678901", LocalDate.of(1980, 1, 1), null);
        UpdatingPersonRecord update = new UpdatingPersonRecord("Jane Doe", LocalDate.of(1985, 2, 2));
        when(personRepository.existsByCpf(person.getCpf())).thenReturn(true);
        when(personRepository.save(person)).thenReturn(person);

        // Act
        Person updatedPerson = personService.update(person, update);

        // Assert
        assertThat(updatedPerson.getName()).isEqualTo(update.name());
        assertThat(updatedPerson.getBirthDay()).isEqualTo(update.birthDay());
        verify(personRepository, times(1)).save(person);
    }

 // scenery
        Long id = 1L;
        UpdatingPersonRecord dto = createUpdatingPersonDTO();

        Person updatingPerson = createPerson();

        Person updatedPerson = Person.builder()
                .id(id)
                .name(dto.name())
                .birthDay(dto.birthDay())
                .cpf(updatingPerson.getCpf())
                .addressSet(updatingPerson.getAddressSet())
                .build();

        when(personRepository.existsByCpf(updatingPerson.getCpf())).thenReturn(true);
        when(personRepository.save(Mockito.any(Person.class))).thenReturn(Mockito.any(Person.class));

        // execution
        Person result = personService.update(updatingPerson, dto);

        // validation
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(dto.name());
        assertThat(result.getCpf()).isEqualTo(updatingPerson.getCpf());
        assertThat(result.getBirthDay()).isEqualTo(dto.birthDay());
        assertThat(result.getAddressSet()).isEqualTo(updatingPerson.getAddressSet());


*/