package com.naspi.demo_backend.database.repository

import com.naspi.demo_backend.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId>{
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}

