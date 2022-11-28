package cat.copernic.fpshare.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.fpshare.clases.Menu
import cat.copernic.fpshare.R
import cat.copernic.fpshare.databinding.ItemMenuBinding
import cat.copernic.fpshare.modelo.Cicle

class MenuAdapter (private val ciclos: MutableList<Cicle>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MenuAdapter.ViewHolder>(){
    private lateinit var contexto: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        contexto = parent.context
        val vista = LayoutInflater.from(contexto).inflate(R.layout.item_menu, parent, false)

        return ViewHolder(vista)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var ciclo = ciclos.get(position)
            with(holder){
                ViewB.txtMenu.text = ciclo.nombre
            }
    }

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view),
    View.OnClickListener{
        var ViewB = ItemMenuBinding.bind(view)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            val id = ciclos.get(position).idCiclo
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(id)
            }
        }

    }

    override fun getItemCount(): Int {
        return ciclos.size
    }

    interface OnItemClickListener {
        fun onItemClick(id: String)
    }

}
