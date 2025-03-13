package uk.co.notori.gol;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.restricted.content.catalog.ContentCatalog;
import com.amazon.kindle.restricted.runtime.Framework;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class Util {
    private static boolean kindle;
    
    public static boolean isKindle() {
        return kindle;
    }
    
	public static void setKindle(boolean kindleValue) {
        kindle = kindleValue;
    }
    
	
    public static BookletContext getBookletContext(AbstractBooklet booklet) {
        BookletContext bc = null;
        Method[] methods = AbstractBooklet.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getReturnType() == BookletContext.class) {
                // Double check that it takes no arguments, too...
                Class<?>[] params = methods[i].getParameterTypes();
                if (params.length == 0) {
                    try {
                        bc = (BookletContext) methods[i].invoke(booklet, (Object[]) null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return bc;
    }
    
    public static Container getUIContainer(AbstractBooklet booklet) throws InvocationTargetException, IllegalAccessException {
        Method getUIContainer = null;
        
        // Should be the only method returning a Container in BookletContext...
        Method[] methods = BookletContext.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getReturnType() == Container.class) {
                // Double check that it takes no arguments, too...
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 0) {
                    getUIContainer = method;
                    break;
                }
            }
        }
        
        if (getUIContainer != null) {
            BookletContext bc = Util.getBookletContext(booklet);
            Container rootContainer = (Container) getUIContainer.invoke(bc, (Object[]) null);
            return rootContainer;
        } else {
            return null;
        }
    }

	// And this was always obfuscated...
	// NOTE: Pilfered from KPVBooklet (https://github.com/koreader/kpvbooklet/blob/master/src/com/github/chrox/kpvbooklet/ccadapter/CCAdapter.java)
	/**
	 * Perform CC request of type "query" and "change"
	 * @param req_type request type of "query" or "change"
	 * @param req_json request json string
	 * @return return json object
	 */
    private static JSONObject ccPerform(String req_type, String req_json) {
        ContentCatalog CC = Framework.getService(ContentCatalog.class);
        try {
            Method perform = null;
            
            // Enumeration approach
            Class<?>[] signature = {String.class, String.class, int.class, int.class};
            Method[] methods = ContentCatalog.class.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                Class<?>[] params = method.getParameterTypes();
                if (params.length == signature.length) {
                    int j = 0;
                    while (j < signature.length && params[j].isAssignableFrom(signature[j])) {
                        j++;
                    }
                    if (j == signature.length) {
                        perform = method;
                        break;
                    }
                }
            }
            
            if (perform != null) {
                return (JSONObject) perform.invoke(CC, new Object[]{req_type, req_json, Integer.valueOf(200), Integer.valueOf(5)});
            } else {
                System.err.println("Failed to find perform method!");
                return new JSONObject();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t.toString());
        }
    }
    
    public static void updateCCDB(String tag, String path) {
        long lastAccess = System.currentTimeMillis() / 1000L;
        String escapedPath = JSONObject.escape(path);
        
        // Query for the file
        String json_query = "{\"filter\":{\"Equals\":{\"value\":\"" + escapedPath + "\",\"path\":\"location\"}},\"type\":\"QueryRequest\",\"maxResults\":1,\"sortOrder\":[{\"order\":\"descending\",\"path\":\"lastAccess\"},{\"order\":\"ascending\",\"path\":\"titles[0].collation\"}],\"startIndex\":0,\"id\":1,\"resultType\":\"fast\"}";
        JSONObject json = Util.ccPerform("query", json_query);
        JSONArray values = (JSONArray) json.get("values");
        JSONObject value = (JSONObject) values.get(0);
        String uuid = (String) value.get("uuid");
        
        // Update the file metadata
        String json_change = "{\"commands\":[{\"update\":{\"uuid\":\"" + uuid + "\",\"lastAccess\":" + lastAccess + ",\"displayTags\":[\"" + tag + "\"]" + "}}],\"type\":\"ChangeRequest\",\"id\":1}";
        Util.ccPerform("change", json_change);
    }
}
