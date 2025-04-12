package com.naspi.demo_backend.controller

import com.naspi.demo_backend.database.model.Note
import com.naspi.demo_backend.database.repository.NoteRepository
import com.naspi.demo_backend.mappers.toNote
import com.naspi.demo_backend.mappers.toNoteResponse
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.awt.Color
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController(
    val repository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long,
    )

    data class NoteResponse(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long,
        val createdDateTime: Instant,
    )

    @PostMapping
    fun saveNote(
        @RequestBody note: NoteRequest,
    ): NoteResponse{
        val uploadedNote= repository.save(
            note.toNote()
        )
        return uploadedNote.toNoteResponse()
    }

    @GetMapping
    fun findByOwnerId(
       @RequestParam(required = true) ownerId: String
    ): List<NoteResponse>{
        return  repository.findByOwnerId(ObjectId(ownerId)).map{
            it.toNoteResponse()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: String
    ){
        repository.deleteById(ObjectId(id))
    }
}