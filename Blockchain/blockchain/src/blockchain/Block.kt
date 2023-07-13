package blockchain.Blockchain

import kotlin.random.Random
import java.security.MessageDigest

data class Block(val id: Int, val timestamp: Long, val previousHash: String, val miner: Int?, val data: List<Message>) {
    private val magicNumber = Random.nextInt()
    val hash = applySha256("$id$timestamp$previousHash$magicNumber$miner${data.joinToString("")}")
    val generationTime = (System.currentTimeMillis() - timestamp) / 1000.0

    // applies Sha256 to a string and returns the result.
    private fun applySha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            /* Applies sha256 to our input */
            val hash = digest.digest(input.toByteArray(charset("UTF-8")))
            val hexString = StringBuilder()
            for (elem in hash) {
                val hex = Integer.toHexString(0xff and elem.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    // checks if the block is valid
    @Synchronized
    fun isValid(n: Int, blockchain: List<Block>): Boolean {
        if (blockchain.lastOrNull() == null) return true
        return hash.substring(0, n) == "0".repeat(n) && blockchain.last().hash == previousHash
    }

    override fun toString(): String {
        return """
            Block:
            ${if (miner != null) "Created by miner: miner$miner" else "Added by owner"}
            ${if (miner != null) "miner$miner gets 100 VC" else "Genesis block"}
            Id: $id
            Timestamp: $timestamp
            Magic number: $magicNumber
            Hash of the previous block:
            $previousHash
            Hash of the block:
            $hash
            Block data:
            ${if (data.isNotEmpty()) data.joinToString("\n") else "No transactions"}
            Block was generating for $generationTime seconds
            ${when (generationTime) {
            in 0.0..0.25 -> "N was increased by 1"
            in 0.25..1.0 -> "N stays the same"
            else -> "N was decreased by 1"
            }}
            """
    }
}