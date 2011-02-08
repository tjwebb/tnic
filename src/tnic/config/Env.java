package tnic.config;

import java.util.logging.Logger;

public class Env {
    public static final Logger log = Logger.getLogger(Env.class.getName());

    public static javax.servlet.ServletContext SERVLET_CONTEXT = null;

    public static boolean TESTING         = false;
    public static String JSVM_THREAD_POOL = null;
}
