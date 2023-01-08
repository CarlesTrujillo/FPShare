package cat.copernic.fpshare.ui.fragments

import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.fpshare.adapters.ModulAdminAdapter
import cat.copernic.fpshare.adapters.UfAdminAdapter
import cat.copernic.fpshare.databinding.FragmentNuevaPublicacionBinding
import cat.copernic.fpshare.modelo.Modul
import cat.copernic.fpshare.modelo.Publicacion
import cat.copernic.fpshare.modelo.Uf
import cat.copernic.fpshare.modelo.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class NuevaPublicacion : Fragment() {

    private var _binding: FragmentNuevaPublicacionBinding? = null
    private val binding get() = _binding!!
    private var bd = FirebaseFirestore.getInstance()
    private lateinit var titulo: EditText
    private lateinit var descripcion: TextInputEditText
    private lateinit var enlace: TextInputEditText
    private var user = Firebase.auth.currentUser
    private lateinit var botonPublicar: Button
    private lateinit var botonPdf: Button
    private lateinit var idModulo: Spinner
    private lateinit var idUf: Spinner
    private lateinit var usuario: User
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var CODIGO_PERMISOS_WR = 2108

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNuevaPublicacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        botonPublicar = binding.btnPublish
        titulo = binding.textPost
        descripcion = binding.textDescription
        enlace = binding.textLink
        idModulo = binding.spinnerModulesNewPost
        idUf = binding.spinnerUfsNewPost
        botonPdf = binding.btnPdf

        botonPublicar.setOnClickListener {
            llegirDades()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun llegirDades() {
        var publi = Publicacion()
        var usuario = User()
        val correo = user?.email.toString()
        /**
         * Leemos los datos del usuario actual.
         * NOTA: Si queremos tratar con datos de dos publicaciones, debe de ser en la misma
         * snapshot. No podemos tratar con datos que esten fuera de la snapshot, porque kotlin
         * no puede guardarlos fuera.
         */
        bd.collection("Usuarios").document(correo).get().addOnSuccessListener { doc ->
            //val usuario = doc.toObject(User::class.java)
            usuario.nombre = doc["nombre"].toString()
            usuario.email = doc["email"].toString()
            usuario.telefono = doc["telefono"].toString()
            usuario.instituto = doc["instituto"].toString()
            usuario.apellidos = doc["apellidos"].toString()
            usuario.imgPerfil = doc["imgPerfil"].toString()
            usuario.esAdmin = doc["esAdmin"] as Boolean


            /**
             * Creamos una publicacion y utilizamos los datos del usuario para generar la publicacion.
             */
            publi.id = "a"
            publi.imgPubli = usuario.email
            publi.perfil = usuario.nombre + " " + usuario.apellidos
            publi.titulo = titulo.text.toString()
            publi.descripcion = descripcion.text.toString()
            publi.checked = ""
            if (binding.optionDam.isChecked) {
                publi.checked = "DAM"
            } else if (binding.optionDaw.isChecked) {
                publi.checked = "DAW"
            } else if (binding.optionSmix.isChecked) {
                publi.checked = "SMIR"
            } else if (binding.optionAsix.isChecked) {
                publi.checked = "ASIR"
            }
            publi.enlace = enlace.text.toString()
            /**
             * Si la ID no esta vacia, añadiremos la publicacion en el Storage.
             */
            if (publi.id.isNotEmpty() && publi.id.isNotBlank()) {
                //anadirPublicacion(publi.checked, idModulo.text.toString(), idUf.text.toString(), publi)
            }
        }

    }

    private fun anadirPublicacion(checked: String, idModulo: String, idUf: String, publi: Publicacion) {
        val appContext = context
        /***
         * En añadir publicacion se define la ruta donde se guardara la publicacion.
         * La variable checked sera la id del Ciclo, el idModulo y idUf lo escribimos
         * en los EditText abajo.
         */
         bd.collection("Ciclos").document(checked)
             .collection("Modulos").document(idModulo)
             .collection("UFs").document(idUf)
             .collection("Publicaciones").add(publi)
            .addOnSuccessListener { //S'ha afegit el departament...
                Toast.makeText(appContext,"Documento añadido", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{ //No s'ha afegit el departament...
                Toast.makeText(appContext,"Documento no añadido", Toast.LENGTH_LONG).show()
            }
    }
}
