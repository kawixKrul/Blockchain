package blockchain

import kotlin.random.Random

class Blockchain(@Volatile private var n: Int) {
    val blockchain = mutableListOf<Block>()
    private var timestamp = System.currentTimeMillis()
    private val miners = List(15) { Miner() }

    companion object {
        const val BLOCKCHAIN_LENGTH = 15
        var identifier: Long = Long.MIN_VALUE
        val customers = listOf(
            "Bob", "Alice", "Nick",
            "ShoesShop", "FastFood", "CarShop", "Worker1", "Worker2",
            "Worker3", "Director1", "CarPartsShop", "GamingShop", "BeautyShop")
        val payment = listOf(1,2,5,10,20,50)
    }

    init {
        
        for (miner in miners) miner.start()
        for (miner in miners) miner.join()
    }

    // gets valid block
    private fun getValid(id: Int, previousHash: String, miner: Int): Block? {
        val block = Block(id, timestamp, previousHash, miner)
        synchronized(this) {
            if (block.isValid(n, blockchain)) {
                n = when (block.generationTime) {
                    in 0.0..0.25 -> n + 1
                    in 0.25..1.0 -> n
                    else -> n - 1
                }
                val msg = List(Random.nextInt(0,5)) { Message(transaction(), identifier++) }
                block.data = msg.filter { it.data != "" }.sortedBy { it.identifier }
                miners[miner].coins += 100
                timestamp = System.currentTimeMillis()
                return block
            } else return null
        }
    }

    fun printBlock(block: Block): Block {
        println(block)
        return block
    }

    private fun transaction(): String {
        val buyer = if (Random.nextInt()%2==0) customers.random() else miners.random()
        val target = customers.random()
        val pay = payment.random()
        if (buyer is Miner && buyer.coins > pay) {
            buyer.coins -= pay
            return "miner${miners.indexOf(buyer)} sent $pay VC to $target"
        } else if (buyer is Miner) {
            return ""
        } else {
            return "$buyer sent $pay VC to $target"
        }
    }

    override fun toString(): String {
        return blockchain.joinToString("\n")
    }

    // class for a miner
    inner class Miner(): Thread() {
        var coins = 0

        override fun run() {
            while (blockchain.size < BLOCKCHAIN_LENGTH) {
                getValid(blockchain.size+1, blockchain.lastOrNull()?.hash ?: "0", miners.indexOf(this))
                    ?.let { blockchain.add(printBlock(it)) }
            }
        }
    }
}