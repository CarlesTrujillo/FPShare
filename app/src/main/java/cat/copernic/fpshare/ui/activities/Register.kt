package cat.copernic.fpshare.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cat.copernic.fpshare.R
import cat.copernic.fpshare.databinding.ActivityRegistroBinding
import cat.copernic.fpshare.modelo.User
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var btnRegistrarse: Button
    private lateinit var InputNombre: EditText
    private lateinit var InputPassword: EditText
    private lateinit var InputMail: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegistroBinding
    private lateinit var volverIniciarSesion: TextView
    private var bd = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Inicializacion de componentes
        InputNombre = findViewById(R.id.InputNombre)
        InputPassword = findViewById(R.id.InputPassword)
        InputMail = findViewById(R.id.InputMail)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        volverIniciarSesion = findViewById(R.id.txtSign)


        //Inicializacion Firebase
        auth = Firebase.auth

        btnRegistrarse.setOnClickListener {
            val nombre = InputNombre.text.toString()
            val password = InputPassword.text.toString()
            val mail = InputMail.text.toString()

            if (!campoVacio(nombre, password, mail)) {
                Snackbar.make(
                    findViewById(R.id.registroLayout),
                    getString(R.string.errorCamposVacios),
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (nombreLargo(nombre)) {
                Snackbar.make(
                    findViewById(R.id.registroLayout),
                    getString(R.string.nombreInvalido),
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (!limiteCaracteres(password)) {
                Snackbar.make(
                    findViewById(R.id.registroLayout),
                    getString(R.string.errorContraseña),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {

                registrar(password, mail)

                val usuario = User(mail, nombre)
                anadirUsuario(usuario)
            }
        }

        volverIniciarSesion.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun anadirUsuario(usuario: User) {

        bd.collection("Usuarios").document(usuario.email).set(usuario)
    }

    private fun campoVacio(nombre: String, password: String, mail: String): Boolean {
        return nombre.isNotEmpty() && password.isNotEmpty() && mail.isNotEmpty()
                && nombre.isNotBlank() && password.isNotBlank() && mail.isNotBlank()
    }

    private fun limiteCaracteres(cadena: String): Boolean {
        return cadena.length > 6
    }

    private fun nombreLargo(cadena: String): Boolean {
        return cadena.length > 30
    }

    private fun registrar(password: String, mail: String) {
        auth.createUserWithEmailAndPassword(mail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    error()
                }
            }
    }

    private fun error() {
        Snackbar.make(
            findViewById(R.id.registroLayout),
            getString(R.string.errorEmail),
            BaseTransientBottomBar.LENGTH_SHORT
        ).show()
    }

}