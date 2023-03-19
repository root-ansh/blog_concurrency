package work.curioustools.basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import work.curioustools.basic.concurrency_scenarios.BasicNetworkConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            BasicNetworkConnection.connect()

    }
}