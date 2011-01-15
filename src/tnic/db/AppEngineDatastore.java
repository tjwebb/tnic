package tnic.db;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.GeoPt;

import com.google.appengine.api.datastore.Cursor;
import org.datanucleus.store.appengine.query.JDOCursorHelper;

import javax.jdo.Transaction;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Persistent;
import javax.jdo.Extent;
import javax.jdo.Query;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class AppEngineDatastore {
    private static class PMF {
        private static final PersistenceManagerFactory pmfInstance =
            JDOHelper.getPersistenceManagerFactory("transactions-optional");
        public static PersistenceManagerFactory get() {
            return pmfInstance;
        }
    }

    private static final Logger log = Logger.getLogger(AppEngineDatastore.class.getName());
    private PersistenceManager pm;

    /**
     * Sole constructor.
     */
    public AppEngineDatastore () { 
        if (pm != null) return;
        pm = PMF.get().getPersistenceManager();
    }
    /*
    public boolean descriptorExists (String agency_id, String checksum) {
        Query query = pm.newQuery(GtfsDatasetDescriptor.class);
        query.setFilter("agency_id == agency_id_param && checksum == checksum_param");
        query.declareParameters("String agency_id_param, String checksum_param");
        try {
            List results = (List)query.execute(agency_id, checksum);
            return (0 != results.size());
        }
        finally {
            query.closeAll();
        }
    }
    */
}
