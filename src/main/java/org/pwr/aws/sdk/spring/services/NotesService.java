package org.pwr.aws.sdk.spring.services;

import org.pwr.aws.sdk.spring.models.Note;

import java.util.List;

public interface NotesService {
    Note getNoteByUuid(String uuid);
    void addNote(Note note);
    void removeNote(String uuid);
    void updateNote(String uuid, Note newNote);
    List<Note> getAll();
}
