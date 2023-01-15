package cat.copernic.fpshare.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.fpshare.R
import cat.copernic.fpshare.databinding.ItemPubliBinding
import cat.copernic.fpshare.modelo.Publicacion
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*
import androidx.lifecycle.lifecycleScope
import cat.copernic.fpshare.modelo.Cicle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class PubliAdapter(private val publicaciones: List<Publicacion>) : RecyclerView.Adapter<PubliAdapter
.PubliViewHolder>(), Filterable {
    private lateinit var contexto: Context
    var storage = FirebaseStorage.getInstance()
    /***
     * Cargamos en publiFilter la lista de publicaciones que entran por el Adapter.
     */
    var publiFilter: List<Publicacion> = publicaciones


    inner class PubliViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val viewB = ItemPubliBinding.bind(view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PubliViewHolder {
        contexto = viewGroup.context
        val view = LayoutInflater.from(contexto)
            .inflate(R.layout.item_publi, viewGroup, false)

        return PubliViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: PubliViewHolder, position: Int) {
        val publicacion = publiFilter.get(position)

        with(viewHolder) {
            /***
             * Ponemos los textos en cada recuadro del Item Publi.
             */
            viewB.txtProf.text = publicacion.perfil
            viewB.txtPubliTitle.text = publicacion.titulo
            viewB.txtDescr.text = publicacion.descripcion
            viewB.textLink.text = publicacion.enlace
            //viewB.progressBar1.setProgress(publicacion.rating1)
            /***
             * Inicializacion de barras de progreso
             *
             */
            val maxRating: Int = publicacion.rating1 + publicacion.rating2 + publicacion.rating3 + publicacion.rating4 + publicacion.rating5
            if(maxRating>0){
                viewB.progressBar1.setProgress(publicacion.rating1 / maxRating*100)
                viewB.progressBar2.setProgress(publicacion.rating2 / maxRating*100)
                viewB.progressBar3.setProgress(publicacion.rating3 / maxRating*100)
                viewB.progressBar4.setProgress(publicacion.rating4 / maxRating*100)
                viewB.progressBar5.setProgress(publicacion.rating5 / maxRating*100)
                val media =  (((publicacion.rating5*5) + (publicacion.rating4*4) +(publicacion.rating3*3) +(publicacion.rating2*2) +(publicacion.rating1*1))/maxRating).toDouble()
                viewB.textMedia.text = media.toString()
            }

            val db = FirebaseFirestore.getInstance()
            val query = db.collection("Ciclos").document(publicacion.idCiclo)
                .collection("Modulos").document(publicacion.idModulo)
                .collection("UFs").document(publicacion.idUf)
                .collection("Publicaciones").document(publicacion.id)
            db.collection("Ciclos").document(publicacion.idCiclo).collection("Modulos").document(publicacion.idModulo).get().addOnSuccessListener { document ->
                val nombreModulo = document["nombre"].toString()
                viewB.textModulo.setText(nombreModulo)
            }


                viewB.ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                    // Acción a realizar cuando la calificación cambia
                    if (rating.roundToInt() == 1){
                        publicacion.rating1++
                        query.update("rating1", publicacion.rating1)
                        val progress = 1/maxRating*100
                        viewB.progressBar1.incrementProgressBy(progress)
                    }else if (rating.roundToInt() == 2) {
                        publicacion.rating2++
                        query.update("rating2", publicacion.rating2)
                        val progress = 1/maxRating*100
                        viewB.progressBar2.incrementProgressBy(progress)
                    }else if (rating.roundToInt() == 3) {
                        publicacion.rating3++
                        query.update("rating3", publicacion.rating3)
                        val progress = 1/maxRating*100
                        viewB.progressBar3.incrementProgressBy(progress)
                    }else if (rating.roundToInt() == 4) {
                        publicacion.rating4++
                        query.update("rating4", publicacion.rating4)
                        val progress = 1/maxRating*100
                        viewB.progressBar4.incrementProgressBy(progress)
                    }else if (rating.roundToInt() == 5){
                        publicacion.rating5++
                        query.update("rating5", publicacion.rating5)
                        val progress = 1/maxRating*100
                        viewB.progressBar5.incrementProgressBy(progress)
                    }
                }

            /***
             * Carga de la ruta del enlace a la imagen de la publicacion
             */

            /***
             * Colocamos la imagen en el ImageView del Item Publi.
             */

                val storageRef = storage.reference.child("Imagenes/" + publicacion.imgPubli)
                val localfile = File.createTempFile("tempImage", "jpg")
                storageRef.getFile(localfile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    viewB.imgIcon.setImageBitmap(bitmap)
                }



            val path = publicacion.pathFile.toUri()
            val pdfRef = storageRef.child("pdfs/${path.lastPathSegment}")
            pdfRef.putFile(path).addOnSuccessListener { taskSnapshot ->
                val pdfName = taskSnapshot.metadata?.name
                viewB.textLink.text = pdfName
            }


            /***
             * Inicializacion del enlace
             */
            viewB.textLink.setOnClickListener{
                val queryUrl: Uri = Uri.parse(publicacion.enlace)
                val intent = Intent(Intent.ACTION_VIEW, queryUrl)
                contexto?.startActivity(intent)

            }
        }

    }

    override fun getItemCount(): Int {
        return publiFilter.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                /***
                 * Si no hay una query, el adapter devolvera todas las publicaciones. Para ello, las cargará en publiFilter, puesto que no hay ningun filtro.
                 *
                 */
                if (charString.isEmpty()){
                    publiFilter = publicaciones
                }
                else {
                    /***
                     * Iniciamos una lista que contendrá los resultados filtrados.
                     */
                    var filteredList = mutableListOf<Publicacion>()
                    /***
                     * Definimos el filtro, donde comprobaremos si el titulo de la publicacion contiene la query escrita en el buscador.
                     */
                    publicaciones
                        .filter {
                            (it.titulo.toLowerCase().contains(constraint!!.toString().toLowerCase()))
                        }
                        /***
                         * Todos los resultados que contienen la query en el titulo de la publicacion seran añadidos en la lista para, despues, pasarlos a
                         * publiFilter.
                          */
                        .forEach { filteredList.add(it) }
                    publiFilter = filteredList

                }
                /***
                 * Retornamos todos los valores.
                 */
                return FilterResults().apply { values = publiFilter}
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                /***
                 * Si no hay un valor en values, publiFilter será una lista vacia.
                 */
                publiFilter = if (results?.values == null)
                    mutableListOf()
                /***
                 * Sino, añadira los valores a una Lista de publicaciones.
                 */
                else
                    results.values as List<Publicacion>
                notifyDataSetChanged()
            }
        }
    }
}

