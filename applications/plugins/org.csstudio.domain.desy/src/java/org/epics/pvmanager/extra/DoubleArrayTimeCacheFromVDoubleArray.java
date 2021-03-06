/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
public class DoubleArrayTimeCacheFromVDoubleArray implements DoubleArrayTimeCache {
    
    private NavigableMap<Timestamp, VDoubleArray> cache = new TreeMap<Timestamp, VDoubleArray>();
    private Function<List<VDoubleArray>> function;
    private Display display;

    public DoubleArrayTimeCacheFromVDoubleArray(Function<List<VDoubleArray>> function) {
        this.function = function;
    }
    
    public class Data implements DoubleArrayTimeCache.Data {
        
        private List<Timestamp> times = new ArrayList<Timestamp>();
        private List<double[]> arrays = new ArrayList<double[]>();
        private Timestamp begin;
        private Timestamp end;

        private Data(SortedMap<Timestamp, VDoubleArray> subMap, Timestamp begin, Timestamp end) {
            this.begin = begin;
            this.end = end;
            for (Map.Entry<Timestamp, VDoubleArray> en : subMap.entrySet()) {
                times.add(en.getKey());
                arrays.add(en.getValue().getArray());
            }
        }

        @Override
        public Timestamp getBegin() {
            return begin;
        }

        @Override
        public Timestamp getEnd() {
            return end;
        }

        @Override
        public int getNArrays() {
            return times.size();
        }

        @Override
        public double[] getArray(int index) {
            return arrays.get(index);
        }

        @Override
        public Timestamp getTimestamp(int index) {
            return times.get(index);
        }
        
    }
    
    private void deleteBefore(Timestamp timeStamp) {
        if (cache.isEmpty())
            return;
        
        // This we want to keep as we need to draw the area
        // from the timestamp to the first new value
        Timestamp firstEntryBeforeTimestamp = cache.lowerKey(timeStamp);
        if (firstEntryBeforeTimestamp == null)
            return;
        
        // This is the last entry we want to delete
        Timestamp lastToDelete = cache.lowerKey(firstEntryBeforeTimestamp);
        if (lastToDelete == null)
            return;
        
        Timestamp firstKey = cache.firstKey();
        while (firstKey.compareTo(lastToDelete) <= 0) {
            cache.remove(firstKey);
            firstKey = cache.firstKey();
        }
    }

    @Override
    public DoubleArrayTimeCache.Data getData(Timestamp begin, Timestamp end) {
        List<VDoubleArray> newValues = function.getValue();
        for (VDoubleArray value : newValues) {
            cache.put(value.getTimestamp(), value);
        }
        if (cache.isEmpty())
            return null;
        
        Timestamp newBegin = cache.lowerKey(begin);
        if (newBegin == null)
            newBegin = cache.firstKey();
        
        deleteBefore(begin);
        return data(newBegin, end);
    }
    
    private DoubleArrayTimeCache.Data data(Timestamp begin, Timestamp end) {
        return new Data(cache.subMap(begin, end), begin, end);
    }

    @Override
    public List<DoubleArrayTimeCache.Data> newData(Timestamp beginUpdate, Timestamp endUpdate, Timestamp beginNew, Timestamp endNew) {
        List<VDoubleArray> newValues = function.getValue();
        
        // No new values, just return the last value
        if (newValues.isEmpty()) {
            return Collections.singletonList(data(cache.lowerKey(endNew), endNew));
        }
        
        List<Timestamp> newTimestamps = new ArrayList<Timestamp>();
        for (VDoubleArray value : newValues) {
            cache.put(value.getTimestamp(), value);
            newTimestamps.add(value.getTimestamp());
        }
        if (cache.isEmpty())
            return Collections.emptyList();
        
        Collections.sort(newTimestamps);
        Timestamp firstNewValue = newTimestamps.get(0);
        
        // We have just one section that start from the oldest update.
        // If the oldest update is too far, we use the start of the update region.
        // If the oldest update is too recent, we start from the being period
        Timestamp newBegin = firstNewValue;
        if (firstNewValue.compareTo(beginUpdate) < 0) {
            newBegin = beginUpdate;
        }
        if (firstNewValue.compareTo(beginNew) > 0) {
            newBegin = beginNew;
        }
        
        
        newBegin = cache.lowerKey(newBegin);
        if (newBegin == null)
            newBegin = cache.firstKey();
        
        deleteBefore(beginUpdate);
        return Collections.singletonList(data(newBegin, endNew));
    }

    @Override
    public Display getDisplay() {
        if (display == null) {
            display = cache.firstEntry().getValue();
        }
            
        return display;
    }
    
}
