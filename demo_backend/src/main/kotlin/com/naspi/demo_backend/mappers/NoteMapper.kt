package com.naspi.demo_backend.mappers

import com.naspi.demo_backend.controller.NoteController
import com.naspi.demo_backend.controller.NoteController.NoteResponse
import com.naspi.demo_backend.database.model.Note
import org.bson.types.ObjectId
import java.time.Instant

fun Note.toNoteResponse(): NoteResponse {
    return NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        color = color,
        createdDateTime = createdDateTime
    )
}

fun NoteController.NoteRequest.toNote(): Note{
    return Note(
            id = id?.let {
                ObjectId(it)
            }?: ObjectId.get(),
            title = title,
            content = content,
            color = color,
            ownerId = ObjectId(),
            createdDateTime = Instant.now()
    )
}