/*
 * Copyright (c) 2013 TIBCO Software Inc. All Rights Reserved.
 * 
 * Use is subject to the terms of the TIBCO license terms accompanying the download of this code. 
 * In most instances, the license terms are contained in a file named license.txt.
 */

package com.datasynapse.fabric.container.node.express;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import com.datasynapse.fabric.common.RuntimeContextVariable;
import com.datasynapse.fabric.container.Container;
import com.datasynapse.fabric.container.ExecContainer;
import com.datasynapse.fabric.container.Feature;
import com.datasynapse.fabric.domain.Domain;
import com.datasynapse.fabric.domain.featureinfo.FeatureInfo;
import com.datasynapse.fabric.domain.featureinfo.HttpFeatureInfo;

/**
 * FabricServer nodejs enabler enabler implementation.
 */
public class NodeContainer extends ExecContainer {
	private static final long serialVersionUID = -4671237224306339437L;
	private HttpFeatureInfo _httpFeatureInfo;
	private URL statsUrl, startConditionURL;

	private transient java.util.logging.Logger engineLogger;

	public NodeContainer() {
		super();
		engineLogger = Logger.getLogger(getClass().getSimpleName());
	}

	@Override
	protected void doInit(List<RuntimeContextVariable> additionalVariables) throws Exception {
		super.doInit(additionalVariables);
		boolean statsEnabled = Boolean.parseBoolean(getStringVariableValue("SERVER_STATS_ENABLED"));
		String serverStatsPath = getStringVariableValue("SERVER_STATS_PATH");
		Feature feature = getFeature(Feature.HTTP_FEATURE_NAME, this);
		_httpFeatureInfo = (HttpFeatureInfo) getFeatureInfo(feature, getCurrentDomain());
		boolean httpEnabled = _httpFeatureInfo.isHttpEnabled();
		if (httpEnabled) {
			String port = getStringVariableValue("LISTEN_PORT");
			startConditionURL = new URL("http://localhost:" + port);
			if ( statsEnabled ){
				statsUrl = new URL("http://localhost:" + port + serverStatsPath);				
			}
		}
		boolean httpsEnabled = _httpFeatureInfo.isHttpsEnabled();
		if (httpsEnabled) {
			String sslPort = getStringVariableValue("LISTEN_PORT_SSL");
			if (startConditionURL == null ){
				startConditionURL = new URL("https://localhost:" + sslPort);
				if ( statsEnabled ){
					statsUrl = new URL("https://localhost:" + sslPort + serverStatsPath);				
				}
			}
		}
		additionalVariables.add(new RuntimeContextVariable("HTTPS_ENABLED", httpsEnabled + "", 0));
		additionalVariables.add(new RuntimeContextVariable("HTTP_ENABLED", httpEnabled + "", 0));

	}

	public URL getStatUrl() {
		return statsUrl;
	}
	
	protected boolean checkCondition() {
		boolean rc = false;
		try {
			startConditionURL.openStream();
			rc = true;
		} catch (IOException e) {
			rc = false;
			getEngineLogger().warning("Failed to open statistics url. Server probably is not running.");
		}
		return rc;
	}

	private static Feature getFeature(String featureName, Container container) {
		if (container != null) {
			for (int i = 0; i < container.getSupportedFeatureCount(); i++) {
				Feature feature = container.getSupportedFeature(i);
				if (feature.getName().equalsIgnoreCase(featureName)) {
					return feature;
				}
			}
		}
		return null;
	}

	private static FeatureInfo getFeatureInfo(Feature feature, Domain domain) {
		String infoClassName = feature.getInfoClass();
		for (int i = 0; i < domain.getFeatureInfoCount(); i++) {
			FeatureInfo info = domain.getFeatureInfo(i);
			if (info.getClass().getName().equals(infoClassName)) {
				return info;
			}
		}
		return null;
	}

}
