package com.p2p.exceptions;

public class CoreException extends RuntimeException {

    private static final long serialVersionUID = -1352741341491250475L;

    /**
     * Instantiates a new Core exception.
     */
    public CoreException() {
        super();
    }

    /**
     * Instantiates a new Core exception.
     */
    public CoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Instantiates a new Core exception.
     */
    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException(String message) {
        super(message);
    }

    public CoreException(Throwable cause) {
        super(cause);
    }

    public static class NotValidException extends CoreException {

        private static final long serialVersionUID = -8891808942835246216L;

        public NotValidException() {
            super();
        }

        /**
         * Instantiates a new Not valid exception.
         */
        public NotValidException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        /**
         * Instantiates a new Not valid exception.
         */
        public NotValidException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Instantiates a new Not valid exception.
         *
         * @param message the message
         * @param args    the args
         */
        public NotValidException(String message, Object... args) {
            super(String.format(message, args));
        }

        /**
         * Instantiates a new Not valid exception.
         */
        public NotValidException(String message) {
            super(message);
        }

        /**
         * Instantiates a new Not valid exception.
         */
        public NotValidException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * The type Not found exception.
     */
    public static class NotFoundException extends CoreException {

        private static final long serialVersionUID = -8891808942835246216L;

        public NotFoundException() {
            super();
        }

        public NotFoundException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public NotFoundException(String message, Object... args) {
            super(String.format(message, args));
        }

        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public NotFoundException(String message) {
            super(message);
        }

        public NotFoundException(Throwable cause) {
            super(cause);
        }

    }

}
