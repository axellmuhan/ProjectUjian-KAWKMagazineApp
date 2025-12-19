package id.ac.umn.axellmuhamad.projectujian_kawkmagazineapp

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.File

class SentimentHelper(context: Context) {

    private var interpreter: Interpreter? = null

    init {
        // Memuat model dari folder assets
        val modelFile = FileUtil.loadMappedFile(context, "sentiment_mobile_flex.tflite")

        // Konfigurasi Interpreter
        val options = Interpreter.Options()

        // PENTING: Flex Delegate otomatis aktif jika library 'select-tf-ops' terinstall
        // Tidak perlu setting khusus di sini, cukup init biasa.
        interpreter = Interpreter(modelFile, options)
    }

    fun predict(text: String): String {
        if (interpreter == null) return "Model Error"

        // 1. INPUT: Model menerima Tensor String [1]
        // Kita bungkus text dalam Array 1 elemen
        val inputs = arrayOf(text)

        // 2. OUTPUT: Model menghasilkan probabilitas [1][3] (Negatif, Netral, Positif)
        val outputs = Array(1) { FloatArray(3) }

        // 3. JALANKAN
        // Jika crash di sini, berarti library 'select-tf-ops' belum tersync di gradle
        interpreter?.run(inputs, outputs)

        // 4. HASIL
        val probabilities = outputs[0]
        val neg = probabilities[0]
        val net = probabilities[1]
        val pos = probabilities[2]

        // Logika sederhana untuk menentukan label tertinggi
        return if (neg > net && neg > pos) {
            "Negatif 😡 (${(neg * 100).toInt()}%)"
        } else if (net > neg && net > pos) {
            "Netral 😐 (${(net * 100).toInt()}%)"
        } else {
            "Positif 😊 (${(pos * 100).toInt()}%)"
        }
    }

    fun close() {
        interpreter?.close()
    }
}