package nz.school.mrgs.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nz.school.mrgs.lostandfound.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // This variable will connect to the XML layout file.
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the standard code to link this Kotlin file to the 'activity_login.xml' layout.
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The screen is now showing the blank layout.
        // I can start adding your button listeners and other logic here later.
    }
}
