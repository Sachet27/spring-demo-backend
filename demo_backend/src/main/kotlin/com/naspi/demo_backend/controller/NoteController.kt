package com.naspi.demo_backend.controller

import com.naspi.demo_backend.database.model.Note
import com.naspi.demo_backend.database.repository.NoteRepository
import com.naspi.demo_backend.mappers.toNote
import com.naspi.demo_backend.mappers.toNoteResponse
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
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
    private val repository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long,
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdDateTime: Instant,
    )

    @PostMapping
    fun saveNote(
        @RequestBody note: NoteRequest,
    ): NoteResponse{
        val ownerId= SecurityContextHolder.getContext().authentication.principal as String
        println(SecurityContextHolder.getContext().authentication.name)
        val uploadedNote= repository.save(
            Note(
                id = note.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = note.title,
                content = note.content,
                color = note.color,
                createdDateTime = Instant.now(),
                ownerId = ObjectId(ownerId) // gets anonymousUser instead of 67fb8648a3d9193e1f5676c4 causing 24 character error
            )
        )
        return uploadedNote.toNoteResponse()
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse>{
        val ownerId= SecurityContextHolder.getContext().authentication.principal as String
        println(ObjectId(ownerId))
        return  repository.findByOwnerId(ObjectId(ownerId)).map{
            it.toNoteResponse()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable("id") id: String
    ){
        val note= repository.findById(ObjectId(id)).orElseThrow{
            IllegalArgumentException("Note not found")
        }
        val ownerId= SecurityContextHolder.getContext().authentication.principal as String
        if(note.ownerId.toHexString() == ownerId){
            repository.deleteById(ObjectId(id))
        }
    }
}