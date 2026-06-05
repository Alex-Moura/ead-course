package br.com.devalex.course.exceptions;

public class ErrorMessages {

    private ErrorMessages() {}

    public static final String COURSE_NOT_FOUND = "Curso com id: %s não encontrado";
    public static final String MODULE_NOT_FOUND = "Módulo com id: %s não encontrado";
    public static final String LESSON_NOT_FOUND = "Lição com id: %s não encontrada";
    public static final String MODULE_NOT_IN_COURSE = "Módulo com id: %s não encontrado no curso: %s";
}
