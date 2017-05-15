package controllers.actions;

import models.user.User;
import play.api.http.MediaRange;
import play.api.mvc.Request;
import play.api.mvc.RequestHeader;
import play.i18n.Lang;
import play.mvc.Http;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthenticatedRequest implements Http.Request {
    private final Http.Request delegate;
    private final User authenticatedUser;

    public AuthenticatedRequest(Http.Request delegate, User authenticatedUser) {
        this.delegate = delegate;
        this.authenticatedUser = authenticatedUser;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public Http.RequestBody body() {
        return delegate.body();
    }

    @Override
    public String username() {
        return delegate.username();
    }

    @Override
    @Deprecated
    public void setUsername(String username) {
        delegate.setUsername(username);
    }

    @Override
    public Http.Request withUsername(String username) {
        return delegate.withUsername(username);
    }

    @Override
    public Request<Http.RequestBody> _underlyingRequest() {
        return delegate._underlyingRequest();
    }

    @Override
    public String uri() {
        return delegate.uri();
    }

    @Override
    public String method() {
        return delegate.method();
    }

    @Override
    public String version() {
        return delegate.version();
    }

    @Override
    public String remoteAddress() {
        return delegate.remoteAddress();
    }

    @Override
    public boolean secure() {
        return delegate.secure();
    }

    @Override
    public String host() {
        return delegate.host();
    }

    @Override
    public String path() {
        return delegate.path();
    }

    @Override
    public List<Lang> acceptLanguages() {
        return delegate.acceptLanguages();
    }

    @Override
    public List<MediaRange> acceptedTypes() {
        return delegate.acceptedTypes();
    }

    @Override
    public boolean accepts(String mimeType) {
        return delegate.accepts(mimeType);
    }

    @Override
    public Map<String, String[]> queryString() {
        return delegate.queryString();
    }

    @Override
    public String getQueryString(String key) {
        return delegate.getQueryString(key);
    }

    @Override
    public Http.Cookies cookies() {
        return delegate.cookies();
    }

    @Override
    public Http.Cookie cookie(String name) {
        return delegate.cookie(name);
    }

    @Override
    public Map<String, String[]> headers() {
        return delegate.headers();
    }

    @Override
    public String getHeader(String headerName) {
        return delegate.getHeader(headerName);
    }

    @Override
    public boolean hasHeader(String headerName) {
        return delegate.hasHeader(headerName);
    }

    @Override
    public Optional<String> contentType() {
        return delegate.contentType();
    }

    @Override
    public Optional<String> charset() {
        return delegate.charset();
    }

    @Override
    public Optional<List<X509Certificate>> clientCertificateChain() {
        return delegate.clientCertificateChain();
    }

    @Override
    public Map<String, String> tags() {
        return delegate.tags();
    }

    @Override
    public RequestHeader _underlyingHeader() {
        return delegate._underlyingHeader();
    }
}
