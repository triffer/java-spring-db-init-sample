package com.triffer.dbunit.repository.dbunit;

import com.triffer.dbunit.model.Person;
import com.triffer.dbunit.repository.PersonRepository;
import com.triffer.dbunit.repository.sql.RepositoryIntegrationSqlScriptInitTest;
import com.triffer.dbunit.testsupport.commands.CleanDb;
import com.triffer.dbunit.testsupport.commands.CreatePerson;
import com.triffer.dbunit.testsupport.commands.CreatePost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

public class PersonRepositoryDbunitScriptInitTest extends RepositoryIntegrationDbunitInitTest {

    @Autowired
    private PersonRepository sut;

    @Test
    void findAll() {
        // given
        CreatePerson.instance(testDatabase).withId(10L).withName("a new Person").execute();
        CreatePost.instance(testDatabase)
                .withId(2L)
                .withTitle("a title")
                .withPersonId(10L)
                .execute();

        // when
        List<Person> result = sut.findAll();

        // then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("a new Person", result.get(0).getName());
        assertFalse(result.get(0).getPostSet().isEmpty());
        assertEquals("a title", result.get(0).getPostSet().iterator().next().getTitle());
        assertEquals("default text", result.get(0).getPostSet().iterator().next().getText());
    }

    @Test
    void findAllByNameOrderByIdDesc_filtersByNameAndOrdersByIdDesc() {
        // given
        CreatePerson.instance(testDatabase).withId(10L).withName("Testperson").execute();
        CreatePerson.instance(testDatabase).withId(11L).withName("Testperson").execute();
        CreatePerson.instance(testDatabase).withId(12L).withName("Cool Testperson").execute();


        // when
        List<Person> result = sut.findAllByNameOrderByIdDesc("Testperson");

        // then
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Testperson", result.get(0).getName());
        assertEquals(11L, result.get(0).getId());
        assertEquals(10L, result.get(1).getId());
    }

    @Test
    void findAll_createPersonAddsDefaultValues() {
        // given
        CreatePerson.instance(testDatabase).execute();

        // when
        List<Person> result = sut.findAll();

        // then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("DefaultUser", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
    }
}