package cat.copernic.fpshare.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import cat.copernic.fpshare.R
import cat.copernic.fpshare.databinding.ActivityMainBinding
import cat.copernic.fpshare.ui.fragments.pantalla_principalDirections
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class
MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

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
        setupActionBarWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.pantalla_principal, R.id.menuAdministracion, R.id.fp_ajustes),
            binding.drawerLayout
        )
        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    private fun setupBottomNavMenu(navController: NavController){
        val bottomNav=findViewById<BottomNavigationView>(R.id.bottom_view)
        bottomNav?.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.getItemId()
        if (id == R.id.nav_logout) {
            Firebase.auth.signOut()
            val action =
                pantalla_principalDirections.actionPantallaPrincipalToLogin()
            //view.findNavController().navigate(action)
        }
        else(
            return item.onNavDestinationSelected(
                findNavController(R.id.nav_host_fragment)
            ) || super.onOptionsItemSelected(item)
        )
            return true
    }

}