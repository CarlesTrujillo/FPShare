
import android.graphics.drawable.Drawable
import com.google.firebase.firestore.Exclude

data class User(@get:Exclude var email : String = "", var nombre: String = "", var apellidos: String = "",
                var telefono : String = "", var insituto : String = "", var esAdmin : Boolean = false){
}