public class Logger {

    static Level messageLevel = Level.always;

    enum Level {
        always,
        shout,
        error,
    }

    static void log(String message) {
        log(message, Level.always);
    }

    static void log(String message, Level level) {
        if (level == messageLevel) {
            if (level == Level.error) {
                System.err.println(message);
            } else {
                System.out.println(message);
            }
        } else if (level == Level.always) {
            System.out.println(message);
        }
    }

}
