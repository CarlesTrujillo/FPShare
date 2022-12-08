package cat.copernic.fpshare.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.fpshare.adapters.UserAdapter
import cat.copernic.fpshare.modelo.Usuario
import cat.copernic.fpshare.databinding.FragmentListaUsuariosAdministracionBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaUsuariosAdministracion : Fragment(), UserAdapter.OnItemClickListener {
    private var _binding: FragmentListaUsuariosAdministracionBinding? = null
    private val binding get() = _binding!!
    private lateinit var botonRename : Button
    private lateinit var botonDelete : Button
    private lateinit var recyclerView : RecyclerView
    private var bd = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListaUsuariosAdministracionBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch{
            inicializar()
            withContext(Dispatchers.IO){
                llamarecycleview()
            }

            botonRename.setOnClickListener {
                val action =
                    ListaUsuariosAdministracionDirections.actionFragmentListaUsuariosAdministracionToRenameUser(idUsuario = "xg")
                view.findNavController().navigate(action)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun llamarecycleview(){
        val userList = ArrayList<Usuario>()
        val adapterUser = UserAdapter(userList, this)

        bd.collection("Usuarios").get().addOnSuccessListener {documents ->
            for (document in documents){
                val wallitem = document.toObject(Usuario::class.java)
                wallitem.email = document.id
                wallitem.nombre = document["nombre"].toString()
                wallitem.apellidos = document["apellidos"].toString()
                wallitem.telefono = document["telefono"].toString()
                wallitem.insituto = document["instituto"].toString()
                wallitem.esAdmin = document["esAdmin"] as Boolean

                userList.add(wallitem)
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapterUser
        }

        }

   private fun inicializar(){
       botonRename = binding.buttonRenameUser
       botonDelete = binding.buttonDeleteUser
       recyclerView = binding.recyclerView
   }


    override fun onItemClick(email: String) {
        val action = ListaUsuariosAdministracionDirections.actionListaUsuariosAdministracionToMostarInfoUsuario(email)
        view?.findNavController()?.navigate(action)
    }
}