package com.liferay.samples.fbo.oauth.override;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2AuthorizationException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author fabian-liferay
 */
@Component(
	    immediate = true,
	    property = {
	    },
	    service = ServiceWrapper.class
	)
public class OAuth2ScopeGrantLocalServiceOverride extends OAuth2ScopeGrantLocalServiceWrapper {

	public OAuth2ScopeGrantLocalServiceOverride() {
		super(null);
	}
	
	@Override
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(long companyId, String applicationName,
			String bundleSymbolicName, String accessTokenContent) {

		try {
			OAuth2Authorization oauth2Authorization = _oAuth2AuthorizationLocalService.getOAuth2AuthorizationByAccessTokenContent(accessTokenContent);
			
			List<Long> ids = _customOAuth2ScopeGrantFinder.findByC_A_B_A(companyId, applicationName, bundleSymbolicName, oauth2Authorization.getOAuth2AuthorizationId());
			List<OAuth2ScopeGrant> oauth2ScopeGrants = new ArrayList<>();
			
			ids.forEach(id -> {
				try {
					oauth2ScopeGrants.add(getOAuth2ScopeGrant(id));
				} catch (PortalException e) {
					_log.error("Failed to read oauth 2 scope grant", e);
				}
			});
					
			return oauth2ScopeGrants;			
			
		} catch (NoSuchOAuth2AuthorizationException e) {
			return Collections.emptyList();
		}
	}

	@Reference
	private CustomOAuth2ScopeGrantFinder _customOAuth2ScopeGrantFinder;
	
	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;
	
	private static final Log _log = LogFactoryUtil.getLog(OAuth2ScopeGrantLocalServiceOverride.class);

}