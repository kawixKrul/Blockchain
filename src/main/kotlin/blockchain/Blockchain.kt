package blockchain

class Blockchain(@Volatile private var n: Int) {
    val blockchain = mutableListOf<Block>(Block(1, System.currentTimeMillis(), "0", null, listOf()))
    private var timestamp = System.currentTimeMillis()
    val miners = List(15) { Miner() }
    private val transactionHandler = Transactions
    private val transactionMaker = TransactionMaker()
    private val data = mutableListOf<Message>()

    companion object {
        const val BLOCKCHAIN_LENGTH = 15
        @Volatile var identifier: Long = Long.MIN_VALUE
    }

    init {
        println(blockchain[0])
        n += 1
        transactionMaker.start()
        for (miner in miners) miner.start()
        for (miner in miners) miner.join()
        transactionMaker.join()
    }

    // gets valid block
    private fun getValid(id: Int, previousHash: String, miner: Int): Block? {
        val block = Block(id, timestamp, previousHash, miner, data)
        synchronized(this) {
            if (block.isValid(n, blockchain)) {
                n = when (block.generationTime) {
                    in 0.0..0.25 -> n + 1
                    in 0.25..1.0 -> n
                    else -> n - 1
                }
                updateData()
                return block
            } else return null
        }
    }

    @Synchronized
    private fun updateData() {
        data.clear()
        Transactions.validateTransactions().forEach { data.add(Message(it, identifier++)) }
        Transactions.clearBuffer()
        timestamp = System.currentTimeMillis()
    }

    fun printBlock(block: Block): Block {
        println(block)
        return block
    }

    override fun toString(): String {
        return blockchain.joinToString("\n")
    }

    // class for a miner
    inner class Miner(): Thread() {
        var coins = 100

        override fun run() {
            while (blockchain.size < BLOCKCHAIN_LENGTH) {
                getValid(blockchain.size+1, blockchain.lastOrNull()?.hash ?: "0", miners.indexOf(this))
                    ?.let { blockchain.add(printBlock(it)) }
            }
        }
    }

    // class that generates transactions during the mining process
    inner class TransactionMaker(): Thread() {
        override fun run() {
            while (blockchain.size < BLOCKCHAIN_LENGTH) {
                if (Transactions.TransactionList.size < Transactions.MAX_TRANSACTIONS)
                    Transactions.createTransaction(miners.random())
                else
                    sleep(100)
            }
        }
    }

}