
package org.example;

import org.example.domain.Nota;
import org.example.domain.Student;
import org.example.domain.Tema;
import org.example.repository.NotaXMLRepo;
import org.example.repository.StudentXMLRepo;
import org.example.repository.TemaXMLRepo;
import org.example.service.Service;
import org.example.validation.NotaValidator;
import org.example.validation.StudentValidator;
import org.example.validation.TemaValidator;
import org.example.validation.ValidationException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.internal.matchers.Not;

import java.time.LocalDate;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class MokitoTestClass {
    @Mock
    private StudentValidator studentValidator;

    @Mock
    private TemaValidator temaValidator;

    @Mock
    private NotaValidator notaValidator;

    @Mock
    private StudentXMLRepo studentXMLRepository;

    @Mock
    private TemaXMLRepo temaXMLRepository;

    @Mock
    private NotaXMLRepo notaXMLRepository;

    private Service service;

    @BeforeEach
    public void setup() {
        studentValidator = mock(StudentValidator.class);
        temaValidator = mock(TemaValidator.class);
        notaValidator = mock(NotaValidator.class);
        studentXMLRepository = mock(StudentXMLRepo.class);
        temaXMLRepository = mock(TemaXMLRepo.class);
        notaXMLRepository = mock(NotaXMLRepo.class);
        service = new Service(studentXMLRepository, studentValidator, temaXMLRepository, temaValidator, notaXMLRepository, notaValidator);
    }

    @AfterEach
    public void tearDown() {
        studentValidator = null;
        temaValidator = null;
        notaValidator = null;
        studentXMLRepository = null;
        temaXMLRepository = null;
        notaXMLRepository = null;
        service = null;
    }

    @Test
    public void addStudent_ValidData() {
        String studentId = "1001";
        String nume = "Bellingham";
        int grupa = 935;
        String email = "belingham@yahoo.com";

        Student student = new Student(studentId, nume, grupa, email);

        try {
           when(studentXMLRepository.save(student)).thenReturn(student);
           Student returnedStudent = service.addStudent(student);
        } catch (ValidationException ve) {
            ve.printStackTrace();
            assert false;
        }
    }

    @Test
    public void addStudent_Valid_addTema_Valid() {
        String studentId = "1001";
        String nume = "Bellingham";
        int grupa = 935;
        String email = "bellingham@yahoo.com";
        Student student = new Student(studentId, nume, grupa, email);

        String nrTema = "1";
        String descriere = "Good Homework";
        int deadline = 13;
        int primire = 2;

        Tema tema = new Tema(nrTema, descriere, deadline, primire);

        try {

            when(studentXMLRepository.save(student)).thenReturn(student);
            Student returnedStudent = service.addStudent(student);

            when(temaXMLRepository.save(tema)).thenReturn(tema);
            Tema returnedTema = service.addTema(tema);
        } catch (ValidationException ve) {
            ve.printStackTrace();
            assert false;
        }
    }

    @Test
    public void addStudent_Valid_addTema_Valid_addNota_Valid_ThrowsException() {
        String studentId = "1001";
        String nume = "Bellingham";
        int grupa = 935;
        String email = "Bellingham@yahoo.com";
        Student student = new Student(studentId, nume, grupa, email);

        String nrTema = "1";
        String descriere = "Good Homework";
        int deadline = 12;
        int primire = 2;
        Tema tema = new Tema(nrTema, descriere, deadline, primire);

        String notaId = "Grade01";
        double notaVal = 9.5;
        LocalDate date = LocalDate.of(2024, 4, 15);
        Nota nota = new Nota(notaId, studentId, nrTema, notaVal, date);

        try {
            doNothing().when(studentValidator).validate(student);
            when(studentXMLRepository.save(student)).thenReturn(null);

            doNothing().when(temaValidator).validate(tema);
            when(temaXMLRepository.save(tema)).thenReturn(null);

            when(studentXMLRepository.findOne(studentId)).thenReturn(student);
            when(temaXMLRepository.findOne(nrTema)).thenReturn(tema);

            Student returnedStudent = service.addStudent(student);
            Assertions.assertNull(returnedStudent);

            Tema returnedTema = service.addTema(tema);
            Assertions.assertNull(returnedTema);

            double returnedNota = service.addNota(nota, "feedback");
            Assertions.assertEquals(9.5, returnedNota);
            Assertions.assertEquals(9.5, nota.getNota());
        } catch (ValidationException ve) {
            ve.printStackTrace();
        }
    }
}
