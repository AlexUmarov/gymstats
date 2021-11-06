package ru.uao.gymstats.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.uao.gymstats.data.model.LoggedInUser
import java.io.IOException
import java.util.*
import ru.uao.gymstats.MainActivity

class LoginFireBaseSource: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val ADD_TASK_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //reload();
        }
    }

    private fun createUser(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this@LoginFireBaseSource, "Aвторизация успешна", Toast.LENGTH_SHORT).show()
                    auth.currentUser?.let { loadMainFrag(it.uid) }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginFireBaseSource, "Aвторизация провалена", Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    fun login(username: String, password: String) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            signIn(username, password)
        } else {
            createUser(username, password)
        }

    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@LoginFireBaseSource, "Aвторизация успешна", Toast.LENGTH_SHORT).show()
                auth.currentUser?.let { loadMainFrag(it.uid) }
            } else
                Toast.makeText(this@LoginFireBaseSource, "Aвторизация провалена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMainFrag(uid: String){
        val intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent, ADD_TASK_REQUEST)
    }

    fun logout() {
        // TODO: revoke authentication
    }
}