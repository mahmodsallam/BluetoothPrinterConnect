package com.adc.bluetoothprinterconnect

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adc.bluetoothprinterconnect.databinding.ActivityMainBinding
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback

class MainActivity : AppCompatActivity(), PrintingCallback {
    private lateinit var binding: ActivityMainBinding
    var printing: Printing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (printing != null)
            printing?.printingCallback = this

        binding.btnUnPair.setOnClickListener {
            if (Printooth.hasPairedPrinter())
                Printooth.removeCurrentPrinter()
            else {
                startActivityForResult(
                    Intent(
                        this,
                        ScanningActivity::class.java
                    ), ScanningActivity.SCANNING_FOR_PRINTER
                )
                changePairedAndUnPaired()
            }
        }

        binding.btnPrintHelloWorld.setOnClickListener {
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(
                    Intent(this, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER
                )
            else
                printText()
        }

        changePairedAndUnPaired()
    }

    private fun printText() {
        val list = ArrayList<Printable>()
        list.add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())
        list.add(
            TextPrintable.Builder()
                .setText("Hello World !")
                .setCharacterCode(DefaultPrinter.Companion.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )

        printing?.print(list)
    }

    private fun changePairedAndUnPaired() {
        if (Printooth.hasPairedPrinter())
            binding.btnUnPair.text = "Un Pair ${Printooth.getPairedPrinter()?.name.toString()}"
        else
            binding.btnUnPair.text = "Pair with printer"

    }

    override fun connectingWithPrinter() {
        Toast.makeText(this, "Connecting to printer", Toast.LENGTH_LONG).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this, "Failed:$error", Toast.LENGTH_LONG).show()

    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error:$error", Toast.LENGTH_LONG).show()

    }

    override fun onMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this, "order sent to printer", Toast.LENGTH_LONG).show()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            initPrinting()
        changePairedAndUnPaired()
    }

    private fun initPrinting() {
        if (!Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        if (printing != null)
            printing?.printingCallback = this
    }
}