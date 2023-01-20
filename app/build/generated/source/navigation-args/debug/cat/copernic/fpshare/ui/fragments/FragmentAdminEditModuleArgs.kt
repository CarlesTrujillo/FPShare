package cat.copernic.fpshare.ui.fragments

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class FragmentAdminEditModuleArgs(
  public val idModulo: String,
  public val idCiclo: String
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("idModulo", this.idModulo)
    result.putString("idCiclo", this.idCiclo)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("idModulo", this.idModulo)
    result.set("idCiclo", this.idCiclo)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): FragmentAdminEditModuleArgs {
      bundle.setClassLoader(FragmentAdminEditModuleArgs::class.java.classLoader)
      val __idModulo : String?
      if (bundle.containsKey("idModulo")) {
        __idModulo = bundle.getString("idModulo")
        if (__idModulo == null) {
          throw IllegalArgumentException("Argument \"idModulo\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"idModulo\" is missing and does not have an android:defaultValue")
      }
      val __idCiclo : String?
      if (bundle.containsKey("idCiclo")) {
        __idCiclo = bundle.getString("idCiclo")
        if (__idCiclo == null) {
          throw IllegalArgumentException("Argument \"idCiclo\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"idCiclo\" is missing and does not have an android:defaultValue")
      }
      return FragmentAdminEditModuleArgs(__idModulo, __idCiclo)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle):
        FragmentAdminEditModuleArgs {
      val __idModulo : String?
      if (savedStateHandle.contains("idModulo")) {
        __idModulo = savedStateHandle["idModulo"]
        if (__idModulo == null) {
          throw IllegalArgumentException("Argument \"idModulo\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"idModulo\" is missing and does not have an android:defaultValue")
      }
      val __idCiclo : String?
      if (savedStateHandle.contains("idCiclo")) {
        __idCiclo = savedStateHandle["idCiclo"]
        if (__idCiclo == null) {
          throw IllegalArgumentException("Argument \"idCiclo\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"idCiclo\" is missing and does not have an android:defaultValue")
      }
      return FragmentAdminEditModuleArgs(__idModulo, __idCiclo)
    }
  }
}
