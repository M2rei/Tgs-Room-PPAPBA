package com.example.tgs_room_ppapba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import android.view.View
import android.widget.ArrayAdapter
import com.example.tgs_room_ppapba.database.Note
import com.example.tgs_room_ppapba.database.NoteDao
import com.example.tgs_room_ppapba.database.NoteRoomDatabase
import com.example.tgs_room_ppapba.databinding.ActivityAddNoteBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var NotesDao: NoteDao
    private lateinit var excutorService: ExecutorService
    private var updateId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        excutorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        NotesDao = db!!.noteDao()!!

        val intentData = intent
        if (intentData != null && intentData.hasExtra("UPDATE_ID")) {
            updateId = intentData.getIntExtra("UPDATE_ID", 0)
            val title = intentData.getStringExtra("TITLE")
            val description = intentData.getStringExtra("DESCRIPTION")
            val date = intentData.getStringExtra("DATE")

            // Isi form dengan data yang diambil dari Intent
            binding.txtTitle.setText(title)
            binding.txtDesc.setText(description)
            binding.txtDate.setText(date)
        }
        with(binding){
            btnnn.setOnClickListener(View.OnClickListener {
                insert(
                    Note(
                        title =  txtTitle.text.toString(),
                        description = txtDesc.text.toString(),
                        date = txtDate.text.toString()
                    )
                )
                setEmptyField()
                backToMainActivity()
            })

            btnUpdate.setOnClickListener {
                val updatedTitle = txtTitle.text.toString()
                val updatedDesc = txtDesc.text.toString()
                val updatedDate = txtDate.text.toString()
                update(
                    Note(
                        id = updateId,
                        title = updatedTitle,
                        description = updatedDesc,
                        date = updatedDate
                    )
                )
                updateId = 0
                setEmptyField()
                backToMainActivity()

            }


        }

    }
    private fun insert(note: Note) {
        excutorService.execute { NotesDao.insert(note) }
    }

    private fun delete(note: Note) {
        excutorService.execute { NotesDao.delete(note) }
    }

    private fun update(note: Note) {
        excutorService.execute { NotesDao.update(note) }
    }
    private fun setEmptyField() {
        with(binding) {
            txtTitle.setText("")
            txtDesc.setText("")
            txtDate.setText("")
        }
    }
    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}