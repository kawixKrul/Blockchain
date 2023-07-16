package blockchain

import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.nio.file.Files
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

class VerifyMessage(filename: String, keyFile: String) {
    private val list: List<ByteArray>

    init {
        val input = ObjectInputStream(FileInputStream(filename))
        val obj = input.readObject()
        println(obj)
        list = listOf(obj as ByteArray)
        input.close()
        println(
            try {
                if (verifySignature(list[0], list[1], keyFile)) """
                VERIFIED MESSAGE
                ----------------
                ${String(list[0])}
                """.trimIndent() else "Could not verify the signature."
            } catch (e: Exception) {
                e.message
            }
        )
    }

    //Method for signature verification that initializes with the Public Key,
    //updates the data to be verified and then verifies them using the signature
    private fun verifySignature(data: ByteArray, signature: ByteArray, keyFile: String): Boolean {
        val sig = Signature.getInstance("SHA1withRSA")
        sig.initVerify(getPublic(keyFile))
        sig.update(data)
        return sig.verify(signature)
    }

    //Method to retrieve the Public Key from a file
    private fun getPublic(filename: String): PublicKey {
        val keyBytes = Files.readAllBytes(File(filename).toPath())
        val spec = X509EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePublic(spec)
    }
}