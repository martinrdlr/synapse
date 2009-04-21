/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.synapse.commons.util.datasource.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.synapse.commons.util.MiscellaneousUtil;
import org.apache.synapse.commons.util.SynapseUtilException;
import org.apache.synapse.commons.util.datasource.DataSourceConfigurationConstants;
import org.apache.synapse.commons.util.datasource.DataSourceInformation;
import org.apache.synapse.commons.util.secret.SecretConfigurationConstants;
import org.apache.synapse.commons.util.secret.SecretInformation;
import org.apache.synapse.commons.util.secret.SecretInformationFactory;

import java.util.Properties;

/**
 * Factory to create a DataSourceInformation based on given properties
 */

public class DataSourceInformationFactory {

    private static final Log log = LogFactory.getLog(DataSourceInformationFactory.class);


    private DataSourceInformationFactory() {
    }

    /**
     * Factory method to create a DataSourceInformation instance based on given properties
     *
     * @param dsName     DataSource Name
     * @param properties Properties to create and configure DataSource
     * @return DataSourceInformation instance
     */
    public static DataSourceInformation createDataSourceInformation(String dsName,
                                                                    Properties properties) {

        if (dsName == null || "".equals(dsName)) {
            if (log.isDebugEnabled()) {
                log.debug("DataSource name is either empty or null, ignoring..");
            }
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(DataSourceConfigurationConstants.PROP_SYNAPSE_PREFIX_DS);
        buffer.append(DataSourceConfigurationConstants.DOT_STRING);
        buffer.append(dsName);
        buffer.append(DataSourceConfigurationConstants.DOT_STRING);

        // Prefix for getting particular data source's properties
        String prefix = buffer.toString();

        String driver = MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_DRIVER_CLS_NAME, null);
        if (driver == null) {
            handleException(prefix + DataSourceConfigurationConstants.PROP_DRIVER_CLS_NAME +
                    " cannot be found.");
        }

        String url = MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_URL, null);
        if (url == null) {
            handleException(prefix + DataSourceConfigurationConstants.PROP_URL +
                    " cannot be found.");
        }

        DataSourceInformation datasourceInformation = new DataSourceInformation();
        datasourceInformation.setAlias(dsName);

        datasourceInformation.setDriver(driver);
        datasourceInformation.setUrl(url);

