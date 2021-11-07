package ru.uao.gymstats

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class EmailPasswordActivity: AppCompatActivity() {

    //private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val ADD_TASK_REQUEST = 1
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        //database = FirebaseDatabase.getInstance().reference


        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        println("myRef "+myRef)

        var etMail = findViewById<EditText>(R.id.etMail)
        var etPassword = findViewById<EditText>(R.id.etPassword)
        var btnLogin = findViewById<Button>(R.id.btLogin)
        var btnRegist = findViewById<Button>(R.id.btRegist)


        btnLogin.setOnClickListener{
            var email = etMail.text.toString()
            var password = etPassword.text.toString()
            if (validateCred(email,password)){
                signin(email,password)
            }else{
                Toast.makeText(
                    baseContext, "Укажите логин и пароль для входа",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        btnRegist.setOnClickListener {
            var email = etMail.text.toString()
            var password = etPassword.text.toString()
            if (validateCred(email,password)){
                createAccount(email,password)
            }else{
                Toast.makeText(
                    baseContext, "Укажите логин и пароль для регистрации",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // your code to validate the user_name and password combination
            // and verify the same

        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@EmailPasswordActivity, "Aвторизация успешна", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivityForResult(intent, ADD_TASK_REQUEST)
                } else
                    Toast.makeText(this@EmailPasswordActivity, "Aвторизация провалена", Toast.LENGTH_SHORT).show()
            }
    }

    /*private fun writeNewUser(userId: String?, name: String?, email: String?) {
        val user = User(name, email)
        database.child("users").child(userId!!).setValue(user)
    }*/

    private fun createAccount(email:String, password:String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    //writeNewUser(auth.uid, auth.currentUser!!.displayName, auth.currentUser!!.email)
                    Toast.makeText(
                        baseContext, "Регистрация успешна!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivityForResult(intent, ADD_TASK_REQUEST)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Регистрация провалена.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }

                // ...
            }
    }

    private fun validateCred(email:String, password:String): Boolean{
        if(email != "" && password != ""){
            if(password.length>=6){
                return true
            }else{
                Toast.makeText(
                    baseContext, "Пароль должен содержать не менее 6 символов.",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return false
    }
}