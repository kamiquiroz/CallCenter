package ge.magti.server;

/**
 * Created by user on 3/30/17.
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.isomorphic.datasource.DataSource;
import com.isomorphic.datasource.DataSourceManager;
import com.isomorphic.log.Logger;
import com.isomorphic.util.DataTools;

public class aastore {

    static Logger log = new Logger(aastore.class.getName());

    // itemID->supplyItem storage map - prepopulated from sql below
    static Map itemsByID = new HashMap();

    static {
        try {
            // NOTE: The sample supplyItem dataset contains about 4,000 records.
            // grab at most 300 so we don't use too much memory
            DataSource ds = DataSourceManager.get("supplyItem");
            List supplyItems = ds.fetch(new HashMap());
            int maxRows = supplyItems.size();
            if (maxRows > 300) maxRows = 300;
            for (int i = 0; i < maxRows; i++) {
                Map properties = (Map) supplyItems.get(i);
                aaitem item = new aaitem();
                // DataTools.setProperties() this is a SmartClient utility method that applies a
                // map of properties on an object using the Bean reflection mechanism
                DataTools.setProperties(properties, item);
                storeItem(item);
            }
        } catch (Exception e) {
            log.error("Failed to initialize SupplyItemStore", e);
        }
    }

    // add a supply item bean to the stored beans, fetching an itemID for it if necessary
    // if the bean already has an itemID, it will replace any existing bean with that itemID
    public static aaitem storeItem(aaitem item) {
        Long itemID = item.getItemID();
        if (itemID == null) itemID = getNextItemID();
        item.setItemID(itemID);
        itemsByID.put(itemID, item);
        return item;
    }

    // delete the bean with the given itemID
    public static aaitem removeItem(Long itemID) {
        return (aaitem) itemsByID.remove(itemID);
    }

    // get the next available itemID
    public static Long getNextItemID() {
        int highest = 0;
        for (Iterator i = itemsByID.keySet().iterator(); i.hasNext();) {
            Long id = (Long) i.next();
            highest = Math.max(highest, id.intValue());
        }
        return new Long(highest + 1);
    }

    // get the supply item bean, given its itemID, or null if it can't be found
    public static aaitem getItemByID(Long itemID) {
        return (aaitem) itemsByID.get(itemID);
    }

    // This version is called by code that does not need data paging
    public static List findMatchingItems(Long itemID, String itemName) {
        return findMatchingItems(itemID, itemName, 0, Integer.MAX_VALUE);
    }

    // The following two methods show an example of writing bespoke code that can work with
    // SmartClient's data-paging protocol.  Basically, you need two functions: one that can
    // return the total number of rows matching a given set of selection criteria, and one
    // that can return rows m to n of the set of rows that match a given set of selection
    // criteria.  This simple example should help to clarify that.
    public static List findMatchingItems(Long itemID, String itemName, int startRow, int endRow)  {
        Collection items = itemsByID.values();
        List matchingItems = new ArrayList();
        int count = 0;
        for (Iterator i = items.iterator(); i.hasNext();) {
            aaitem item = (aaitem) i.next();

            if (itemID != null && !itemID.equals(item.getItemID())) continue;

      /*      if (itemName != null &&
                    item.getItemName().toLowerCase().indexOf(itemName.toLowerCase()) == -1)
            {
                continue;
            }*/

            // Only return records in the requested range
            if (count++ < startRow) continue;
            if (count >= endRow) break;

            matchingItems.add(item);
        }
        return matchingItems;
    }

    public static int getMatchingRowcount(Long itemID, String itemName)  {
        Collection items = itemsByID.values();
        int count = 0;
        for (Iterator i = items.iterator(); i.hasNext(); ) {
            aaitem item = (aaitem)i.next();

            if (itemID != null && !itemID.equals(item.getItemID())) continue;

         /*   if (itemName != null &&
                    item.getItemName().toLowerCase().indexOf(itemName.toLowerCase()) == -1)
            {
                continue;
            }*/

            count++;
        }
        return count;
    }
}