        String dataSourceName = (String) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_DSNAME, dsName,
                String.class);
        datasourceInformation.setDatasourceName(dataSourceName);

        String dsType = (String) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_TYPE,
                DataSourceConfigurationConstants.PROP_BASIC_DATA_SOURCE, String.class);

        datasourceInformation.setType(dsType);

        String repositoryType = (String) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_REGISTRY,
                DataSourceConfigurationConstants.PROP_REGISTRY_MEMORY, String.class);

        datasourceInformation.setRepositoryType(repositoryType);

        Integer maxActive = (Integer) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_MAXACTIVE,
                GenericObjectPool.DEFAULT_MAX_ACTIVE, Integer.class);
        datasourceInformation.setMaxActive(maxActive);

        Integer maxIdle = (Integer) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_MAXIDLE,
                GenericObjectPool.DEFAULT_MAX_IDLE, Integer.class);
        datasourceInformation.setMaxIdle(maxIdle);

        Long maxWait = (Long) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_MAXWAIT,
                GenericObjectPool.DEFAULT_MAX_WAIT, Long.class);

        datasourceInformation.setMaxWait(maxWait);

        // Construct DriverAdapterCPDS reference
        String suffix = DataSourceConfigurationConstants.PROP_CPDSADAPTER +
                DataSourceConfigurationConstants.DOT_STRING +
                DataSourceConfigurationConstants.PROP_CLASS_NAME;
        String className = MiscellaneousUtil.getProperty(properties, prefix + suffix,
                DataSourceConfigurationConstants.PROP_CPDSADAPTER_DRIVER);
        datasourceInformation.addParameter(suffix, className);
        suffix = DataSourceConfigurationConstants.PROP_CPDSADAPTER +
                DataSourceConfigurationConstants.DOT_STRING +
                DataSourceConfigurationConstants.PROP_FACTORY;
        String factory = MiscellaneousUtil.getProperty(properties, prefix + suffix,
                DataSourceConfigurationConstants.PROP_CPDSADAPTER_DRIVER);
        datasourceInformation.addParameter(suffix, factory);
        suffix = DataSourceConfigurationConstants.PROP_CPDSADAPTER +
                DataSourceConfigurationConstants.DOT_STRING +
                DataSourceConfigurationConstants.PROP_NAME;
        String name = MiscellaneousUtil.getProperty(properties, prefix + suffix,
                "cpds");
        datasourceInformation.addParameter(suffix, name);

        boolean defaultAutoCommit = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_DEFAULTAUTOCOMMIT, true,
                Boolean.class);

        boolean defaultReadOnly = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_DEFAULTREADONLY, false,
                Boolean.class);

        boolean testOnBorrow = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_TESTONBORROW, true,
                Boolean.class);

        boolean testOnReturn = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_TESTONRETURN, false,
                Boolean.class);

        long timeBetweenEvictionRunsMillis = (Long) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_TIMEBETWEENEVICTIONRUNSMILLIS,
                GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS, Long.class);

        int numTestsPerEvictionRun = (Integer) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_NUMTESTSPEREVICTIONRUN,
                GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN, Integer.class);

        long minEvictableIdleTimeMillis = (Long) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_MINEVICTABLEIDLETIMEMILLIS,
                GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS, Long.class);

        boolean testWhileIdle = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_TESTWHILEIDLE, false,
                Boolean.class);

        String validationQuery = MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_VALIDATIONQUERY, null);

        int minIdle = (Integer) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_MINIDLE,
                GenericObjectPool.DEFAULT_MIN_IDLE,
                Integer.class);

        int initialSize = (Integer) MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_INITIALSIZE, 0,
                Integer.class);

        int defaultTransactionIsolation = (Integer) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_DEFAULTTRANSACTIONISOLATION, -1,
                Integer.class);

        String defaultCatalog = MiscellaneousUtil.getProperty(
                properties, prefix + DataSourceConfigurationConstants.PROP_DEFAULTCATALOG, null);

        boolean accessToUnderlyingConnectionAllowed =
                (Boolean) MiscellaneousUtil.getProperty(properties,
                        prefix +
                                DataSourceConfigurationConstants.
                                        PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED,
                        false, Boolean.class);

        boolean removeAbandoned = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_REMOVEABANDONED, false,
                Boolean.class);

        int removeAbandonedTimeout = (Integer) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_REMOVEABANDONEDTIMEOUT, 300,
                Integer.class);

        boolean logAbandoned = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_LOGABANDONED, false,
                Boolean.class);

        boolean poolPreparedStatements = (Boolean) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_POOLPREPAREDSTATEMENTS, false,
                Boolean.class);

        int maxOpenPreparedStatements = (Integer) MiscellaneousUtil.getProperty(properties,
                prefix + DataSourceConfigurationConstants.PROP_MAXOPENPREPAREDSTATEMENTS,
                GenericKeyedObjectPool.DEFAULT_MAX_TOTAL, Integer.class);

        datasourceInformation.setDefaultAutoCommit(defaultAutoCommit);
        datasourceInformation.setDefaultReadOnly(defaultReadOnly);
        datasourceInformation.setTestOnBorrow(testOnBorrow);
        datasourceInformation.setTestOnReturn(testOnReturn);
        datasourceInformation.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasourceInformation.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        datasourceInformation.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasourceInformation.setTestWhileIdle(testWhileIdle);
        datasourceInformation.setMinIdle(minIdle);
        datasourceInformation.setDefaultTransactionIsolation(defaultTransactionIsolation);
        datasourceInformation.setAccessToUnderlyingConnectionAllowed(
                accessToUnderlyingConnectionAllowed);
        datasourceInformation.setRemoveAbandoned(removeAbandoned);
        datasourceInformation.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        datasourceInformation.setLogAbandoned(logAbandoned);
        datasourceInformation.setPoolPreparedStatements(poolPreparedStatements);
        datasourceInformation.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        datasourceInformation.setInitialSize(initialSize);

        if (validationQuery != null && !"".equals(validationQuery)) {
            datasourceInformation.setValidationQuery(validationQuery);
        }

        if (defaultCatalog != null && !"".equals(defaultCatalog)) {
            datasourceInformation.setDefaultCatalog(defaultCatalog);
        }

        datasourceInformation.addProperty(
                prefix + DataSourceConfigurationConstants.PROP_ICFACTORY,
                MiscellaneousUtil.getProperty(
                        properties, prefix + DataSourceConfigurationConstants.PROP_ICFACTORY,
                        null));
        //Provider URL
        datasourceInformation.addProperty(
                prefix + DataSourceConfigurationConstants.PROP_PROVIDER_URL,
                MiscellaneousUtil.getProperty(
                        properties, prefix + DataSourceConfigurationConstants.PROP_PROVIDER_URL,
                        null));

        datasourceInformation.addProperty(
                prefix + DataSourceConfigurationConstants.PROP_PROVIDER_PORT,
                MiscellaneousUtil.getProperty(
                        properties, prefix + DataSourceConfigurationConstants.PROP_PROVIDER_PORT,
                        null));

        String passwordPrompt = (String) MiscellaneousUtil.getProperty(
                properties, prefix + SecretConfigurationConstants.PROP_PASSWORD_PROMPT, 
                "Password for datasource " + dsName, String.class);

        SecretInformation secretInformation = SecretInformationFactory.createSecretInformation(
                properties, prefix, passwordPrompt);

        datasourceInformation.setSecretInformation(secretInformation);

        return datasourceInformation;
    }

    /**
     * Helper methods for handle errors.
     *
     * @param msg The error message
     */
    private static void handleException(String msg) {
        log.error(msg);
        throw new SynapseUtilException(msg);
    }
}
