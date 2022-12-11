package cat.copernic.fpshare.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.fpshare.adapters.ForoAdapter
import cat.copernic.fpshare.adapters.MsgAdapter
import cat.copernic.fpshare.databinding.FragmentFpHiloBinding
import cat.copernic.fpshare.modelo.Cicle
import cat.copernic.fpshare.modelo.Foro
import cat.copernic.fpshare.modelo.Mensaje
import cat.copernic.fpshare.modelo.Modul
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase


class FPHilo : Fragment() {
    private var _binding: FragmentFpHiloBinding? = null
    private val binding get() = _binding!!
    private lateinit var botonSend: ImageButton
    private lateinit var inputMsg: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var textviewDescripcion : TextView
    private lateinit var textViewTitulo: TextView
    private lateinit var  textViewAutor: TextView
    private var bd = FirebaseFirestore.getInstance()
    private var user = Firebase.auth.currentUser

    private val args: FPHiloArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFpHiloBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        inicializadores()
        infoforo()
        llamarecycleviewmensajes()

        botonSend.setOnClickListener() {
            val texto = inputMsg.text.toString()

            if (campoVacio(texto)) {
                addMensaje(texto)
                llamarecycleviewmensajes()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun infoforo(){
        val idForo = args.idforo
        bd.collection("Foros").document(idForo).get().addOnSuccessListener { document ->
            textViewTitulo.setText(document["titulo"].toString())
            textViewAutor.setText(document["emailautor"].toString())
            textviewDescripcion.setText(document["descripcion"].toString())
        }
    }
    private fun llamarecycleviewmensajes(){
        val mensajesList = ArrayList<Mensaje>()
        val idForo = args.idforo

        bd.collection("Foros").document(idForo).collection("Mensajes").get().addOnSuccessListener { documents ->
            for (document in documents){
                if(document["idMensaje"].toString() != "0") {
                    val wallitem = document.toObject(Mensaje::class.java)
                    mensajesList.add(wallitem)
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = MsgAdapter(mensajesList)
        }
    }
    private fun inicializadores() {
        botonSend = binding.buttonSend
        inputMsg = binding.timInput
        recyclerView = binding.recyclerViewHilo
        textViewAutor = binding.autorForo
        textViewTitulo = binding.tituloForo
        textviewDescripcion = binding.descripcion

    }

    fun addMensaje(mensaje: String) {
        val idForo = args.idforo
        val email = user?.email.toString()
        bd.collection("Foros").document(idForo).collection("Mensajes").orderBy("idMensaje", Query.Direction.DESCENDING).limit(1).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val wallitem = document.toObject(Mensaje::class.java)
                    wallitem.idMensaje = document["idMensaje"].toString()
                    var idtxt = wallitem.idMensaje
                    var idint = idtxt.toInt()
                    idint += 1
                    idtxt = idint.toString()
                    val nuevomensaje = Mensaje(idtxt, email, mensaje)

                bd.collection("Foros").document(idForo)
                    .collection("Mensajes").document(idtxt).set(nuevomensaje)
                }
            }
    }
    fun campoVacio(mensaje: String): Boolean {
        return mensaje.isNotEmpty() && mensaje.isNotBlank()
    }

}