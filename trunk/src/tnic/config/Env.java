package tnic.config;

import java.util.logging.Logger;

public class Env {
    public static final Logger log = Logger.getLogger(Env.class.getName());
    /* getServletContext() */
    public static javax.servlet.ServletContext SERVLET_CONTEXT = null;

    public static String PERSISTENCE_PROVIDER   = null;

    public static Class  JDBC_DRIVER            = null;
}
