package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val cancelButton: TextView = findViewById(R.id.cancel_post_button)
        val postButton: TextView = findViewById(R.id.post_button)
        val postEditText: EditText = findViewById(R.id.post_edit_text)

        cancelButton.setOnClickListener {
            finish()
        }

        postButton.setOnClickListener {
            val postText = postEditText.text.toString()

            if (postText.isNotBlank()) {
                val resultIntent = Intent()
                resultIntent.putExtra("NEW_POST_TEXT", postText)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}