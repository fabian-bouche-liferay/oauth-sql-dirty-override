package com.liferay.samples.fbo.oauth.override;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2AuthorizationException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.util.Collection;
import java.util.Collections;

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
			
			return _customOAuth2ScopeGrantFinder.findByC_A_B_A(companyId, applicationName, bundleSymbolicName, oauth2Authorization.getOAuth2AuthorizationId());			
			
		} catch (NoSuchOAuth2AuthorizationException e) {
			return Collections.emptyList();
		}
	}

	@Reference
	private CustomOAuth2ScopeGrantFinder _customOAuth2ScopeGrantFinder;
	
	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;
	
}