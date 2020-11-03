package org.pwr.aws.sdk.spring.services;

import org.pwr.aws.sdk.spring.exceptions.ElementNotFoundException;
import org.pwr.aws.sdk.spring.models.Note;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotesDynamoDbService implements NotesService {
    private Region region = Region.US_EAST_1;
    private DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();
    private DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
    private DynamoDbTable<Note> noteTable = enhancedClient.table("Note", TableSchema.fromBean(Note.class));

    @Override
    public Note getNoteByUuid(String uuid) {
        Key key = Key.builder().partitionValue(uuid).build();
        Note note = noteTable.getItem(r -> r.key(key));
        if (note == null) throw new ElementNotFoundException();
        return note;
    }

    @Override
    public void addNote(Note note) {
        String uuid = UUID.randomUUID().toString();
        note.setUuid(uuid);
        saveNote(note);
    }

    @Override
    public void removeNote(String uuid) {
        Key key = Key.builder().partitionValue(uuid).build();
        Note note = noteTable.deleteItem(key);
        if (note == null) throw new ElementNotFoundException();
    }

    @Override
    public void updateNote(String uuid, Note newNote) {
        if (newNote.getUuid().equals(uuid)) {
            saveNote(newNote);
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        for (Note note : noteTable.scan().items()) {
            notes.add(note);
        }
        return notes;
    }

    private void saveNote(Note note) {
        noteTable.putItem(note);
    }
}
