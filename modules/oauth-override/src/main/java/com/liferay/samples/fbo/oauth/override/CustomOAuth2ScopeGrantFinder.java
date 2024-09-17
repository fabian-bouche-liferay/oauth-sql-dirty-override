package com.liferay.samples.fbo.oauth.override;

import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		service = CustomOAuth2ScopeGrantFinder.class
		)
public class CustomOAuth2ScopeGrantFinder extends BasePersistenceImpl<OAuth2ScopeGrant> {

	public static final String FIND_BY_C_A_B_A =
			CustomOAuth2ScopeGrantFinder.class.getName() + ".findByC_A_B_A";
	
	public Collection<OAuth2ScopeGrant> findByC_A_B_A(
			long companyId, String applicationName, String bundleSymbolicName,
			long oauth2AuthorizationId) {
		
		Session session = null;

		try {
			session = openSession();

			String sql = _customSQL.get(getClass(), FIND_BY_C_A_B_A);

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			sqlQuery.addSynchronizedEntityName("com.liferay.oauth2.provider.model.impl.OAuth2ScopeGrantImpl");
			
			queryPos.add(companyId);
			queryPos.add(applicationName);
			queryPos.add(bundleSymbolicName);
			queryPos.add(oauth2AuthorizationId);
			
			List<OAuth2ScopeGrant> oAuth2ScopeGrants = (List<OAuth2ScopeGrant>)QueryUtil.list(
				sqlQuery, getDialect(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			return oAuth2ScopeGrants;
		}
		catch (Exception exception) {
			_log.error("Failed to find", exception);
			throw new SystemException(exception);
		}
		finally {
			closeSession(session);
		}		
		
	}
	
	@Override
	@Reference(
		target = SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
	
	private static final Log _log = LogFactoryUtil.getLog(CustomOAuth2ScopeGrantFinder.class);
	
	@Reference
	private CustomSQL _customSQL;	
	
	public static final String BUNDLE_SYMBOLIC_NAME =
			"com.liferay.samples.fbo.oauth.override";

	public static final String ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER =
		"(origin.bundle.symbolic.name=" + BUNDLE_SYMBOLIC_NAME + ")";

	public static final String SERVICE_CONFIGURATION_FILTER =
		"(&" + ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER + "(name=service))";
}
