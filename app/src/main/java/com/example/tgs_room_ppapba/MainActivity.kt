package com.example.tgs_room_ppapba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.tgs_room_ppapba.database.Note
import com.example.tgs_room_ppapba.database.NoteDao
import com.example.tgs_room_ppapba.database.NoteRoomDatabase
import com.example.tgs_room_ppapba.databinding.ActivityMainBinding
import java.nio.file.Files.delete
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var executorService: ExecutorService
    private lateinit var notesDao: NoteDao
    private var updateId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        notesDao = db!!.noteDao()!!



        binding.listView.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView.adapter.getItem(i) as Note
            updateId = item.id
            // Replace these placeholders with your actual variable references
            // txtTitle.setText(item.title)
            // txtDesc.setText(item.description)
            // txtDate.setText(item.date)
            val intentToUpdateNoteActivity = Intent(this@MainActivity, AddNoteActivity::class.java)

            // Menambahkan data dari item yang diklik ke Intent
            intentToUpdateNoteActivity.putExtra("UPDATE_ID", updateId)
            intentToUpdateNoteActivity.putExtra("TITLE", item.title)
            intentToUpdateNoteActivity.putExtra("DESCRIPTION", item.description)
            intentToUpdateNoteActivity.putExtra("DATE", item.date)

            // Memulai AddNoteActivity untuk proses pembaruan
            startActivity(intentToUpdateNoteActivity)
        }
        with(binding){
            fab.setOnClickListener{
                val  intentToAddNoteActivity = Intent(this@MainActivity, AddNoteActivity::class.java)
                startActivity(intentToAddNoteActivity)
            }
        }
    }
    private fun getAllNotes() {
        notesDao.allNotes.observe(this) { notes ->
            val adapter = object : ArrayAdapter<Note>(
                this,
                R.layout.item, // Replace with your custom layout resource ID
                notes
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val itemView = convertView ?: LayoutInflater.from(context)
                        .inflate(R.layout.item, parent, false)

                    val note = getItem(position)

                    // Bind data to TextViews in your custom layout
                    val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
                    val descriptionTextView: TextView =
                        itemView.findViewById(R.id.textViewDescription)
                    val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)

                    titleTextView.text = note?.title
                    descriptionTextView.text = note?.description
                    dateTextView.text = note?.date

                    return itemView
                }
            }

            binding.listView.adapter = adapter
        }
    }
    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

}