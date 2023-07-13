package blockchain.Blockchain

import java.io.*
import java.nio.file.Files
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

data class Message(val data: String, val identifier: Long) {
    val publicKey: ByteArray = Files.readAllBytes(File("KeyPair/publicKey").toPath())
    val signature: ByteArray = sign(data+identifier.toString())

    //The method that signs the data using the private key that is stored in keyFile path
    private fun sign(data: String): ByteArray {
        val rsa = Signature.getInstance("SHA1withRSA")
        rsa.initSign(getPrivate("KeyPair/privateKey"))
        rsa.update(data.toByteArray())
        return rsa.sign()
    }

    //Method to retrieve the Private Key from a file
    private fun getPrivate(filename: String): PrivateKey {
        val keyBytes = Files.readAllBytes(File(filename).toPath())
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(spec)
    }

    override fun toString(): String {
        return data
    }

}