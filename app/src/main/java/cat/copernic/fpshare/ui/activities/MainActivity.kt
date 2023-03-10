package cat.copernic.fpshare.ui.activities

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import cat.copernic.fpshare.R
import cat.copernic.fpshare.databinding.ActivityMainBinding
import cat.copernic.fpshare.modelo.AlarmReceiver
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * Main Activity
 *
 * @author FPShare
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var pendingIntent: PendingIntent
    private var user = Firebase.auth.currentUser
    private var bd = FirebaseFirestore.getInstance()
    companion object {
        var alarma: Int = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get the navigation host fragment from this Activity

        // Instantiate the navController using the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        setupBottomNavMenu(navController)
        // Make sure actions in the ActionBar get propagated to the NavController
        // setupActionBarWithNavController(navController)

        /**
         * Men?? lateral
         */
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.pantalla_principal, R.id.menuAdministracion, R.id.login),
            binding.drawerLayout
        )

        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        esAdmin()

        /**
         * Aqu?? creamos el bot??n de cerrar sesi??n para que el usuario pueda salir de la app
         * cerrando la sesi??n y volviendo a la pantalla de login
         */
        val signupmenuitem = binding.navView.menu.findItem(R.id.nav_logout)
        signupmenuitem.setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
            true
        }

        /**
         * Creaci??n de boton de recordatorio, bot??n que si pulsamos nos enviar?? una notificaci??n
         */

        //asignamos el boton recordatorio a una variable
        val recordatorio = binding.navView.menu.findItem(R.id.recordatorio)

        //si pulsmos el boton recordatorio
        recordatorio.setOnMenuItemClickListener {

            //creamos un objeto de notificationManager y comprobrobamos si las notificaciones estan activadas
            val notificationManager = NotificationManagerCompat.from(this)
            val areNotificationsEnabled = notificationManager.areNotificationsEnabled()

            //si las notificaciones estsn actyivadas
            if (areNotificationsEnabled) {
                //Permiso concedido


                //si la variable alarama es 0
                if (alarma == 0) {
                    //llamamos a la funcion
                    crearAlerta2(getString(R.string.activar_recordatorio), 0)
                    alarma++
                }else{
                    //llamamos a la funci??n
                    crearAlerta2(getString(R.string.desactivar_recordatorio), 1)
                    alarma = 0
                }
            } else {
                //Permiso no concedido

                //llamamos la funci??n
                crearAlerta1()

            }
            true
        }
    }

    /**
     * Funci??n que crea una alerta que dependiendo del int recibido mostrara una descripci??n u otra
     * y hara una funci??n u otra
     * @param descripcion un string que contiene la descripci??n que se mostrara en la alerta
     * @param numero un int que puede ser 0 o 1
     * si es 0 mostrar?? una alerta para crear una alarma y la opci??n aceptar crear?? la alrma
     * si es 1 mostrara una alerta para desactivar una alarma y la opci??n aceptar desctiva la alarma activada
     */

    private fun crearAlerta2(descripcion: String, numero: Int) {
        //creramos una alerta
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.reminder))
        builder.setMessage(descripcion)
        builder.setPositiveButton(getString(R.string.aceptat)) { dialog, which ->
            // Acci??n al presionar OK

            //si el numero es 0
            if (numero == 0) {
                //creamos la alarma
                crearAlarma()
            }else{
                //desctivamos la alrma
                desactivarAlarma()
            }
        }
        builder.setNegativeButton(getString(R.string.cancelar)) {dialog, which ->
        // Boton cancel
    }
        //creamos la alerta y la mostramos
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /**
     * Funci??n con la que desactivamos la alarma activada
     */
    private fun desactivarAlarma() {
        //desactivamos la alarma

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

    }

    /**
     * Funci??n para comprobar que el usuario es administrador, si no lo es, no le cargar?? el
     * bot??n de administraci??n, en cambio si lo es, si que le aparecer??
     */

    /**
     * Funci??n para crear una alerta que avisa al usuario que no tiene los permisos de notificacion de la app activados
     */
    private fun crearAlerta1() {

        //creramos una alerta
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.reminder))
        builder.setMessage(getString(R.string.decripcion_alerta_no_permisos))
        builder.setPositiveButton(getString(R.string.aceptat)) { dialog, which ->
            // Acci??n al presionar OK
        }

        //creamos la lerta y la mostramos
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    private fun esAdmin(){
        //recogemos el email del usuario
        val email = user?.email.toString()

        //buscamos el usuario en la base de datos y miramos si el usuario ees administrador
        bd.collection("Usuarios").document(email).get().addOnSuccessListener { document ->

            val esAdmin = document["esAdmin"] as Boolean
            //si no es admin ocultamos la opci??n adminstrador del menu lateral
            if(!esAdmin){
                binding.navView.menu.findItem(R.id.menuAdministracion).setVisible(false)
            }
        }
    }

    /**
     * Funci??n para la creaci??n del men?? en el appBar
     *
     * @return boolean
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    /**
     * Funci??n de la creaci??n del men?? inferior
     *
     * @param navController
     */
    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_view)
        bottomNav?.setupWithNavController(navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(
            findNavController(R.id.nav_host_fragment)
        ) || super.onOptionsItemSelected(item)
    }

    /**
     * Funci??n para la creaci??n de la alarma
     */
    private fun crearAlarma() {

        //creamos un intent que inicia la clase AlarmaReceiver
         val alarmIntent = Intent(this, AlarmReceiver::class.java)

        //creamos un pendingItem
         pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE)

        //obtenemos el AlarmManager desde el servicio del sistema, para configurar una alarma
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //creamos un objeto calender y le asignamos la hora actual
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        //le a??adimos 5 segundos
        calendar.add(Calendar.SECOND, 5)

        //creamos la alarma
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
        /*
        return item.onNavDestinationSelected(

            findNavController(R.id.nav_host_fragment)
        ) || super.onOptionsItemSelected(item)




        startActivity(Intent(this, Login::class.java))
        finish()
         */


}

