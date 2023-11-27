
class Main {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello World!")

            // Try adding program arguments via Run/Debug configuration.
            // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
            println("Program arguments: ${TestUtils.longRunningOperation(100)}")

        }
    }
}