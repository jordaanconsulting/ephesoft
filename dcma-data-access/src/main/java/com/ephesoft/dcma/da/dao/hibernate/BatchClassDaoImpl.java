/********************************************************************************* 
* Ephesoft is a Intelligent Document Capture and Mailroom Automation program 
* developed by Ephesoft, Inc. Copyright (C) 2010-2011 Ephesoft Inc. 
* 
* This program is free software; you can redistribute it and/or modify it under 
* the terms of the GNU Affero General Public License version 3 as published by the 
* Free Software Foundation with the addition of the following permission added 
* to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED WORK 
* IN WHICH THE COPYRIGHT IS OWNED BY EPHESOFT, EPHESOFT DISCLAIMS THE WARRANTY 
* OF NON INFRINGEMENT OF THIRD PARTY RIGHTS. 
* 
* This program is distributed in the hope that it will be useful, but WITHOUT 
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
* FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more 
* details. 
* 
* You should have received a copy of the GNU Affero General Public License along with 
* this program; if not, see http://www.gnu.org/licenses or write to the Free 
* Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 
* 02110-1301 USA. 
* 
* You can contact Ephesoft, Inc. headquarters at 111 Academy Way, 
* Irvine, CA 92617, USA. or at email address info@ephesoft.com. 
* 
* The interactive user interfaces in modified source and object code versions 
* of this program must display Appropriate Legal Notices, as required under 
* Section 5 of the GNU Affero General Public License version 3. 
* 
* In accordance with Section 7(b) of the GNU Affero General Public License version 3, 
* these Appropriate Legal Notices must retain the display of the "Ephesoft" logo. 
* If the display of the logo is not reasonably feasible for 
* technical reasons, the Appropriate Legal Notices must display the words 
* "Powered by Ephesoft". 
********************************************************************************/ 

package com.ephesoft.dcma.da.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ephesoft.dcma.core.common.Order;
import com.ephesoft.dcma.core.dao.hibernate.HibernateDao;
import com.ephesoft.dcma.core.hibernate.EphesoftCriteria;
import com.ephesoft.dcma.da.dao.BatchClassDao;
import com.ephesoft.dcma.da.domain.BatchClass;
import com.ephesoft.dcma.da.domain.BatchClassDynamicPluginConfig;
import com.ephesoft.dcma.da.domain.BatchClassGroups;
import com.ephesoft.dcma.da.domain.BatchClassModule;
import com.ephesoft.dcma.da.domain.BatchClassModuleConfig;
import com.ephesoft.dcma.da.domain.BatchClassPlugin;
import com.ephesoft.dcma.da.domain.BatchClassPluginConfig;
import com.ephesoft.dcma.da.domain.KVPageProcess;
import com.ephesoft.dcma.da.service.BatchClassGroupsService;

@Repository
public class BatchClassDaoImpl extends HibernateDao<BatchClass> implements BatchClassDao {

	private static final String IS_DELETED = "isDeleted";
	private static final String IDENTIFIER = "identifier";
	private static final String ASSIGNED_GROUPS_NAME="assignedGroups.groupName";
	private static final String UNC_FOLDER = "uncFolder";
	private static final String NAME = "name";
	private static final String CURRENT_USER = "currentUser";
	private static final String PROCESS_NAME = "processName";
	private static final String ASSIGNED_GROUPS = "assignedGroups";
	private static final Logger LOG = LoggerFactory.getLogger(BatchClassDaoImpl.class);

	@Autowired
	BatchClassGroupsService batchClassGroupsService;

