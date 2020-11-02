package org.pwr.zrcaw_z4.services;

import org.pwr.zrcaw_z4.models.Note;

import java.util.List;

public interface NotesService {
    Note getNoteByUuid(String uuid);
    void addNote(Note note);
    void removeNote(String uuid);
    void updateNote(String uuid, Note newNote);
    List<Note> getAll();
}
