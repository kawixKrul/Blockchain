package services.mining

class Mining() {
    enum class States{
        MINING,
        STOPPED
    }
    var currentState = States.STOPPED


}