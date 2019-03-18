package by.squareroot.droidguardtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.ccc.abuse.droidguard.DroidGuard
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedInputStream
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val executor = Executors.newSingleThreadExecutor();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test_button.setOnClickListener {
            executor.submit {
                runDroidguard()
            }
        }
    }

    private fun loadBytecode(filename: String): ByteArray? {
        try {
            val input = BufferedInputStream(assets.open(filename))
            val sb = StringBuilder();
            input.reader().forEachLine {
                sb.append(it)
            }
            val base64 = sb.toString()
            return Base64.decode(base64, Base64.NO_WRAP);
        } catch (ex: Exception) {
            return null
        }
    }

    private fun runDroidguard() {
        var byteCode: ByteArray? = loadBytecode("bytecode.base64");
        byteCode?.let {
            val droidguard = DroidGuard(applicationContext, "addAccount", it)
            val params = mapOf("dg_email" to "test@gmail.com", "dg_gmsCoreVersion" to "910055-30",
                "dg_package" to "com.google.android.gms", "dg_androidId" to UUID.randomUUID().toString())
            droidguard.init()
            val result = droidguard.ss(params)
            droidguard.close()
            Log.d(TAG, "DroidGuard result: " + Base64.encodeToString(result, Base64.NO_WRAP))
        }
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}
