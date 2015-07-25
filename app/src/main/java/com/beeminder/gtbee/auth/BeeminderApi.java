package com.beeminder.gtbee.auth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;

public class BeeminderApi extends DefaultApi20
{
    private static final String AUTHORIZATION_URL ="https://www.beeminder.com/apps/authorize?client_id=%s&response_type=token&redirect_uri=%s";


    @Override
    public String getAccessTokenEndpoint()
    {
        return "https://www.beeminder.com/apps/authorize";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config)
    {
        return String.format(AUTHORIZATION_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }

}
