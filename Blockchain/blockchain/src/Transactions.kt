package blockchain

import kotlin.random.Random

// Object for storing transaction info and creating transactions
// also for making sure that the transactions can be validated
object Transactions {
    val customers = listOf(
        "Bob", "Alice", "Nick",
        "ShoesShop", "FastFood", "CarShop", "Worker1", "Worker2",
        "Worker3", "Director1", "CarPartsShop", "GamingShop", "BeautyShop")
    val payment = listOf(1,2,5,10,20,50)
    val transactionList = TransactionList
    const val MAX_TRANSACTIONS = 10

    object TransactionList {
        val listWithMiners = mutableListOf<Transaction<Blockchain.Miner>>()
        val withoutMiners = mutableListOf<Transaction<String>>()
        val size
            get() = listWithMiners.size + withoutMiners.size
    }

    data class Transaction<T>(val buyer: T, val target: String, val money: Int) {
        var validated = false
    }

    fun createTransaction(miner: Blockchain.Miner) {
        val buyer: Any = if (Random.nextInt() % 2 == 0) customers.random() else miner
        val target = customers.random()
        val pay = payment.random()
        if (buyer is Blockchain.Miner) {
            transactionList.listWithMiners.add(Transaction(buyer, target, pay))
        } else {
            transactionList.withoutMiners.add(Transaction(buyer.toString(), target, pay))
        }
    }

    // If the miner is the buyer, then the transaction money is taken from the miner's wallet
    // currently the targets money is not implemented
    // returns who send the money and who received it
    @Synchronized
    fun validateTransactions(): List<String> {
        val result = mutableListOf<String>()
        for (t in transactionList.listWithMiners) {
            if (t.buyer.coins >= t.money) {
                t.buyer.coins -= t.money
                t.validated = true
                result.add("${t.buyer.name} sent ${t.money} to ${t.target}")
            }
        }
        for (t in transactionList.withoutMiners) {
            if (t.buyer != t.target) {
                t.validated = true
                result.add("${t.buyer} sent ${t.money} to ${t.target}")
            }
        }
        return result
    }

    @Synchronized
    fun clearBuffer() {
        transactionList.listWithMiners.clear()
        transactionList.withoutMiners.clear()
    }
}