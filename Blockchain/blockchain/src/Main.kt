package blockchain

import java.io.File
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException


fun main() {
    /* Run once in order to create the keys pair (KeyPair folder) */
    if (!File("KeyPair").exists()) {
        val gk: GenerateKeys
        try {
            gk = GenerateKeys(1024)
            gk.createKeys()
            gk.writeToFile("KeyPair/publicKey", gk.publicKey!!.encoded)
            gk.writeToFile("KeyPair/privateKey", gk.privateKey!!.encoded)
        } catch (e: NoSuchAlgorithmException) {
            System.err.println(e.message)
        } catch (e: NoSuchProviderException) {
            System.err.println(e.message)
        } catch (e: IOException) {
            System.err.println(e.message)
        }
    }

    val blockchain = Blockchain(0)

}