	/**
	 * An api to getch batch class by unc folder name.
	 * 
	 * @param folderName String
	 * @return BatchClass
	 */
	@Override
	public BatchClass getBatchClassbyUncFolder(final String folderName) {
		LOG.info("folder name : " + folderName);
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.eq(UNC_FOLDER, folderName));
		return this.findSingle(criteria);
	}

	/**
	 * An api to fetch BatchClass by batch Class name.
	 * 
	 * @param batchClassName String
	 * @return List<BatchClass>
	 */
	@Override
	public List<BatchClass> getBatchClassbyName(final String batchClassName) {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.eq(NAME, batchClassName));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.findSingle(criteria);
	}

	/**
	 * An api to fetch BatchClass by batch Class processName.
	 * 
	 * @param processName String
	 * @return List<BatchClass>
	 */
	@Override
	public List<BatchClass> getBatchClassbyProcessName(final String processName) {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.eq(PROCESS_NAME, processName));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.findSingle(criteria);
	}

	@Override
	public List<BatchClass> getAllBatchClasses() {
		DetachedCriteria criteria = criteria();
		return this.find(criteria);
	}
	
	@Override
	public List<BatchClass> getAllUnlockedBatchClasses() {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.isNull(CURRENT_USER));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.find(criteria);
	}

	@Override
	public BatchClass getBatchClassByIdentifier(final String batchClassIdentifier) {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.eq(IDENTIFIER, batchClassIdentifier));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.findSingle(criteria);
	}

	/**
	 * This method will update the existing batch class.
	 * 
	 * @param batchClass BatchClass
	 */
	@Override
	public void updateBatchClass(final BatchClass batchClass) {
		saveOrUpdate(batchClass);
	}

	@Override
	public List<BatchClass> getAllBatchClassesForCurrentUser(final String currentUser) {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.eq(CURRENT_USER, currentUser));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return find(criteria);
	}

	/**
	 * This API will fetch all the unc folder paths.
	 * 
	 * @return List<String>
	 */
	@Override
	public List<String> getAllUNCFolderPaths() {
		DetachedCriteria criteria = criteria();
		criteria.setProjection(Projections.property(UNC_FOLDER));
		return find(criteria);
	}

	@Override
	public List<BatchClass> getBatchClassList(final int firstResult, final int maxResults, final List<Order> order) {
		EphesoftCriteria criteria = criteria();
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return find(criteria, firstResult, maxResults, order.toArray(new Order[order.size()]));
	}

	@Override
	public List<BatchClass> getAllBatchClassesByUserRoles(final Set<String> userRoles) {
		DetachedCriteria criteria = criteria();
		List<BatchClass> batchClassList = null;
		if (userRoles == null) {
			batchClassList = new ArrayList<BatchClass>();
		} else {
			List<String> roleList = new ArrayList<String>();
			for (String userRole : userRoles) {
				if (null == userRole || userRole.isEmpty()) {
					continue;
				}
				roleList.add(userRole);
			}
			criteria.createAlias(ASSIGNED_GROUPS, ASSIGNED_GROUPS);
			criteria.add(Restrictions.in(ASSIGNED_GROUPS_NAME, roleList));
			criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
			criteria.addOrder(org.hibernate.criterion.Order.asc(IDENTIFIER));
			batchClassList = find(criteria);
		}

		return batchClassList;
	}

	@Override
	public int getAllBatchClassCountExcludeDeleted(Set<String> userRoles) {
		DetachedCriteria criteria = criteria();
		if (userRoles != null && !userRoles.isEmpty()) {
			List<String> roleList = new ArrayList<String>();
			for (String userRole : userRoles) {
				if (null == userRole || userRole.isEmpty()) {
					continue;
				}
				roleList.add(userRole);
			}
			criteria.createAlias(ASSIGNED_GROUPS,ASSIGNED_GROUPS);
			criteria.add(Restrictions.in(ASSIGNED_GROUPS_NAME, roleList));
		}
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		criteria.addOrder(org.hibernate.criterion.Order.asc(IDENTIFIER));

		return count(criteria);
	}

	@Override
	public List<BatchClass> getAllBatchClassExcludeDeleted() {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.find(criteria);
	}

	@Override
	public List<BatchClass> getAllLoadedBatchClassExcludeDeleted() {
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		List<BatchClass> batchClasses = this.find(criteria);
		for (BatchClass batchClass : batchClasses) {
			for (BatchClassGroups batchClassGroups : batchClass.getAssignedGroups()) {
				if (LOG.isDebugEnabled() && batchClassGroups != null) {
					LOG.debug(batchClassGroups.getGroupName());
				}
			}

			for (BatchClassModule mod : batchClass.getBatchClassModules()) {
				List<BatchClassPlugin> plugins = mod.getBatchClassPlugins();
				List<BatchClassModuleConfig> batchClassModuleConfig = mod.getBatchClassModuleConfig();
				for (BatchClassPlugin plugin : plugins) {
					List<BatchClassPluginConfig> pluginConfigs = plugin.getBatchClassPluginConfigs();
					List<BatchClassDynamicPluginConfig> dynamicPluginConfigs = plugin.getBatchClassDynamicPluginConfigs();
					for (BatchClassPluginConfig conf : pluginConfigs) {
						List<KVPageProcess> kvs = conf.getKvPageProcesses();
						for (KVPageProcess kv : kvs) {
							if (LOG.isDebugEnabled() && kv != null) {
								LOG.debug(kv.getKeyPattern());
							}
						}
					}
					for (BatchClassDynamicPluginConfig dyPluginConfig : dynamicPluginConfigs) {
						if (LOG.isDebugEnabled() && dyPluginConfig != null) {
							LOG.debug(dyPluginConfig.getName());
						}
					}
				}
				for (BatchClassModuleConfig bcmc : batchClassModuleConfig) {
					if (LOG.isDebugEnabled() && bcmc != null) {
						LOG.debug(bcmc.getBatchClassModule().getModule().getName());
					}
				}
			}
		}
		return batchClasses;
	}

	@Override
	public List<String> getAllAssociatedUNCFolders(String batchClassName) {
		DetachedCriteria criteria = criteria();
		criteria.setProjection(Projections.property(UNC_FOLDER));
		criteria.add(Restrictions.eq(NAME, batchClassName));
		criteria.addOrder(org.hibernate.criterion.Order.asc(IDENTIFIER));	
		criteria.addOrder(org.hibernate.criterion.Order.asc(IDENTIFIER));	
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.find(criteria);
	}

	@Override
	public BatchClass getBatchClassByNameIncludingDeleted(String batchClassName) {
		BatchClass batchClass = null;
		DetachedCriteria criteria = criteria();
		criteria.add(Restrictions.eq(NAME, batchClassName));
		List<BatchClass> batchClasses = this.find(criteria);
		if (batchClasses != null && !batchClasses.isEmpty()) {
			batchClass = batchClasses.get(0);
		}
		return batchClass;
	}

	@Override
	public String getBatchClassIdentifierByUNCfolder(String uncFolder) {
		DetachedCriteria criteria = criteria();
		criteria.setProjection(Projections.property(IDENTIFIER));
		criteria.add(Restrictions.eq(UNC_FOLDER, uncFolder));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));		
		return this.findSingle(criteria);
	}
	/**
	 * This API returns list of all batch class identifiers.
	 * 
	 * @return List<String>
	 */
	@Override
	public List<String> getAllBatchClassIdentifiers() {
		DetachedCriteria criteria = criteria();
		criteria.setProjection(Projections.property(IDENTIFIER));
		criteria.add(Restrictions.or(Restrictions.isNull(IS_DELETED), Restrictions.eq(IS_DELETED, false)));
		return this.find(criteria);
	}

}