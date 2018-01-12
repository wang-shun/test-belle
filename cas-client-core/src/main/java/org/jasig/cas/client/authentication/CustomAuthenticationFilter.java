package org.jasig.cas.client.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

public class CustomAuthenticationFilter extends AbstractCasFilter{
	
	 private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;

    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();
    
    private UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass = null;
    
    private static final Map<String, Class<? extends UrlPatternMatcherStrategy>> PATTERN_MATCHER_TYPES =
            new HashMap<String, Class<? extends UrlPatternMatcherStrategy>>();
    
    static {
        PATTERN_MATCHER_TYPES.put("CONTAINS", ContainsPatternUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("REGEX", RegexUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("EXACT", ExactUrlPatternMatcherStrategy.class);
    }
    
    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        if (isRequestUrlExcluded(request)) {
            logger.debug("Request is ignored.");
            filterChain.doFilter(request, response);
            return;
        }
        
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = retrieveTicketFromRequest(request);
        final boolean wasGatewayed = this.gateway && this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            filterChain.doFilter(request, response);
            return;
        }

        final String modifiedServiceUrl;

        logger.debug("no ticket and no assertion found");
        if (this.gateway) {
            logger.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        logger.debug("Constructed service url: {}", modifiedServiceUrl);

        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl,
                getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

        logger.debug("redirecting to \"{}\"", urlToRedirectTo);
        this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
    }


	public UrlPatternMatcherStrategy getIgnoreUrlPatternMatcherStrategyClass() {
		return ignoreUrlPatternMatcherStrategyClass;
	}

	public void setIgnoreUrlPatternMatcherStrategyClass(UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass) {
		this.ignoreUrlPatternMatcherStrategyClass = ignoreUrlPatternMatcherStrategyClass;
	}

	public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }
        
    private boolean isRequestUrlExcluded(final HttpServletRequest request) {
        if (this.ignoreUrlPatternMatcherStrategyClass == null) {
            return false;
        }
        
        final StringBuffer urlBuffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);
    }
	
}
