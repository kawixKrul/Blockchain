package services.cryptography

import java.io.File
import java.io.FileOutputStream
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey

class GenerateKeys(keyLength: Int) {
    private val keyGen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    private var pair: KeyPair? = null
    var privateKey: PrivateKey? = null
    var publicKey: PublicKey? = null

    init {
        keyGen.initialize(keyLength)
    }

    fun createKeys() {
        pair = keyGen.generateKeyPair()
        privateKey = pair!!.private
        publicKey = pair!!.public
    }

    fun writeToFile(path: String, key: ByteArray) {
        val f = File(path)
        f.parentFile.mkdirs()
        val fos = FileOutputStream(f)
        fos.write(key)
        fos.flush()
        fos.close()
    }

}