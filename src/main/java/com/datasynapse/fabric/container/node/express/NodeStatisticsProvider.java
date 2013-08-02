/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.node.express;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.datasynapse.fabric.common.RuntimeContext;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;
import com.datasynapse.fabric.stats.DefaultStatistic;
import com.datasynapse.fabric.stats.Statistic;
import com.datasynapse.fabric.stats.StatisticsMetadata;
import com.datasynapse.fabric.stats.provider.AbstractStatisticsProvider;

public class NodeStatisticsProvider extends AbstractStatisticsProvider {
    
    private static final String TOTAL_ACCESS = "Total Access";
    
    private Map<String, Double> stats;
    private URL url;
    private Logger logger;
    
    public Statistic getStatistic(StatisticsMetadata statistic) {
        
        String name = statistic.getName();
        String internalName = ((NodeStatisticsMetadata) statistic).getInternalName();
        Double value = (Double)stats.get(internalName);

        if (value == null)
            return null;
        else {
            double v = value.doubleValue();
            return new DefaultStatistic(name, v);
        }
    }

    public void init(Container container, Domain domain, ProcessWrapper process, 
            RuntimeContext context) {
        if (logger == null)
            logger = this.getLogger();
        
        url = ((NodeContainer)container).getStatUrl();
    }

    public Statistic[] getStatistics(Object key) {
    	if ( url == null ){
    		return new Statistic[0];
    	}
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            stats = new HashMap<String, Double>();
            while ((line = in.readLine()) != null) {
            	line = line.trim();
            	if ( line.trim().length() > 0 ){
            		int requests = Integer.parseInt(line);
                	stats.put(TOTAL_ACCESS, (double)requests);
                	break;
            	}
            }
        } catch (IOException e) {
            logger.warning("Failed to read server status");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.getStatistics(key);
    }
}
