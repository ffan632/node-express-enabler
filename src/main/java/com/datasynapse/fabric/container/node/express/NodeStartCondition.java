/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.node.express;

import com.datasynapse.fabric.common.RuntimeContext;
import com.datasynapse.fabric.common.StartCondition;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ProcessWrapper;
import com.datasynapse.fabric.domain.Domain;

public class NodeStartCondition implements StartCondition {
    
    private NodeContainer _container;
    private long _pollPeriod;
    
    public long getPollPeriod() {
        return _pollPeriod;
    }

    public boolean hasStarted() throws Exception {
        return _container.checkCondition();
    }

    public void init(Container container, Domain domain, ProcessWrapper process, RuntimeContext runtimeContext) {
        _container = (NodeContainer)container;
    }

    public void setPollPeriod(long pollPeriod) {
        _pollPeriod = pollPeriod;
    }

}
